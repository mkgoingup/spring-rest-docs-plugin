package com.spring.rest.maven.plugin;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 合并spring rest docs生成的adoc文件片断，只支持两级目录，两级以上的目录文件将不会处理
 * 执行阶段在test之前package之后，且配置在asciidoctor-maven-plugin插件之前(同阶段的插件按配置顺序执行)
 */
@Mojo(name="merge",defaultPhase = LifecyclePhase.PREPARE_PACKAGE)
public class DocMergeJob extends AbstractMojo {

    /**
     * 当前工程目录
     */
    @Parameter(defaultValue = "${basedir}")
    private String appDir;

    /**
     * 合并后的adoc配置路径，使用asciidoctor-maven-plugin的默认配置文件读取路径
     */
    @Parameter(defaultValue = "${basedir}/src/main/asciidoc")
    private String outputDirectory;

    /**
     * 需要合并的片断文件目录
     */
    @Parameter(defaultValue = "${basedir}/target/generated-snippets")
    private String sourceDirectory;

    /**
     * 生成api配置文件的名称
     */
    @Parameter
    private String docName = "openApi.adoc";

    /**
     * 标题
     */
    @Parameter
    private String title = "api列表";

    /**
     * 只合并部分片断，逗号分隔
     */
    @Parameter
    private String includeDocFiles;

    /**
     * 过滤掉部分片断,逗号分隔
     */
    @Parameter
    private String excludeDocFiles;

    /**
     * 行分隔符
     */
    private String lineSeparator = "\n";

    /**
     * 默认输出片断
     */
    private static final List<String> docList = new ArrayList<String>();

    /**
     * 输出片断描述
     */
    private static final Map<String,String> docDescMap = new HashMap<String,String>();

    static {
        //通过list保证输出顺序
        docList.add("curl-request.adoc");
        docList.add("http-request.adoc");
        docList.add("httpie-request.adoc");
        docList.add("request-headers.adoc");
        docList.add("request-parameters.adoc");
        docList.add("request-body.adoc");
        docList.add("response-body.adoc");
        docList.add("http-response.adoc");
        docList.add("request-fields.adoc");
        docList.add("response-fields.adoc");

        docDescMap.put("curl-request.adoc",".curl请求示例");
        docDescMap.put("http-request.adoc",".请求url、header及示例");
        docDescMap.put("httpie-request.adoc",".httpie请求示例");
        docDescMap.put("request-headers.adoc",".header说明");
        docDescMap.put("request-parameters.adoc",".请求参数说明");
        docDescMap.put("request-body.adoc",".body说明");
        docDescMap.put("http-response.adoc",".返回内容示例");
        docDescMap.put("request-fields.adoc",".请求字段说明");
        docDescMap.put("response-fields.adoc",".返回值说明");
        docDescMap.put("response-body.adoc",".返回结果示例");

    }

    /**
     * 插件执行时调用方法，合并片断的入口
     * @throws MojoExecutionException
     * @throws MojoFailureException
     */
    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        //检查参数是否合法
        validCheck();
        //content记录所有配置，然后一起写入文件
        StringBuilder content = new StringBuilder();
        //增加左侧目录配置
        addMenu(content);
        //遍历第一级目录（这个方法中会去遍历第二级目录）
        circleDocsDirs(content);
        //将配置写入文件
        writeToFile(content);
    }

    /**
     * 检查参数是否合法
     */
    private void validCheck(){
        if(!docName.endsWith(".adoc")){
            docName += ".adoc";
        }
    }

    /**
     * 增加左侧目录配置
     * @param content
     */
    private void addMenu(StringBuilder content){
        content.append("= "+title+lineSeparator+":toc: left"+lineSeparator+lineSeparator);
    }

    /**
     * 循环遍历第一级目录，如果有二级目录，则会遍历二级目录
     * @param content
     */
    private void circleDocsDirs(StringBuilder content){
        File sourceDir = new File(sourceDirectory);
        if(sourceDir == null){
            getLog().warn("no docs!!!");
            return;
        }
        for(File docDir:sourceDir.listFiles()){
            if(!docDir.isDirectory()){
                continue;
            }
            if(checkHasChildrenDirs(docDir)){
                //如果有二级目录，则遍历二级目录
                circleSubDirs(docDir,content);
            }else{
                //遍历下面的文件，输出include配置
                circleDocs(docDir,content,2);
            }
        }
    }

    /**
     * 遍历二级目录，上级目录会作为一个大标题
     * @param dir
     * @param content
     */
    private void circleSubDirs(File dir,StringBuilder content){
        content.append("== "+dir.getName()+lineSeparator+lineSeparator);

        File[] dirs = dir.listFiles(new FileFilter() {
            @Override
            public boolean accept(File file) {
                return file.isDirectory();
            }
        });
        if(dirs == null){
            return;
        }
        for(File subDir:dirs){
            //遍历下面的文件，输出include配置
            circleDocs(subDir,content,3);
        }
    }

    /**
     * 输出文件include配置，将目录下的adoc片断include到目标配置文件中
     * 上级目录是第一级目录的时候，titleLevel为2
     * 上级目录是第二级目录的时候，titleLevel为3
     * @param dir
     * @param content
     * @param titleLevel
     */
    private void circleDocs(File dir,StringBuilder content,int titleLevel){
        for(int i=0;i<titleLevel;i++){
            content.append("=");
        }
        content.append(" "+dir.getName()+lineSeparator+lineSeparator);
        String path = dir.getAbsolutePath();

        if(includeDocFiles!=null){
            for(String docName:includeDocFiles.split(",")){
                fileAppend(content,path+File.separator+docName,docDescMap.get(docName));
            }
        }else{
            List<String> excludeList = new ArrayList<String>();
            if(excludeDocFiles!=null){
                for(String docName:excludeDocFiles.split(",")){
                    excludeList.add(docName);
                }
            }
            for(String docName:docList){
                //如果是排除的片断则直接跳过
                if(excludeList.contains(docName)){
                    continue;
                }
                fileAppend(content,path+File.separator+docName,docDescMap.get(docName));
            }
        }
    }


    /**
     * 检查目录上否还有子目录
     * @param dir
     * @return
     */
    private boolean checkHasChildrenDirs(File dir){
        File[] dirs = dir.listFiles(new FileFilter() {
            @Override
            public boolean accept(File file) {
                return file.isDirectory();
            }
        });
        return dirs!=null&&dirs.length>0;
    }

    /**
     * 配置写入文件
     * @param content
     */
    private void writeToFile(StringBuilder content){
        File outDir = new File(outputDirectory);
        OutputStreamWriter osw = null;
        try{
            if(!outDir.exists()){
                outDir.mkdirs();
            }
            File file = new File(outDir.getAbsolutePath(),docName);
            if(!file.exists()){
                file.createNewFile();
            }
            osw = new OutputStreamWriter(new FileOutputStream(file),"UTF-8");
            osw.write(content.toString());
            osw.flush();
        }catch (Exception e){
            throw new RuntimeException(e);
        }finally {
            if(osw != null ){
                try {
                    osw.close();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    /**
     * include配置拼装
     * @param content
     * @param include
     * @param title
     */
    private void fileAppend(StringBuilder content,String include,String title){
        File file = new File(include);
        if(file.exists()){
            content.append(title).append(lineSeparator).append("include::").append(include).append("[]").append(lineSeparator);
        }
    }
}

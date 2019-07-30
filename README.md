# spring-rest-docs-plugin
##功能
spring rest docs 的maven 插件,自动合并碎片文档

结合asciidoctor-maven-plugin插件自动生成api的HTML文档，合并的碎片文档包括:

```默认碎片文件
curl-request.adoc
http-request.adoc
request-headers.adoc
request-parameters.adoc
request-body.adoc
http-response.adoc
response-fields.adoc
```
碎片文件目录只支持两级目录，两级以上的目录文件将不会处理，即如果碎片文件目录为target/generated-snippets，
此目录下有type1和type2两个目录，这算第一层目录，type1和type2会标记为一个大的标题分类，
如果它们下面又有目录，那么这些目录作为第二层目录，为具体的接口的文档目录，生成的html文档目录格式如下:
```
    type1
        接口1
        接口2
    type2
        接口3
        接口4
```
<br>maven插件的执行阶段配置在test之前package之后，且在asciidoctor-maven-plugin插件之前(同阶段的插件按配置顺序执行)

##maven插件配置示例：
```插件配置示例
<plugin>
    <groupId>com.spring.rest.maven.plugin</groupId>
    <artifactId>docmerge-maven-plugin</artifactId>
    <version>1.0.0</version>
    <executions>
         <execution>
            <id>merge-docs</id>
            <phase>prepare-package</phase>
            <goals>
               <goal>merge</goal>
            </goals>
         </execution>
    </executions>
</plugin>
```

##配置参数说明:
<br>1.appDir：当前工程目录，默认自动为pom.xml文件所在目录${basedir}
```示例
<execution>
    <configuration>     
        <appDir>${basedir}</appDir>
    </configuration>
    <id>merge-docs</id>
     ......
</execution>

```
<br>2.outputDirectory：合并后的adoc配置文件路径，默认使用${basedir}/src/main/asciidoc
```示例
<execution>
    <configuration>     
        <outputDirectory>${basedir}/src/main/asciidoc</outputDirectory>
    </configuration>
    <id>merge-docs</id>
     ......
</execution>

```
<br>3.sourceDirectory：需要合并的片断文件目录，默认使用${basedir}/target/generated-snippets
```示例
<execution>
    <configuration>     
        <sourceDirectory>${basedir}/target/generated-snippets</sourceDirectory>
    </configuration>
    <id>merge-docs</id>
     ......
</execution>

```
<br>4.docName：生成api配置文件的名称，默认使用openApi.adoc
```示例
<execution>
    <configuration>     
        <docName>openApi.adoc</docName>
    </configuration>
    <id>merge-docs</id>
     ......
</execution>

```
<br>5.title：api标题，默认使用 api列表
```示例
<execution>
    <configuration>     
        <title>api列表</title>
    </configuration>
    <id>merge-docs</id>
     ......
</execution>

```
<br>6.includeDocFiles：只合并部分片断，逗号分隔,如果配置了这一项，那么输出文件中只会有include的这些片断，不能和excludeDocFiles同时配置
```示例
<execution>
    <configuration>     
        <includeDocFiles>curl-request.adoc,http-request.adoc</includeDocFiles>
    </configuration>
    <id>merge-docs</id>
     ......
</execution>

```
<br>7.excludeDocFiles：过滤掉部分片断,逗号分隔,输出的时候，配置中的片断将不会输出

```示例
<execution>
    <configuration>     
        <excludeDocFiles>curl-request.adoc,http-request.adoc</excludeDocFiles>
    </configuration>
    <id>merge-docs</id>
     ......
</execution>

```


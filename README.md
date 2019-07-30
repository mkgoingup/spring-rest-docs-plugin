# spring-rest-docs-plugin
spring rest docs 的maven 插件,自动合并碎片文档

结合asciidoctor-maven-plugin插件自动生成api的HTML文档

配置示例：
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

配置参数说明:
<br>1.appDir：当前工程目录，默认自动为pom.xml文件所在目录${basedir}
···示例
<execution>
    <configuration>     
        <appDir>${basedir}</appDir>
    </configuration>
    <id>merge-docs</id>
     ......
</execution>

···
<br>outputDirectory：合并后的adoc配置文件路径，默认使用${basedir}/src/main/asciidoc
<br>sourceDirectory：需要合并的片断文件目录，默认使用${basedir}/target/generated-snippets
<br>docName：生成api配置文件的名称，默认使用openApi.adoc
<br>title：api标题，默认使用api列表
<br>includeDocFiles：只合并部分片断，逗号分隔
<br>includeDocFiles：过滤掉部分片断,逗号分隔



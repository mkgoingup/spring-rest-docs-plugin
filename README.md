# spring-rest-docs-plugin
spring rest docs 的maven 插件,自动合并碎片文档

结合asciidoctor-maven-plugin插件自动生成api的HTML文档

配置示例：
'''插件配置示例
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
'''

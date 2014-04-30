**config-generation-maven-plugin**

Generates config and scripts for multiple target environments using
template placeholder substitution from values in multiple filter files.

*Dependency:*
```
<dependency>
  <groupId>com.ariht</groupId>
  <artifactId>config-generation-maven-plugin</artifactId>
  <version>0.9.5</version>
</dependency>
```
*Example configuration:*
```
<build>
    <plugins>
        <plugin>
            <groupId>com.ariht</groupId>
            <artifactId>config-generation-maven-plugin</artifactId>
            <version>0.9.5</version>
            <executions>
                <execution>
                    <goals>
                        <goal>process</goal>
                    </goals>
                </execution>
            </executions>

            <!-- There are reasonable defaults so configuration is entirely optional -->
            <configuration>
                <encoding>${project.build.sourceEncoding}</encoding>
                <outputBasePath>${basedir}/target/generated-config</outputBasePath>

                <filtersBasePath>${basedir}/src/config/filters</filtersBasePath>
                <filtersToIgnore>
                    <ignore>${basedir}/src/config/filters/readme.txt</ignore>
                    <ignore>${basedir}/src/config/filters/subdir</ignore>
                </filtersToIgnore>

                <templatesBasePath>${basedir}/src/config/templates</templatesBasePath>
                <templatesToIgnore>
                    <ignore>${basedir}/src/config/templates/readme.txt</ignore>
                </templatesToIgnore>
            </configuration>
        </plugin>
    </plugins>
</build>
```


*For example the following inputs:*
```
/src/main/config/filter/dev.filter
/src/main/config/filter/uat.filter
/src/main/config/filter/prod.filter
/src/main/config/template/ci_deploy.sh
/src/main/config/template/properties/db_connection_properties.sh
/src/main/config/template/properties/webapp_properties.sh
```
*outputs:*
```
/target/generated-config/dev/ci_deploy.sh
/target/generated-config/dev/properties/db_connection_properties.sh
/target/generated-config/dev/properties/webapp_properties.sh

/target/generated-config/uat/ci_deploy.sh
/target/generated-config/uat/properties/db_connection_properties.sh
/target/generated-config/uat/properties/webapp_properties.sh

/target/generated-config/prod/ci_deploy.sh
/target/generated-config/prod/properties/db_connection_properties.sh
/target/generated-config/prod/properties/webapp_properties.sh
```

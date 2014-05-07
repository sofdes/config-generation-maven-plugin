**config-generation-maven-plugin**

Generates config, properties, scripts and any other text resources from templates
with ${placeholder} substitution to target multiple environments.

Attached to the generate-resources phase if configured with build, but can equally
be run with different configuration in different profiles.

*Example configuration:*
```
<build>
    <plugins>
        <plugin>
            <groupId>com.ariht</groupId>
            <artifactId>config-generation-maven-plugin</artifactId>
            <version>0.9.8</version>
            <executions>
                <execution>
                    <goals>
                        <goal>generate/goal>
                    </goals>
                </execution>
            </executions>

            <!-- There are reasonable defaults so configuration is entirely optional -->
            <configuration>
                <encoding>${project.build.sourceEncoding}</encoding>
                <outputBasePath>${basedir}/target/generated-config</outputBasePath>

                <!-- Create a filter for each environment, sub-directories walked and paths kept consistent when output -->
                <filtersBasePath>${basedir}/src/config/filters</filtersBasePath>
                <filtersToIgnore>
                    <ignore>${basedir}/src/config/filters/old_uat</ignore>
                </filtersToIgnore>

                <!-- One template for *all* environments, sub-directories walked and paths kept consistent when output -->
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

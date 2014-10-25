/*
 * Copyright 2014 Software Design Studio Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.ariht.maven.plugins.config;

import com.ariht.maven.plugins.config.generator.ConfigGeneratorImpl;
import com.ariht.maven.plugins.config.parameters.ConfigGeneratorParameters;
import com.ariht.maven.plugins.config.parameters.ConfigGeneratorParametersBuilder;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

import java.util.List;

/**
 * Generates config and scripts for multiple target environments using
 * template placeholder substitution from values in multiple filter io.
 */
@Mojo(name = "generate", defaultPhase = LifecyclePhase.GENERATE_RESOURCES, requiresDirectInvocation = false)
public class ConfigGenerationMojo extends AbstractMojo {

    @Parameter (defaultValue = "${project.build.sourceEncoding}")
    protected String encoding;
    @Parameter (defaultValue = "${basedir}/src/config/templates")
    protected String templatesBasePath;
    @Parameter (defaultValue = "${basedir}/src/config/filters")
    protected String filtersBasePath;
    @Parameter
    protected List<String> externalFilterBasePaths;
    @Parameter (defaultValue = "${basedir}/target/generated-config")
    protected String outputBasePath;
    @Parameter (defaultValue = "true")
    protected boolean logOutput;
    @Parameter
    protected List<String> templatesToIgnore;
    @Parameter
    protected List<String> filtersToIgnore;
    @Parameter (defaultValue = "filter.source")
    protected String filterSourcePropertyName;
    @Parameter (defaultValue = "${")
    protected String propertyPrefix;
    @Parameter (defaultValue = "}")
    protected String propertySuffix;
    @Parameter (defaultValue = "true")
    protected boolean failOnMissingProperty;

    public void execute() throws MojoExecutionException, MojoFailureException {
        final Log log = getLog();

        // Copy parameters in POM to object
        final ConfigGeneratorParameters parameters = new ConfigGeneratorParametersBuilder(log)
                                                        .withEncoding(encoding)
                                                        .withTemplatesBasePath(templatesBasePath)
                                                        .withFiltersBasePath(filtersBasePath)
                                                        .withExternalFilterBasePaths(externalFilterBasePaths)
                                                        .withOutputBasePath(outputBasePath)
                                                        .isWithLogOutput(logOutput)
                                                        .withTemplatesToIgnore(templatesToIgnore)
                                                        .withFiltersToIgnore(filtersToIgnore)
                                                        .withFilterSourcePropertyName(filterSourcePropertyName)
                                                        .withPropertyPrefix(propertyPrefix)
                                                        .withPropertySuffix(propertySuffix)
                                                        .isWithFailOnMissingProperty(failOnMissingProperty)
                                                        .build();

        new ConfigGeneratorImpl(log, parameters).processFiltersIntoTemplates();
    }

}

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

import org.apache.commons.configuration.ConfigurationConverter;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.text.StrSubstitutor;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Properties;

/**
 * Generates config and scripts for multiple target environments using
 * template placeholder substitution from values in multiple filter files.
 */
@Mojo(name = "process", defaultPhase = LifecyclePhase.GENERATE_RESOURCES, requiresDirectInvocation = false)
public class ConfigGenerationMojo extends AbstractMojo {

    @Parameter (defaultValue = "${project.build.sourceEncoding}")
    protected String encoding;
    @Parameter (defaultValue = "${basedir}/src/config/templates")
    protected String templatesBasePath;
    @Parameter (defaultValue = "${basedir}/src/config/filters")
    protected String filtersBasePath;
    @Parameter (defaultValue = "${basedir}/target/generated-config")
    protected String outputBasePath;
    @Parameter (defaultValue = "true")
    protected boolean logOutput;
    @Parameter
    protected List<String> templatesToIgnore;
    @Parameter
    protected List<String> filtersToIgnore;

    private static final String PATH_SEPARATOR = "/";

    /**
     * For properties substituted from every filter, create config based on each template.
     */
    public void execute() throws MojoExecutionException, MojoFailureException {
        logConfigurationParameters();
        clearTargetDirectory();
        try {
            final DirectoryReader directoryReader = new DirectoryReader(getLog(), PATH_SEPARATOR);
            processTemplatesAndGenerateConfig(directoryReader);
        } catch (Exception e) {
            getLog().error("Error generating config: " + String.valueOf(e.getMessage()));
            throw new MojoFailureException(e.getMessage(), e);
        }
    }

    private void clearTargetDirectory() throws MojoFailureException {
        try {
            deleteOutputDirectory();
        } catch (Exception e) {
            getLog().error("Error while clearing config generation output directory: " + String.valueOf(e.getMessage()));
            throw new MojoFailureException(e.getMessage(), e);
        }
    }

    private void processTemplatesAndGenerateConfig(final DirectoryReader directoryReader) throws Exception {
        final List<FileInfo> filters = directoryReader.readFiles(filtersBasePath, filtersToIgnore);
        final List<FileInfo> templates = directoryReader.readFiles(templatesBasePath, templatesToIgnore);
        for (final FileInfo filter : filters) {
            getLog().info("");
            for (final FileInfo template : templates) {
                generateConfig(template, filter, outputBasePath);
            }
        }
    }

    /**
     * Read properties from filter file and substitute template place-holders.
     * Write results to output path with same relative path as input filters.
     *
     * Typical output is to .../filter-dir/filter-name-no-extension/template-dir/template.name
     */
    private void generateConfig(final FileInfo template, final FileInfo filter, final String outputBasePath) throws IOException, ConfigurationException {
        final String outputDirectory = createOutputDirectory(template, filter, outputBasePath);
        final String templateFilename = template.getFile().getName();
        final String outputFilename = FilenameUtils.separatorsToSystem(outputDirectory + templateFilename);
        if (logOutput) {
            getLog().info("Generating : " + String.valueOf(outputFilename));
        } else if (getLog().isDebugEnabled()) {
            getLog().debug("Generating : " + String.valueOf(outputFilename));
        }
        getLog().debug("Applying filter : " + filter.toString() + " to template : " + template.toString());
        final String rawTemplate = FileUtils.readFileToString(template.getFile());
        final Properties properties = readFilterIntoProperties(filter);
        final String processedTemplate = StrSubstitutor.replace(rawTemplate, properties);

        if (StringUtils.isNotBlank(encoding)) {
            FileUtils.writeStringToFile(new File(outputFilename), processedTemplate, encoding);
        } else {
            FileUtils.writeStringToFile(new File(outputFilename), processedTemplate);
        }
    }

    /**
     * Filter files contain the properties we wish to substitute in templates.
     *
     * Uses Apache Commons Configuration to load filters.
     */
    private Properties readFilterIntoProperties(final FileInfo filter) throws ConfigurationException {
        final PropertiesConfiguration config = new PropertiesConfiguration(filter.getFile());
        config.setEncoding(encoding);
        // Add one more property:   filter.source=/relative/sub/dir/filenameNoExtension
        final String filterSource = filter.getRelativeSubDirectory() + filter.getNameWithoutExtension();
        config.setProperty("filter.source", FilenameUtils.separatorsToUnix(filterSource));
        return ConfigurationConverter.getProperties(config);
    }

    /**
     * Prepare output directory: base-path/filter-sub-dir/template-dir/template.name
     */
    private String createOutputDirectory(final FileInfo template, final FileInfo filter, final String outputBasePath) throws IOException {
        final String outputDirectory = getOutputPath(template, filter, outputBasePath);
        final File outputDir = new File(outputDirectory);
        if (!outputDir.exists()) {
            getLog().debug("Creating : " + outputDir);
            FileUtils.forceMkdir(outputDir);
        }
        return FilenameUtils.normalize(outputDirectory);
    }

    /**
     * Concatenate together the filter's directory with the template's - 'deploy' templates just go into the
     * base path so only have the filter (i.e. the environment they are intended for).
     */
    private String getOutputPath(final FileInfo template, final FileInfo filter, final String outputBasePath) {
        final String outputPath = outputBasePath + PATH_SEPARATOR
                                + filter.getRelativeSubDirectory()
                                + filter.getNameWithoutExtension() + PATH_SEPARATOR
                                + template.getRelativeSubDirectory() + PATH_SEPARATOR;
        return FilenameUtils.normalize(outputPath);
    }

    private void deleteOutputDirectory() throws IOException {
        final File outputDir = new File(outputBasePath);
        if (outputDir.exists()) {
            getLog().debug("Deleting : " + outputDir);
            FileUtils.forceDelete(outputDir);
        }
    }

    private void logConfigurationParameters() {
        if (StringUtils.isBlank(encoding)) {
            encoding = System.getProperty("file.encoding");
            getLog().warn("File encoding has not been set, using platform encoding '" + encoding
                    + "', i.e. generated config is platform dependent!");
        } else if (logOutput) {
            getLog().info("Using file encoding '" + encoding + "' while generating config.");
        }
        if (logOutput) {
            getLog().info("templatesBasePath : " + FilenameUtils.separatorsToSystem(templatesBasePath));
            getLog().info("filtersBasePath   : " + FilenameUtils.separatorsToSystem(filtersBasePath));
            getLog().info("outputBasePath    : " + FilenameUtils.separatorsToSystem(outputBasePath));
        }
    }
}

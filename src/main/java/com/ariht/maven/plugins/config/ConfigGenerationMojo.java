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

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
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
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Generates config and scripts for multiple target environments using
 * template placeholder substitution from values in multiple filter files.
 */
@Mojo(name = "generate", defaultPhase = LifecyclePhase.GENERATE_RESOURCES, requiresDirectInvocation = false)
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
    @Parameter (defaultValue = "filter.source")
    protected String filterSourcePropertyName;
    @Parameter (defaultValue = "${")
    protected String propertyPrefix;
    @Parameter (defaultValue = "}")
    protected String propertySuffix;
    @Parameter (defaultValue = "true")
    protected boolean failOnMissingProperty;

    private static final String PATH_SEPARATOR = "/";

    private static final String MISSING_PROPERTY_PREFIX = "MISSING_PROPERTY_START: ";
    private static final String MISSING_PROPERTY_SUFFIX = " MISSING_PROPERTY_END";
    private static final String MISSING_PROPERTY_PATTERN = "(?<=" + MISSING_PROPERTY_PREFIX + ").*?(?=" + MISSING_PROPERTY_SUFFIX + ")";

    private static final Pattern pattern = Pattern.compile(MISSING_PROPERTY_PATTERN);

    /**
     * Clear target directory and create new scripts and config files.
     */
    public void execute() throws MojoExecutionException, MojoFailureException {
        logConfigurationParameters();
        clearTargetDirectory();
        try {
            processTemplatesAndGenerateConfig();
        } catch (Exception e) {
            getLog().error("Error generating config: " + String.valueOf(e.getMessage()));
            throw new MojoFailureException(e.getMessage(), e);
        }
    }

    /**
     * Merge templates with filters to generate config, scripts and property files.
     */
    private void processTemplatesAndGenerateConfig() throws Exception {
        final DirectoryReader directoryReader = new DirectoryReader(getLog());
        final List<FileInfo> filters = directoryReader.readFiles(filtersBasePath, filtersToIgnore);
        final List<FileInfo> templates = directoryReader.readFiles(templatesBasePath, templatesToIgnore);
        logOutputPath();

        // Get list of all properties in all filter files.
        final Set<String> allProperties = getAllProperties(filters);
        // Collection stores missing properties by file so this can be logged once at the end.
        final Map<String, Set<String>> missingPropertiesByFilename = new LinkedHashMap<String, Set<String>>();

        for (final FileInfo filter : filters) {
            final Properties properties = readFilterIntoProperties(filter);
            final LinkedHashMap<String, String> valueMap = Maps.newLinkedHashMap(Maps.fromProperties(properties));

            // No point checking for missing properties if all were found in the filter file
            boolean missingPropertyFound = false;
            for (String missingProperty : Sets.difference(allProperties, valueMap.keySet()).immutableCopy()) {
                valueMap.put(missingProperty, MISSING_PROPERTY_PREFIX + missingProperty + MISSING_PROPERTY_SUFFIX);
                missingPropertyFound = true;
            }
            final StrSubstitutor strSubstitutor = new StrSubstitutor(valueMap, propertyPrefix, propertySuffix);
            for (final FileInfo template : templates) {
                generateConfig(template, filter, outputBasePath, strSubstitutor, missingPropertiesByFilename, missingPropertyFound);
            }
        }

        if (!missingPropertiesByFilename.keySet().isEmpty()) {
            final StringBuilder sb = new StringBuilder("Missing properties identified:\n");
            for (String filename : missingPropertiesByFilename.keySet()) {
                sb.append(filename).append(": ");
                sb.append(StringUtils.join(missingPropertiesByFilename.get(filename), ", ")).append("\n");
            }
            getLog().warn(sb.toString());
            if (failOnMissingProperty) {
                throw new MojoExecutionException(sb.toString());
            }
        }
    }

    /**
     * Read properties from filter file and substitute template place-holders.
     *
     * Typical output is to .../filter-dir/filter-name-no-extension/template-dir/template.name
     */
    private void generateConfig(final FileInfo template,
                                final FileInfo filter,
                                final String outputBasePath,
                                final StrSubstitutor strSubstitutor,
                                final Map<String, Set<String>> missingPropertiesByFilename,
                                final boolean missingPropertyFound) throws IOException, ConfigurationException, MojoFailureException {

        final String outputDirectory = createOutputDirectory(template, filter, outputBasePath);
        final String templateFilename = template.getFile().getName();
        final String outputFilename = FilenameUtils.separatorsToUnix(outputDirectory + templateFilename);

        if (logOutput) {
            getLog().info("Creating : " + StringUtils.replace(outputFilename, outputBasePath, ""));
        } else if (getLog().isDebugEnabled()) {
            getLog().debug("Creating : " + String.valueOf(outputFilename));
        }
        getLog().debug("Applying filter : " + filter.toString() + " to template : " + template.toString());

        final String rawTemplate = FileUtils.readFileToString(template.getFile());
        final String processedTemplate = strSubstitutor.replace(rawTemplate);

        // No point in running regex against long strings if properties are all present
        if (missingPropertyFound) {
            checkForMissingProperties(filter.getFile().getAbsolutePath(), processedTemplate, missingPropertiesByFilename);
        }

        // Only write out the generated files if there were no errors or errors are specifically ignored
        if (StringUtils.isNotBlank(encoding)) {
            FileUtils.writeStringToFile(new File(outputFilename), processedTemplate, encoding);
        } else {
            FileUtils.writeStringToFile(new File(outputFilename), processedTemplate);
        }
    }

    /**
     * Check if there are any properties that haven't been substituted and add to map for logging out later.
     */
    private void checkForMissingProperties(final String filename,
                                           final String processedTemplate,
                                           final Map<String, Set<String>> missingPropertiesByFilename) throws MojoFailureException {
        final Matcher matcher = pattern.matcher(processedTemplate);
        Set<String> missingProperties = null;
        while(matcher.find()) {
            final String propertyName = matcher.group();
            if (StringUtils.isBlank(propertyName)) {
                continue;
            } else if (missingProperties == null) {
                missingProperties = new LinkedHashSet<String>();
            }
            missingProperties.add(propertyName);
        }

        if (missingProperties != null) {
            for (final String propertyName : missingProperties) {
                Set<String> missingPropertiesFromMap = missingPropertiesByFilename.get(filename);
                if (missingPropertiesFromMap == null) {
                    missingPropertiesFromMap = new LinkedHashSet<String>();
                    missingPropertiesByFilename.put(filename, missingPropertiesFromMap);
                }
                missingPropertiesFromMap.add(propertyName);
                getLog().info(filename + " : " + propertyName);
            }
        }
    }

    /**
     * Compile list of every property in all filter files - used to provide dummy values
     * in missing properties identified in set difference check.
     */
    private Set<String> getAllProperties(List<FileInfo> filters) throws ConfigurationException {
        final Set<String> allProperties = new LinkedHashSet<String>();
        for (final FileInfo filter : filters) {
            final Properties properties = readFilterIntoProperties(filter);
            final ImmutableMap<String, String> valueMap = Maps.fromProperties(properties);
            allProperties.addAll(valueMap.keySet());
        }
        return allProperties;
    }

    /**
     * Filter files contain the properties we wish to substitute in templates.
     *
     * Uses Apache Commons Configuration to load filters.
     */
    private Properties readFilterIntoProperties(final FileInfo filter) throws ConfigurationException {
        final PropertiesConfiguration config = new PropertiesConfiguration(filter.getFile());
        config.setEncoding(encoding);
        if (StringUtils.isNotBlank(filterSourcePropertyName)) {
            final String filterSource = filter.getRelativeSubDirectory() + filter.getNameWithoutExtension();
            config.setProperty(filterSourcePropertyName, FilenameUtils.separatorsToUnix(filterSource));
        }
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
     * Concatenate filter directory with template directory
     */
    private String getOutputPath(final FileInfo template, final FileInfo filter, final String outputBasePath) {
        final String outputPath = outputBasePath + PATH_SEPARATOR
                                + filter.getRelativeSubDirectory() + PATH_SEPARATOR
                                + filter.getNameWithoutExtension() + PATH_SEPARATOR
                                + template.getRelativeSubDirectory() + PATH_SEPARATOR;
        return FilenameUtils.separatorsToUnix(FilenameUtils.normalize(outputPath));
    }

    /**
     * Clear contents of config generation build target/output directory ready for new files.
     */
    private void clearTargetDirectory() throws MojoFailureException {
        final File outputDir = new File(outputBasePath);
        if (outputDir.exists()) {
            getLog().debug("Deleting : " + outputDir);
            try {
                FileUtils.forceDelete(outputDir);
            } catch(IOException e) {
                getLog().error("Error while clearing config generation output directory: ["
                        + outputDir + "], error was: " + String.valueOf(e.getMessage()));
                throw new MojoFailureException(e.getMessage(), e);
            }
        }
    }

    private void logConfigurationParameters() {
        if (StringUtils.isBlank(encoding)) {
            encoding = System.getProperty("file.encoding");
            getLog().warn("File encoding has not been set, using platform encoding '" + encoding
                    + "', i.e. generated config is platform dependent!");
        } else if (logOutput) {
            getLog().debug("Using file encoding '" + encoding + "' while generating config.");
        }
        if (logOutput) {
            getLog().debug("Templates path : " + FilenameUtils.separatorsToUnix(templatesBasePath));
            getLog().debug("Filters path   : " + FilenameUtils.separatorsToUnix(filtersBasePath));
            getLog().debug("Output path    : " + FilenameUtils.separatorsToUnix(outputBasePath));
        }
    }

    private void logOutputPath() {
        final String outputPathMessage = "Config generation to: " + FilenameUtils.separatorsToUnix(outputBasePath);
        if (logOutput) {
            getLog().info(outputPathMessage);
        } else if (getLog().isDebugEnabled()) {
            getLog().debug(outputPathMessage);
        }
    }

}

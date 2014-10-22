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

package com.ariht.maven.plugins.config.generator;

import com.ariht.maven.plugins.config.io.DirectoryDeleter;
import com.ariht.maven.plugins.config.io.DirectoryReader;
import com.ariht.maven.plugins.config.io.FileInfo;
import com.ariht.maven.plugins.config.io.FilterPropertiesReader;
import com.ariht.maven.plugins.config.io.OutputDirectoryCreator;
import com.ariht.maven.plugins.config.parameters.ConfigGeneratorParameters;
import com.ariht.maven.plugins.config.parameters.ConfigGeneratorParametersUtils;
import com.google.common.base.Preconditions;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.text.StrSubstitutor;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugin.logging.Log;

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

public class ConfigGeneratorImpl {

    private final Log log;
    private final ConfigGeneratorParameters configGeneratorParameters;
    private final Pattern missingPropertyPattern = Pattern.compile(MISSING_PROPERTY_PATTERN);

    protected static final String MISSING_PROPERTY_PREFIX = "<<<<<<< ";
    protected static final String MISSING_PROPERTY_SUFFIX = " >>>>>>>";
    protected static final String MISSING_PROPERTY_PATTERN = "(?<=" + MISSING_PROPERTY_PREFIX + ").*?(?=" + MISSING_PROPERTY_SUFFIX + ")";

    public ConfigGeneratorImpl(final Log log, final ConfigGeneratorParameters configGeneratorParameters) {
        Preconditions.checkNotNull(log);
        Preconditions.checkNotNull(configGeneratorParameters);
        this.log = log;
        this.configGeneratorParameters = configGeneratorParameters;
        new ConfigGeneratorParametersUtils(log, configGeneratorParameters).logConfigurationParameters();
    }

    /**
     * Clear target directory and create new scripts and config files.
     */
    public void processFiltersIntoTemplates() throws MojoExecutionException, MojoFailureException {
        new DirectoryDeleter(log).clearTargetDirectory(configGeneratorParameters);
        try {
            processTemplatesAndGenerateConfig();
        } catch (Exception e) {
            log.error("Error generating config: " + String.valueOf(e.getMessage()));
            throw new MojoFailureException(e.getMessage(), e);
        }
    }

    /**
     * Merge templates with filters to generate config, scripts and property io.
     */
    private void processTemplatesAndGenerateConfig() throws Exception {
        final DirectoryReader directoryReader = new DirectoryReader(log);
        final List<FileInfo> filters = directoryReader.readFilters(configGeneratorParameters);
        final List<FileInfo> templates = directoryReader.readTemplates(configGeneratorParameters);

        // Get list of all properties in all filter io.
        final FilterPropertiesReader filterPropertiesReader = new FilterPropertiesReader(configGeneratorParameters);
        final Set<String> allProperties = filterPropertiesReader.getAllProperties(filters);
        // Collection stores missing properties by file so this can be logged once at the end.
        final Map<String, Set<String>> missingPropertiesByFilename = new LinkedHashMap<String, Set<String>>();

        for (final FileInfo filter : filters) {
            final Properties properties = filterPropertiesReader.readFilterIntoProperties(filter);
            final LinkedHashMap<String, String> valueMap = Maps.newLinkedHashMap(Maps.fromProperties(properties));

            // No point checking for missing properties if all were found in the filter file
            boolean missingPropertyFound = false;
            for (String missingProperty : Sets.difference(allProperties, valueMap.keySet()).immutableCopy()) {
                valueMap.put(missingProperty, MISSING_PROPERTY_PREFIX + missingProperty + MISSING_PROPERTY_SUFFIX);
                missingPropertyFound = true;
            }
            final StrSubstitutor strSubstitutor = new StrSubstitutor(valueMap, configGeneratorParameters.getPropertyPrefix(), configGeneratorParameters.getPropertySuffix());
            for (final FileInfo template : templates) {
                generateConfig(template, filter, configGeneratorParameters.getOutputBasePath(), strSubstitutor, missingPropertiesByFilename, missingPropertyFound);
            }
        }
        logMissingProperties(missingPropertiesByFilename);
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

        final String outputDirectory = new OutputDirectoryCreator(log).createOutputDirectory(template, filter, outputBasePath);
        final String templateFilename = template.getFile().getName();
        final String outputFilename = FilenameUtils.separatorsToUnix(outputDirectory + templateFilename);

        if (configGeneratorParameters.isLogOutput()) {
            log.info("Creating : " + StringUtils.replace(outputFilename, outputBasePath, ""));
        } else if (log.isDebugEnabled()) {
            log.debug("Creating : " + String.valueOf(outputFilename));
        }
        log.debug("Applying filter : " + filter.toString() + " to template : " + template.toString());

        final String rawTemplate = FileUtils.readFileToString(template.getFile());
        final String processedTemplate = strSubstitutor.replace(rawTemplate);

        // No point in running regex against long strings if properties are all present
        if (missingPropertyFound) {
            checkForMissingProperties(filter.getFile().getAbsolutePath(), processedTemplate, missingPropertiesByFilename);
        }

        // Only write out the generated io if there were no errors or errors are specifically ignored
        if (StringUtils.isNotBlank(configGeneratorParameters.getEncoding())) {
            FileUtils.writeStringToFile(new File(outputFilename), processedTemplate, configGeneratorParameters.getEncoding());
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

        final Matcher matcher = missingPropertyPattern.matcher(processedTemplate);
        final Set<String> missingProperties = new LinkedHashSet<String>();
        while(matcher.find()) {
            final String propertyName = matcher.group();
            if (!StringUtils.isBlank(propertyName)) {
                missingProperties.add(propertyName);
            }
        }
        if (missingProperties.isEmpty()) {
            return;
        }
        for (final String propertyName : missingProperties) {
            Set<String> missingPropertiesFromMap = missingPropertiesByFilename.get(filename);
            if (missingPropertiesFromMap == null) {
                missingPropertiesFromMap = new LinkedHashSet<String>();
                missingPropertiesByFilename.put(filename, missingPropertiesFromMap);
            }
            missingPropertiesFromMap.add(propertyName);
            log.info(filename + " : " + propertyName);
        }
    }

    private void logMissingProperties(final Map<String, Set<String>> missingPropertiesByFilename) throws MojoExecutionException {
        if (!missingPropertiesByFilename.keySet().isEmpty()) {
            final StringBuilder sb = new StringBuilder("Missing properties identified:\n");
            for (String filename : missingPropertiesByFilename.keySet()) {
                sb.append(filename).append(": ");
                sb.append(StringUtils.join(missingPropertiesByFilename.get(filename), ", ")).append("\n");
            }
            log.warn(sb.toString());
            if (configGeneratorParameters.isFailOnMissingProperty()) {
                throw new MojoExecutionException(sb.toString());
            }
        }
    }
}

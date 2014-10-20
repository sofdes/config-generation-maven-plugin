package com.ariht.maven.plugins.config.generator;

import com.ariht.maven.plugins.config.io.FileInfo;
import com.ariht.maven.plugins.config.parameters.ConfigGeneratorParameters;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import org.apache.commons.configuration.CompositeConfiguration;
import org.apache.commons.configuration.ConfigurationConverter;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.commons.lang.StringUtils;

import java.io.File;
import java.io.IOException;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Properties;
import java.util.Set;

/**
 *
 */
public class FilterPropertiesReader {

    private final ConfigGeneratorParameters configGeneratorParameters;

    public FilterPropertiesReader(ConfigGeneratorParameters configGeneratorParameters) {
        this.configGeneratorParameters = configGeneratorParameters;
    }

    /**
     * Compile list of every property in all filter io - used to provide dummy values
     * in missing properties identified in set difference check.
     */
    public Set<String> getAllProperties(List<FileInfo> filters) throws ConfigurationException, IOException {
        final Set<String> allProperties = new LinkedHashSet<String>();
        for (final FileInfo filter : filters) {
            final Properties properties = readFilterIntoProperties(filter);
            final ImmutableMap<String, String> valueMap = Maps.fromProperties(properties);
            allProperties.addAll(valueMap.keySet());
        }
        return allProperties;
    }

    /**
     * Filter io contain the properties we wish to substitute in templates.
     *
     * Uses Apache Commons Configuration to load filters.
     */
    public Properties readFilterIntoProperties(final FileInfo filter) throws ConfigurationException, IOException {
        final CompositeConfiguration composite = new CompositeConfiguration();
        final List<File> files = filter.getFiles();
        for (final File file : files) {
            final PropertiesConfiguration config = new PropertiesConfiguration(file);
            config.setEncoding(configGeneratorParameters.getEncoding());
            composite.addConfiguration(config);
        }
        if (StringUtils.isNotBlank(configGeneratorParameters.getFilterSourcePropertyName())) {
            composite.setProperty(configGeneratorParameters.getFilterSourcePropertyName(), filter.getAllSources());
        }
        return ConfigurationConverter.getProperties(composite);
    }


}

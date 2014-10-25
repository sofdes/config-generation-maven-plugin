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

package com.ariht.maven.plugins.config.io;

import com.ariht.maven.plugins.config.parameters.ConfigGeneratorParameters;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import org.apache.commons.configuration.CompositeConfiguration;
import org.apache.commons.configuration.ConfigurationConverter;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.commons.lang.StringUtils;
import org.apache.maven.plugin.logging.Log;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Properties;
import java.util.Set;

/**
 * Given properties in [property.name=value] format using Commons PropertiesConfiguration to read filter files
 * into Properties instance.
 */
public class FilterPropertyPlaceholderNamesReader {

    private final ConfigGeneratorParameters configGeneratorParameters;
    private final Log log;

    private final Set<String> allFilterPropertyNames = new LinkedHashSet<String>();

    public FilterPropertyPlaceholderNamesReader(final ConfigGeneratorParameters configGeneratorParameters, final Log log) {
        this.log = Preconditions.checkNotNull(log);
        this.configGeneratorParameters = Preconditions.checkNotNull(configGeneratorParameters);
    }

    public Set<String> getAllFilterPropertyNames() {
        return allFilterPropertyNames;
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

        // The 'filter.source' property is used to identify (in the output) where values came from
        if (StringUtils.isNotBlank(configGeneratorParameters.getFilterSourcePropertyName())) {
            composite.setProperty(configGeneratorParameters.getFilterSourcePropertyName(), filter.getAllSources());
        }
        return ConfigurationConverter.getProperties(composite);
    }

    /**
     * Iterate list of all input filter files, compile a list of every property in every filter file.
     *
     * This is then used to provide dummy values in missing properties identified in set difference check.
     */
    public void identifyEveryFilterPropertyPlaceholderName(List<FileInfo> filters) throws ConfigurationException, IOException {
        for (final FileInfo filter : filters) {
            final Properties properties = readFilterIntoProperties(filter);
            final ImmutableMap<String, String> valueMap = Maps.fromProperties(properties);
            allFilterPropertyNames.addAll(valueMap.keySet());
        }
        if (log.isDebugEnabled()) {
            final Object[] propertyNames = allFilterPropertyNames.toArray();
            Arrays.sort(propertyNames);
            log.debug("Filter property placeholder names: " + Arrays.toString(propertyNames));
        }
    }


}

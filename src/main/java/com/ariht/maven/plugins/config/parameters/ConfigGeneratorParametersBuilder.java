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

package com.ariht.maven.plugins.config.parameters;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.maven.plugin.logging.Log;

import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

/**
 * Builds an instance of ConfigGeneratorParameters
 */
public class ConfigGeneratorParametersBuilder {

    private static final List<String> EMPTY_LIST = Collections.unmodifiableList(new LinkedList<String>());

    private final Log log;

    private final ConfigGeneratorParameters configGeneratorParameters;

    public ConfigGeneratorParameters build() {
        if (log.isDebugEnabled()) {
            log.debug("Returning: [" + this.toString() + "]");
        }
        return configGeneratorParameters;
    }

    public ConfigGeneratorParametersBuilder (final Log log) {
        Preconditions.checkArgument(log != null);
        this.log = log;
        configGeneratorParameters = new ConfigGeneratorParameters();
    }

    public ConfigGeneratorParametersBuilder withEncoding(final String encoding) {
        Preconditions.checkArgument(StringUtils.isNotBlank(encoding));
        log.debug("With encoding: [" + encoding + "]");
        configGeneratorParameters.setEncoding(encoding);
        return this;
    }

    public ConfigGeneratorParametersBuilder withTemplatesBasePath(final String templatesBasePath) {
        Preconditions.checkArgument(StringUtils.isNotBlank(templatesBasePath));
        log.debug("With templates base path: [" + templatesBasePath + "]");
        configGeneratorParameters.setTemplatesBasePath(templatesBasePath);
        return this;
    }

    public ConfigGeneratorParametersBuilder withFiltersBasePath(final String filtersBasePath) {
        Preconditions.checkArgument(StringUtils.isNotBlank(filtersBasePath));
        log.debug("With filters base path: [" + filtersBasePath + "]");
        configGeneratorParameters.setFiltersBasePath(filtersBasePath);
        return this;
    }

    public ConfigGeneratorParametersBuilder withOutputBasePath(final String outputBasePath) {
        Preconditions.checkArgument(StringUtils.isNotBlank(outputBasePath));
        log.debug("With output base path: [" + outputBasePath + "]");
        configGeneratorParameters.setOutputBasePath(outputBasePath);
        return this;
    }

    public ConfigGeneratorParametersBuilder isWithLogOutput(final boolean logOutput) {
        log.debug("With log output: [" + logOutput + "]");
        configGeneratorParameters.setLogOutput(logOutput);
        return this;
    }

    public ConfigGeneratorParametersBuilder withTemplatesToIgnore(final List<String> templatesToIgnore) {
        if (log.isDebugEnabled()) {
            log.debug("With templates to ignore: [" + Arrays.toString(templatesToIgnore.toArray()) + "]");
        }
        configGeneratorParameters.setTemplatesToIgnore(deDupeList(templatesToIgnore));
        return this;
    }

    public ConfigGeneratorParametersBuilder withFiltersToIgnore(final List<String> filtersToIgnore) {
        if (log.isDebugEnabled()) {
            log.debug("With filters to ignore: [" + Arrays.toString(filtersToIgnore.toArray()) + "]");
        }
        configGeneratorParameters.setFiltersToIgnore(deDupeList(filtersToIgnore));
        return this;
    }

    public ConfigGeneratorParametersBuilder withFilterSourcePropertyName(final String filterSourcePropertyName) {
        Preconditions.checkArgument(StringUtils.isNotBlank(filterSourcePropertyName));
        log.debug("With filter source property name: [" + filterSourcePropertyName + "]");
        configGeneratorParameters.setFilterSourcePropertyName(filterSourcePropertyName);
        return this;
    }

    public ConfigGeneratorParametersBuilder withPropertyPrefix(final String propertyPrefix) {
        Preconditions.checkArgument(StringUtils.isNotBlank(propertyPrefix));
        log.debug("With property prefix: [" + propertyPrefix + "]");
        configGeneratorParameters.setPropertyPrefix(propertyPrefix);
        return this;
    }

    public ConfigGeneratorParametersBuilder withPropertySuffix(final String propertySuffix) {
        Preconditions.checkArgument(StringUtils.isNotBlank(propertySuffix));
        log.debug("With property suffix: [" + propertySuffix + "]");
        configGeneratorParameters.setPropertySuffix(propertySuffix);
        return this;
    }

    public ConfigGeneratorParametersBuilder isWithFailOnMissingProperty(final boolean failOnMissingProperty) {
        log.debug("With fail on missing property: [" + failOnMissingProperty + "]");
        configGeneratorParameters.setFailOnMissingProperty(failOnMissingProperty);
        return this;
    }

    private List<String> deDupeList(final List<String> inputList) {
        if (inputList == null || inputList.isEmpty()) {
            return EMPTY_LIST;
        }
        final Set<String> outputList = new LinkedHashSet<String>();
        for (final String input : inputList) {
            if (StringUtils.isNotBlank(input)) {
                outputList.add(input);
            }
        }
        return Lists.newArrayList(outputList);
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }
}

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

import org.apache.commons.lang.builder.ToStringBuilder;

import java.util.List;

/**
 * Parameters used by the config generation process.
 */
public class ConfigGeneratorParameters {

    private String encoding = "UTF-8";
    // e.g. /src/config/templates
    private String templatesBasePath;

    // e.g. /src/config/filters
    private String filtersBasePath;
    // For when you need common values across multiple modules
    // Processing order is filtersBasePath then this collection (in order) - first value wins
    // as per the rules in http://commons.apache.org/proper/commons-configuration/userguide/howto_compositeconfiguration.html#Composite_Configuration_Details
    private List<String> externalFilterBasePaths;

    // e.g. /target/generated-config
    private String outputBasePath;
    private boolean logOutput = true;
    private List<String> templatesToIgnore;
    private List<String> filtersToIgnore;
    private String filterSourcePropertyName = "filter.source";
    private String propertyPrefix = "${";
    private String propertySuffix = "}";
    private boolean failOnMissingProperty = true;

    protected ConfigGeneratorParameters() {}

    public String getEncoding() {
        return encoding;
    }

    public void setEncoding(String encoding) {
        this.encoding = encoding;
    }

    public String getTemplatesBasePath() {
        return templatesBasePath;
    }

    public void setTemplatesBasePath(String templatesBasePath) {
        this.templatesBasePath = templatesBasePath;
    }

    public String getFiltersBasePath() {
        return filtersBasePath;
    }

    public void setFiltersBasePath(String filtersBasePath) {
        this.filtersBasePath = filtersBasePath;
    }

    public List<String> getExternalFilterBasePaths() {
        return externalFilterBasePaths;
    }

    public void setExternalFilterBasePaths(List<String> externalFilterBasePaths) {
        this.externalFilterBasePaths = externalFilterBasePaths;
    }

    public String getOutputBasePath() {
        return outputBasePath;
    }

    public void setOutputBasePath(String outputBasePath) {
        this.outputBasePath = outputBasePath;
    }

    public boolean isLogOutput() {
        return logOutput;
    }

    public void setLogOutput(boolean logOutput) {
        this.logOutput = logOutput;
    }

    public List<String> getTemplatesToIgnore() {
        return templatesToIgnore;
    }

    public void setTemplatesToIgnore(List<String> templatesToIgnore) {
        this.templatesToIgnore = templatesToIgnore;
    }

    public List<String> getFiltersToIgnore() {
        return filtersToIgnore;
    }

    public void setFiltersToIgnore(List<String> filtersToIgnore) {
        this.filtersToIgnore = filtersToIgnore;
    }

    public String getFilterSourcePropertyName() {
        return filterSourcePropertyName;
    }

    public void setFilterSourcePropertyName(String filterSourcePropertyName) {
        this.filterSourcePropertyName = filterSourcePropertyName;
    }

    public String getPropertyPrefix() {
        return propertyPrefix;
    }

    public void setPropertyPrefix(String propertyPrefix) {
        this.propertyPrefix = propertyPrefix;
    }

    public String getPropertySuffix() {
        return propertySuffix;
    }

    public void setPropertySuffix(String propertySuffix) {
        this.propertySuffix = propertySuffix;
    }

    public boolean isFailOnMissingProperty() {
        return failOnMissingProperty;
    }

    public void setFailOnMissingProperty(boolean failOnMissingProperty) {
        this.failOnMissingProperty = failOnMissingProperty;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }
}

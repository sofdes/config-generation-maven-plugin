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

import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

public class ConfigGeneratorParametersTest {

    protected static final String DEFAULT_ENCODING_UTF8 = "UTF-8";
    protected static final String FILTER_SOURCE_DEFAULT_PROPERTY_NAME = "filter.source";
    protected static final String DEFAULT_PROPERTY_PREFIX = "${";
    protected static final String DEFAULT_PROPERTY_SUFFIX = "}";

    private static final String DUMMY_TEST_ENCODING = "UTF-99";

    private static final String TEST_FILTERS_BASE_PATH = "/unitTestDummy/src/config/filters";
    public static final String TEST_FILTER_SOURCE_PROPERTY_NAME = "test.filter.property.source.name";
    public static final String TEST_TEMPLATES_BASE_PATH = "/unitTestDummy/src/config/templates";

    @Test
    public void testDefaultState() {
        final ConfigGeneratorParameters parameters = new ConfigGeneratorParameters();
        assertNull(parameters.getFiltersBasePath());
        assertNull(parameters.getFiltersToIgnore());
        assertNull(parameters.getOutputBasePath());
        assertNull(parameters.getTemplatesBasePath());
        assertNull(parameters.getTemplatesToIgnore());
        assertTrue(parameters.isFailOnMissingProperty());
        assertTrue(parameters.isLogOutput());
        assertEquals(DEFAULT_ENCODING_UTF8, parameters.getEncoding());
        assertEquals(FILTER_SOURCE_DEFAULT_PROPERTY_NAME, parameters.getFilterSourcePropertyName());
        assertEquals(DEFAULT_PROPERTY_PREFIX, parameters.getPropertyPrefix());
        assertEquals(DEFAULT_PROPERTY_SUFFIX, parameters.getPropertySuffix());
    }

    @Test
    public void testSettingEncoding() {
        final ConfigGeneratorParameters parameters = new ConfigGeneratorParameters();
        parameters.setEncoding(DUMMY_TEST_ENCODING);
        assertEquals(DUMMY_TEST_ENCODING, parameters.getEncoding());
    }

    @Test
    public void testSettingFiltersBasePath() {
        final ConfigGeneratorParameters configGeneratorParameters = new ConfigGeneratorParameters();
        configGeneratorParameters.setFiltersBasePath(TEST_FILTERS_BASE_PATH);
        assertEquals(TEST_FILTERS_BASE_PATH, configGeneratorParameters.getFiltersBasePath());
    }

    @Test
    public void testSettingFilterSourcePropertyName() {
        final ConfigGeneratorParameters configGeneratorParameters = new ConfigGeneratorParameters();
        configGeneratorParameters.setFilterSourcePropertyName(TEST_FILTER_SOURCE_PROPERTY_NAME);
        assertEquals(TEST_FILTER_SOURCE_PROPERTY_NAME, configGeneratorParameters.getFilterSourcePropertyName());
    }

    @Test
    public void settingTemplatesBaseDirectory() {
        final ConfigGeneratorParameters configGeneratorParameters = new ConfigGeneratorParameters();
        configGeneratorParameters.setTemplatesBasePath(TEST_TEMPLATES_BASE_PATH);
        assertEquals(TEST_TEMPLATES_BASE_PATH, configGeneratorParameters.getTemplatesBasePath());
    }

    @Test
    public void testSettingTemplatesToIgnore() {
        final ConfigGeneratorParameters configGeneratorParameters = new ConfigGeneratorParameters();
        final ArrayList<String> templatesToIgnore = new ArrayList<String>();
        templatesToIgnore.add("/src/config/templates/README.md");
        configGeneratorParameters.setTemplatesToIgnore(templatesToIgnore);
        assertEquals(templatesToIgnore.size(), configGeneratorParameters.getTemplatesToIgnore().size());
        assertTrue(Arrays.equals(templatesToIgnore.toArray(), configGeneratorParameters.getTemplatesToIgnore().toArray()));
        assertSame(templatesToIgnore, configGeneratorParameters.getTemplatesToIgnore());
    }

    @Test
    public void testSettingFiltersToIgnore() {
        final ConfigGeneratorParameters configGeneratorParameters = new ConfigGeneratorParameters();
        final ArrayList<String> filtersToIgnore = new ArrayList<String>();
        filtersToIgnore.add("/src/config/filters/README.md");
        configGeneratorParameters.setFiltersToIgnore(filtersToIgnore);
        assertEquals(filtersToIgnore.size(), configGeneratorParameters.getFiltersToIgnore().size());
        assertTrue(Arrays.equals(filtersToIgnore.toArray(), configGeneratorParameters.getFiltersToIgnore().toArray()));
        assertSame(filtersToIgnore, configGeneratorParameters.getFiltersToIgnore());
    }

    @Test
    public void testSettingLogOutputFlag() {
        final ConfigGeneratorParameters configGeneratorParameters = new ConfigGeneratorParameters();
        assertTrue(configGeneratorParameters.isLogOutput());
        configGeneratorParameters.setLogOutput(false);
        assertFalse(configGeneratorParameters.isLogOutput());
    }

    @Test
    public void testSomething() {
        final ConfigGeneratorParameters configGeneratorParameters = new ConfigGeneratorParameters();
        assertTrue(configGeneratorParameters.isFailOnMissingProperty());
        configGeneratorParameters.setFailOnMissingProperty(false);
        assertFalse(configGeneratorParameters.isFailOnMissingProperty());
    }

}

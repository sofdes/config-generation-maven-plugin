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

import com.ariht.maven.plugins.config.TestsLogger;
import org.apache.maven.plugin.logging.Log;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

public class ConfigGeneratorParametersBuilderTest {


    private Log getLogger() {
        return new TestsLogger();
    }

    @Test(expected = IllegalArgumentException.class)
    public void testInstantiatedWithNullLoggerThrowsException() {
        new ConfigGeneratorParametersBuilder(null);
    }

    @Test
    public void testInstantiatedWithDefaultParameters() {
        final ConfigGeneratorParametersBuilder configGeneratorParametersBuilder = new ConfigGeneratorParametersBuilder(getLogger());
        final ConfigGeneratorParameters parameters = configGeneratorParametersBuilder.build();
        assertNull(parameters.getFiltersBasePath());
        assertNull(parameters.getFiltersToIgnore());
        assertNull(parameters.getOutputBasePath());
        assertNull(parameters.getTemplatesBasePath());
        assertNull(parameters.getTemplatesToIgnore());
        assertTrue(parameters.isFailOnMissingProperty());
        assertTrue(parameters.isLogOutput());
        assertEquals(ConfigGeneratorParametersTest.DEFAULT_ENCODING_UTF8, parameters.getEncoding());
        assertEquals(ConfigGeneratorParametersTest.FILTER_SOURCE_DEFAULT_PROPERTY_NAME, parameters.getFilterSourcePropertyName());
        assertEquals(ConfigGeneratorParametersTest.DEFAULT_PROPERTY_PREFIX, parameters.getPropertyPrefix());
        assertEquals(ConfigGeneratorParametersTest.DEFAULT_PROPERTY_SUFFIX, parameters.getPropertySuffix());
    }

}

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

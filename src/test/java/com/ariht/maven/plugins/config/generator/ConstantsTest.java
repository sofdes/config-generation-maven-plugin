package com.ariht.maven.plugins.config.generator;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class ConstantsTest {

    protected static final String PATH_SEPARATOR = "/";
    protected static final String MISSING_PROPERTY_PREFIX = "<<<<<<< ";
    protected static final String MISSING_PROPERTY_SUFFIX = " >>>>>>>";
    protected static final String MISSING_PROPERTY_PATTERN = "(?<=" + MISSING_PROPERTY_PREFIX + ").*?(?=" + MISSING_PROPERTY_SUFFIX + ")";

    @Test
    public void testConstants() {
        assertEquals(PATH_SEPARATOR, Constants.PATH_SEPARATOR);
        assertEquals(MISSING_PROPERTY_PREFIX, Constants.MISSING_PROPERTY_PREFIX);
        assertEquals(MISSING_PROPERTY_SUFFIX, Constants.MISSING_PROPERTY_SUFFIX);
        assertEquals(MISSING_PROPERTY_PATTERN, Constants.MISSING_PROPERTY_PATTERN);
    }

}

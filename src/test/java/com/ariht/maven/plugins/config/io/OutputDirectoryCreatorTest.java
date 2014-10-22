package com.ariht.maven.plugins.config.io;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class OutputDirectoryCreatorTest {

    private static final String PATH_SEPARATOR = "/";

    @Test
    public void testConstants() {
        assertEquals(PATH_SEPARATOR, OutputDirectoryCreator.PATH_SEPARATOR);
    }

}

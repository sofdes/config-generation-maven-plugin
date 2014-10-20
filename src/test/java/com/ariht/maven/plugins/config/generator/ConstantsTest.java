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

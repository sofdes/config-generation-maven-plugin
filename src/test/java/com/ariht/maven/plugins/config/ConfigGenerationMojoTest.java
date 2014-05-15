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

package com.ariht.maven.plugins.config;

import com.google.common.collect.Lists;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.junit.Test;
import java.io.File;
import java.io.IOException;
import java.net.URL;

/**
 * Not so much a unit test as an example showing filters and templates being processed together.
 */
public class ConfigGenerationMojoTest {

    /**
     * Using example inputs in the src/test/resources directory show how the plugin
     * combines each template and filter.
     */
    @Test
    public void testReadingDirectory() throws MojoExecutionException, MojoFailureException, IOException {
        final ConfigGenerationMojo configGenerationMojo = new ConfigGenerationMojo();

        configGenerationMojo.templatesBasePath = getAbsolutePath("templates");
        configGenerationMojo.filtersBasePath = getAbsolutePath("filters");
        configGenerationMojo.outputBasePath = getAbsolutePath("../generated-unit-tests-config");
        configGenerationMojo.setLog(new TestsLogger());
        configGenerationMojo.logOutput = true;
        configGenerationMojo.filtersToIgnore = Lists.newArrayList(getAbsolutePath("filters/personal/README"));
        configGenerationMojo.templatesToIgnore = Lists.newLinkedList();

        configGenerationMojo.execute();
    }

    /**
     * Given a relative path on the classpath, in this case src/tests/resources.
     */
    private String getAbsolutePath(final String subDirectoryName) throws IOException {
        final URL resource = getClass().getResource("/");
        final String normalizedAbsolutePath = FilenameUtils.normalize(resource.getFile() + subDirectoryName);
        final File directoryFile = new File(normalizedAbsolutePath);
        if (!directoryFile.exists()) {
            FileUtils.forceMkdir(directoryFile);
        }
        return normalizedAbsolutePath;
    }
}

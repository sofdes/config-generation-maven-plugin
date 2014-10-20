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

import com.ariht.maven.plugins.config.generator.Constants;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.maven.plugin.logging.Log;

import java.io.File;
import java.io.IOException;

/**
 * Created by David on 20/10/2014.
 */
public class OutputDirectoryCreator {

    private final Log log;

    public OutputDirectoryCreator(final Log log) {
        this.log = log;
    }

    /**
     * Prepare output io: base-path/filter-sub-dir/template-dir/template.name
     */
    public String createOutputDirectory(final FileInfo template, final FileInfo filter, final String outputBasePath) throws IOException {
        final String outputDirectory = getOutputPath(template, filter, outputBasePath);
        final File outputDir = new File(outputDirectory);
        if (!outputDir.exists()) {
            log.debug("Creating : " + outputDir);
            FileUtils.forceMkdir(outputDir);
        }
        return FilenameUtils.normalize(outputDirectory);
    }

    /**
     * Concatenate filter io with template io
     */
    private String getOutputPath(final FileInfo template, final FileInfo filter, final String outputBasePath) {
        final String outputPath = outputBasePath + Constants.PATH_SEPARATOR
                + filter.getRelativeSubDirectory() + Constants.PATH_SEPARATOR
                + filter.getNameWithoutExtension() + Constants.PATH_SEPARATOR
                + template.getRelativeSubDirectory() + Constants.PATH_SEPARATOR;
        return FilenameUtils.separatorsToUnix(FilenameUtils.normalize(outputPath));
    }
}

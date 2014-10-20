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

import com.ariht.maven.plugins.config.parameters.ConfigGeneratorParameters;
import com.google.common.base.Preconditions;
import org.apache.commons.io.FileUtils;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugin.logging.Log;

import java.io.File;
import java.io.IOException;

/**
 * Deletes all files from the target output directory.
 */
public class DirectoryDeleter {

    private final Log log;

    public DirectoryDeleter(Log log) {
        Preconditions.checkArgument(log != null);
        this.log = log;
    }

    /**
     * Clear contents of config generation build target/output io ready for new directory.
     */
    public void clearTargetDirectory(final ConfigGeneratorParameters configGeneratorParameters) throws MojoFailureException {
        final File outputDir = new File(configGeneratorParameters.getOutputBasePath());
        if (outputDir.exists()) {
            log.debug("Deleting : " + outputDir);
            try {
                FileUtils.forceDelete(outputDir);
            } catch(IOException e) {
                log.error("Error while clearing config generation output io: ["
                        + outputDir + "], error was: " + String.valueOf(e.getMessage()));
                throw new MojoFailureException(e.getMessage(), e);
            }
        }
    }

}

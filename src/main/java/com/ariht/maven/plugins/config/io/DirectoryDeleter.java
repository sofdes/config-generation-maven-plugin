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

import org.apache.commons.io.FileUtils;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugin.logging.Log;

import java.io.File;
import java.io.IOException;

public class DirectoryDeleter {

   /**
    * Clear contents of config generation build target/output io ready for new io.
    */
    public void clearTargetDirectory(final String outputBasePath, final Log logger) throws MojoFailureException {
        final File outputDir = new File(outputBasePath);
        if (outputDir.exists()) {
            logger.debug("Deleting : " + outputDir);
            try {
                FileUtils.forceDelete(outputDir);
            } catch(IOException e) {
                logger.error("Error while clearing config generation output io: ["
                        + outputDir + "], error was: " + String.valueOf(e.getMessage()));
                throw new MojoFailureException(e.getMessage(), e);
            }
        }
    }

}

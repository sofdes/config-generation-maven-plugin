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

import com.google.common.base.Preconditions;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.maven.plugin.logging.Log;

import java.io.File;
import java.text.MessageFormat;
import java.util.*;

public class StringToFileConverter {

    private final List<String> filesAndDirectoriesToIgnore;
    private final Log log;
    private final List<File> EMPTY_FILE_LIST = Collections.unmodifiableList(new LinkedList<File>());

    public StringToFileConverter(final List<String> filesAndDirectoriesToIgnore, final Log log) {
        Preconditions.checkArgument(log != null);
        this.log = log;
        this.filesAndDirectoriesToIgnore = filesAndDirectoriesToIgnore;
    }

    /**
     * Directories and/or specific io you do not wish to include in config generation
     * are converted from String to File instances.
     */
    private List<File> convertStringsToFiles(final List<String> filenames) {
        if (filenames == null || filenames.isEmpty()) {
            return EMPTY_FILE_LIST;
        }
        final List<File> files = new ArrayList<File>(filenames.size());
        for (final String filename : new LinkedHashSet<String>(filenames)) {
            if (StringUtils.isNotBlank(filename)) {
                final String unixFilename = FilenameUtils.separatorsToUnix(FilenameUtils.normalize(filename.trim()));
                final File file = new File(unixFilename);
                if (file.exists()) {
                    if (log.isDebugEnabled()) {
                        log.debug(MessageFormat.format("Adding file to collection: [{0}]", file));
                    }
                    files.add(file);
                }
            }
        }
        return files;
    }
}

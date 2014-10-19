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

import com.google.common.base.Joiner;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.apache.maven.plugin.logging.Log;

import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

/**
 * Container to information about a file so that the processor can access relative
 * paths, filename (without extension) and the File instance with further processing.
 */
public class FileInfo {

    private final File file;
    private final Log log;

    private String relativeSubDirectory;
    private List<File> externalFiles;

    public FileInfo(final Log log, final File file) {
        this.file = file;
        this.log = log;
    }

    public void setRelativeSubDirectory(final String relativeSubDirectory) {
        this.relativeSubDirectory = relativeSubDirectory;
    }

    public String getRelativeSubDirectory() {
        return relativeSubDirectory;
    }

    public String getNameWithoutExtension() {
        return FilenameUtils.removeExtension(file.getName());
    }

    public String getName() {
        return file.getName();
    }

    public File getFile() {
        return file;
    }

    private String getRelativeSubDirectoryAndFilename(final File file) throws IOException {
        return file.getCanonicalPath() + "/" + getName();
    }

    public String getAllSources() throws IOException {
        final List<String> allFileNames = new LinkedList<String>();
        allFileNames.add(FilenameUtils.separatorsToUnix(relativeSubDirectory + "/" + getName()));
        if (this.externalFiles != null) {
            for (final File f : externalFiles) {
                allFileNames.add(FilenameUtils.separatorsToUnix(f.getCanonicalPath() + "/" + f.getName()));
            }
        }
        return "[" + Joiner.on(", ").join(allFileNames) + "]";
    }

    public List<File> getFiles() {
        final List<File> filesList = new LinkedList<File>();
        filesList.add(file);
        if (externalFiles != null && !externalFiles.isEmpty()) {
            filesList.addAll(externalFiles);
        }
        return filesList;
    }

    public void lookForExternalFiles(final List<String> externalBasePaths) {
        if (externalBasePaths == null || externalBasePaths.isEmpty()) {
            return;
        }
        externalFiles = new LinkedList<File>();
        for (final String basePath : externalBasePaths) {
            final String fullCanonicalFilename = FilenameUtils.separatorsToUnix(basePath + relativeSubDirectory + "/" + this.getName());
            log.debug("Searching for: [" + fullCanonicalFilename + "]");
            final File externalFile = new File(fullCanonicalFilename);
            if (externalFile.exists()) {
                log.debug("Including external file: [" + fullCanonicalFilename + "]");
                externalFiles.add(externalFile);
            }
        }
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.MULTI_LINE_STYLE);
    }

}

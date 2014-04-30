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

import java.io.File;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

/**
 * Container to information about a file so that the processors can input and output
 * relative paths and filenames easily as they've already been determined and placed here.
 */
public class FileInfo {

    private final File file;
    private final String nameWithoutExtension;
    private String relativeSubDirectory;

    public FileInfo(final File file) {
        this.file = file;
        this.nameWithoutExtension = FilenameUtils.removeExtension(file.getName());
    }

    public String getRelativeSubDirectory() {
        return relativeSubDirectory;
    }

    public void setRelativeSubDirectory(final String relativeSubDirectory) {
        this.relativeSubDirectory = relativeSubDirectory;
    }

    public String getNameWithoutExtension() {
        return nameWithoutExtension;
    }

    public File getFile() {
        return file;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.MULTI_LINE_STYLE);
    }

}

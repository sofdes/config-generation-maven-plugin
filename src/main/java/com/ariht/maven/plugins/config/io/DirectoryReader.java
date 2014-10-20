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
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.filefilter.DirectoryFileFilter;
import org.apache.commons.io.filefilter.TrueFileFilter;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.maven.plugin.logging.Log;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;
import java.util.Set;

/**
 * Reads directory recursively to create collated file information containing relative
 * paths, name (without extension) and a reference to the {@link File} itself.
 */
public class DirectoryReader {

    private final Log log;
    private final List<File> EMPTY_FILE_LIST = Collections.unmodifiableList(new LinkedList<File>());

    public DirectoryReader(final Log log) {
        this.log = log;
    }

    public List<FileInfo> readFilters(final ConfigGeneratorParameters configGeneratorParameters) throws IllegalAccessException, IOException, InstantiationException {
        final String path = configGeneratorParameters.getFiltersBasePath();
        final List<String> filesAndDirectoriesToIgnore = configGeneratorParameters.getFiltersToIgnore();
        final List<FileInfo> filters = readFiles(path, filesAndDirectoriesToIgnore);
        for (FileInfo fileInfo : filters) {
            fileInfo.lookForExternalFiles(configGeneratorParameters.getExternalFilterBasePaths());
        }
        return filters;
    }




    public List<FileInfo> readTemplates(final ConfigGeneratorParameters configGeneratorParameters) throws IllegalAccessException, IOException, InstantiationException {
        final String path = configGeneratorParameters.getTemplatesBasePath();
        final List<String> filesAndDirectoriesToIgnore = configGeneratorParameters.getTemplatesToIgnore();
        return readFiles(path, filesAndDirectoriesToIgnore);
    }

    /**
     * Read directory creating FileInfo for each file found, include sub-directories.
     */
    private List<FileInfo> readFiles(final String path, final List<String> filesAndDirectoriesToIgnore) throws IOException, InstantiationException, IllegalAccessException {
        final List<File> filesToIgnore = convertStringsToFiles(filesAndDirectoriesToIgnore);
        log.debug("Scanning directory: " + path);
        final File directory = new File(path);
        final Collection<File> allFiles = getAllFiles(directory, filesToIgnore);
        if (allFiles.isEmpty()) {
            log.warn("No files found in directory: " + path);
        }
        final List<FileInfo> allFilesInfo = new ArrayList<FileInfo>(allFiles.size());
        final String canonicalBaseDirectory = directory.getCanonicalPath();
        for (final File file : allFiles) {
            final FileInfo fileInfo = new FileInfo(log, file);
            // Remove base directory to derive sub-directory
            final String canonicalFilePath = FilenameUtils.getFullPathNoEndSeparator(file.getCanonicalPath());
            final String subDirectory = FilenameUtils.normalize(StringUtils.replaceOnce(canonicalFilePath, canonicalBaseDirectory, ""));
            fileInfo.setRelativeSubDirectory(subDirectory);
            allFilesInfo.add(fileInfo);
        }
        return allFilesInfo;
    }

    /**
     * Return collection of all files in directory and sub-directories, ignoring any that
     * have been specifically excluded in plugin configuration.
     */
    @SuppressWarnings("rawtypes")
    private Collection<File> getAllFiles(final File directory, final List<File> filesToIgnore) {
        if (!directory.exists()) {
            log.warn("Directory does not exist: " + directory.getPath());
            return EMPTY_FILE_LIST;
        }
        final Collection allFiles = FileUtils.listFiles(directory, TrueFileFilter.TRUE, DirectoryFileFilter.DIRECTORY);
        final Collection<File> files = new ArrayList<File>(allFiles.size());
        for (final Object o : allFiles) {
            if (o != null && o instanceof File) {
                final File file = (File) o;
                if (isFileToIgnore(file, filesToIgnore)) {
                    log.debug("Ignoring : " + file.toString());
                } else {
                    log.debug("Adding file: " + file.toString());
                    files.add(file);
                }
            } else {
                log.warn("Not a file: " + ToStringBuilder.reflectionToString(o));
            }
        }
        return files;
    }

    /**
     * Has a directory or specific file been excluded from config generation?
     */
    private boolean isFileToIgnore(final File file, final List<File> filesToIgnore) {
        for (File f : filesToIgnore) {
            if (StringUtils.startsWith(file.getAbsolutePath(), f.getAbsolutePath())) {
                log.debug("Matched prefix so will ignore: \n" + file.getAbsolutePath() + "\n" + f.getAbsolutePath());
                return true;
            }
        }
        return false;
    }

    /**
     * Directories and/or specific files you do not wish to include in config generation
     * are converted from String to File instances.
     */
    private List<File> convertStringsToFiles(final List<String> filesToIgnore) {
        if (filesToIgnore == null || filesToIgnore.isEmpty()) {
            return EMPTY_FILE_LIST;
        }
        final List<File> filesIgnored = new ArrayList<File>(filesToIgnore.size());
        for (String fileToIgnore : new LinkedHashSet<String>(filesToIgnore)) {
            if (StringUtils.isNotBlank(fileToIgnore)) {
                fileToIgnore = FilenameUtils.separatorsToUnix(FilenameUtils.normalize(fileToIgnore.trim()));
                final File file = new File(fileToIgnore);
                if (file.exists()) {
                    log.debug("Adding ignore for file: " + file.getAbsolutePath());
                    filesIgnored.add(file);
                }
            }
        }
        return filesIgnored;
    }

}

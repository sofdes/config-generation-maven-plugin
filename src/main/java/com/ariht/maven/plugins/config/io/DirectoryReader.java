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
import com.google.common.base.Objects;
import com.google.common.base.Preconditions;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.filefilter.DirectoryFileFilter;
import org.apache.commons.io.filefilter.TrueFileFilter;
import org.apache.commons.lang.StringUtils;
import org.apache.maven.plugin.logging.Log;

import java.io.File;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;

/**
 * Reads directory recursively to create collated file information containing relative
 * paths, name (without extension) and a reference to the {@link File} itself.
 */
public class DirectoryReader {

    private final Log log;
    private final List<File> EMPTY_FILE_LIST = Collections.unmodifiableList(new LinkedList<File>());
    private final List<String> EMPTY_STRING_LIST = Collections.unmodifiableList(new LinkedList<String>());

    public DirectoryReader(final Log log) {
        this.log = Preconditions.checkNotNull(log);
    }

    /**
     * Recursively scan through the filters directory loading all property files into collection.
     */
    public List<FileInfo> readFilters(final ConfigGeneratorParameters params) throws IllegalAccessException, IOException, InstantiationException {
        final List<FileInfo> filters = readFiles(params.getFiltersBasePath(), params.getFiltersToIgnore());
        log.debug(MessageFormat.format("Read filters: {0}", Arrays.toString(filters.toArray())));
        // For each filter identified, see if there is an identically named files on different base path(s)
        for (FileInfo fileInfo : filters) {
            lookForExternalFiles(fileInfo, params.getExternalFilterBasePaths());
        }
        return filters;
    }

    private void lookForExternalFiles(final FileInfo fileInfo, final List<String> externalBasePaths) {
        final List<File> externalFiles = new LinkedList<File>();
        for (final String basePath : Objects.firstNonNull(externalBasePaths, EMPTY_STRING_LIST)) {
            final String fullCanonicalFilename = FilenameUtils.separatorsToUnix(basePath + fileInfo.getRelativeSubDirectory() + "/" + fileInfo.getName());
            log.debug("Searching for: [" + fullCanonicalFilename + "]");
            final File externalFile = new File(fullCanonicalFilename);
            if (externalFile.exists()) {
                log.debug("Including external file: [" + fullCanonicalFilename + "]");
                externalFiles.add(externalFile);
            }
        }
        fileInfo.setExternalFiles(externalFiles);
    }

    public List<FileInfo> readTemplates(final ConfigGeneratorParameters params) throws IllegalAccessException, IOException, InstantiationException {
        final List<FileInfo> templates = readFiles(params.getTemplatesBasePath(), params.getTemplatesToIgnore());
        log.debug(MessageFormat.format("Read templates: {0}", Arrays.toString(templates.toArray())));
        return templates;
    }

    /**
     * Read directory creating FileInfo for each file found, include sub-directories.
     */
    private List<FileInfo> readFiles(final String path, final List<String> filesAndDirectoriesToIgnore) throws IOException, InstantiationException, IllegalAccessException {
        final List<File> filesToIgnore = convertStringsToFiles(filesAndDirectoriesToIgnore);

        log.debug(MessageFormat.format("Reading from: {0}, ignoring: {1}", path, Arrays.toString(filesAndDirectoriesToIgnore.toArray())));
        final File directory = new File(path);
        final Collection<File> allFiles = getAllFiles(directory, filesToIgnore);
        if (allFiles.isEmpty()) {
            log.warn("No files found in directory: " + path);
        }
        final List<FileInfo> allFilesInfo = new LinkedList<FileInfo>();
        final String canonicalBaseDirectory = directory.getCanonicalPath();
        for (final File file : allFiles) {
            final FileInfo fileInfo = new FileInfo(file);
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
    private Collection<File> getAllFiles(final File directory, final List<File> filesToIgnore) {
        if (!directory.exists()) {
            log.warn("Directory does not exist: " + directory.getPath());
            return EMPTY_FILE_LIST;
        }
        final Collection<File> files = new LinkedList<File>();
        for (final Object o : FileUtils.listFiles(directory, TrueFileFilter.TRUE, DirectoryFileFilter.DIRECTORY)) {
            if (o != null && o instanceof File) {
                final File file = (File) o;
                if (isFileToIgnore(file, filesToIgnore)) {
                    log.debug("Ignoring : " + file.toString());
                } else {
                    log.debug("Adding file: " + file.toString());
                    files.add(file);
                }
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
    private List<File> convertStringsToFiles(final List<String> fileNames) {
        final List<File> filesIgnored = new LinkedList<File>();
        for (String fileToIgnore : new LinkedHashSet<String>(Objects.firstNonNull(fileNames, EMPTY_STRING_LIST))) {
            if (StringUtils.isBlank(fileToIgnore)) {
                continue;
            }
            fileToIgnore = FilenameUtils.separatorsToUnix(FilenameUtils.normalize(fileToIgnore.trim()));
            final File file = new File(fileToIgnore);
            if (file.exists()) {
                log.debug("Adding ignore for file: " + file.getAbsolutePath());
                filesIgnored.add(file);
            }
        }
        return filesIgnored;
    }

}

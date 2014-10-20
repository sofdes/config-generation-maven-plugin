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

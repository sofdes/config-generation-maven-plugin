package com.ariht.maven.plugins.config.parameters;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.maven.plugin.logging.Log;

public class ConfigGeneratorParametersUtils {

    private final Log log;
    private final ConfigGeneratorParameters configGeneratorParameters;

    public ConfigGeneratorParametersUtils(Log log, ConfigGeneratorParameters configGeneratorParameters) {
        this.log = log;
        this.configGeneratorParameters = configGeneratorParameters;
    }

    public void logConfigurationParameters() {
        if (StringUtils.isBlank(configGeneratorParameters.getEncoding())) {
            configGeneratorParameters.setEncoding(System.getProperty("file.encoding"));
            log.warn("File encoding has not been set, using platform encoding '" + configGeneratorParameters.getEncoding()
                    + "', i.e. generated config is platform dependent!");
        } else if (configGeneratorParameters.isLogOutput()) {
            log.debug("Using file encoding '" + configGeneratorParameters.getEncoding() + "' while generating config.");
        }
        if (configGeneratorParameters.isLogOutput()) {
            log.debug("Templates path : " + FilenameUtils.separatorsToUnix(configGeneratorParameters.getTemplatesBasePath()));
            log.debug("Filters path   : " + FilenameUtils.separatorsToUnix(configGeneratorParameters.getFiltersBasePath()));
            log.debug("Output path    : " + FilenameUtils.separatorsToUnix(configGeneratorParameters.getOutputBasePath()));
        }
    }

    public void logOutputPath() {
        final String outputPathMessage = "Config generation to: " + FilenameUtils.separatorsToUnix(configGeneratorParameters.getOutputBasePath());
        if (configGeneratorParameters.isLogOutput()) {
            log.info(outputPathMessage);
        } else if (log.isDebugEnabled()) {
            log.debug(outputPathMessage);
        }
    }

}

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

package com.ariht.maven.plugins.config.parameters;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.maven.plugin.logging.Log;

/**
 * For logging config parameters.
 */
public class ConfigGeneratorParametersLogger {

    private final Log log;
    private final ConfigGeneratorParameters configGeneratorParameters;

    public ConfigGeneratorParametersLogger(Log log, ConfigGeneratorParameters configGeneratorParameters) {
        this.log = log;
        this.configGeneratorParameters = configGeneratorParameters;
    }

    public void logConfigurationParameters() {
        if (StringUtils.isBlank(configGeneratorParameters.getEncoding())) {
            configGeneratorParameters.setEncoding(System.getProperty("file.encoding"));
            log.warn("File encoding has not been set, using platform encoding '" + configGeneratorParameters.getEncoding()
                    + "', i.e. generated config is platform dependent!");
        } else if (configGeneratorParameters.isLogOutput()) {
            log.info("Using file encoding '" + configGeneratorParameters.getEncoding() + "' while generating config.");
        }
        if (configGeneratorParameters.isLogOutput()) {
            log.info("Templates path : " + FilenameUtils.separatorsToUnix(configGeneratorParameters.getTemplatesBasePath()));
            log.info("Filters path   : " + FilenameUtils.separatorsToUnix(configGeneratorParameters.getFiltersBasePath()));
            log.info("Output path    : " + FilenameUtils.separatorsToUnix(configGeneratorParameters.getOutputBasePath()));
        } else if (log.isDebugEnabled()) {
            log.debug("All config parameters: " + configGeneratorParameters.toString());
        }
        logOutputPath();
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

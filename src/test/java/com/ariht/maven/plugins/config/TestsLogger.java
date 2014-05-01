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

import org.apache.maven.plugin.logging.Log;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Maven Plugin Log implementation using slf4j Logger underneath.
 */
public class TestsLogger implements Log {

    private final Logger log = LoggerFactory.getLogger(getClass());

    public boolean isDebugEnabled() {
        return log.isDebugEnabled();
    }

    public void debug(CharSequence content) {
        log.debug(content.toString());
    }

    public void debug(CharSequence content, Throwable error) {
        log.debug(content.toString(), error);
    }

    public void debug(Throwable error) {
        log.debug("", error);
    }

    public boolean isInfoEnabled() {
        return log.isInfoEnabled();
    }

    public void info(CharSequence content) {
        log.info(content.toString());
    }

    public void info(CharSequence content, Throwable error) {
        log.info(content.toString(), error);
    }

    public void info(Throwable error) {
        log.info("", error);
    }

    public boolean isWarnEnabled() {
        return log.isWarnEnabled();
    }

    public void warn(CharSequence content) {
        log.warn(content.toString());
    }

    public void warn(CharSequence content, Throwable error) {
        log.warn(content.toString(), error);
    }

    public void warn(Throwable error) {
        log.warn("", error);
    }

    public boolean isErrorEnabled() {
        return log.isErrorEnabled();
    }

    public void error(CharSequence content) {
        log.error(content.toString());
    }

    public void error(CharSequence content, Throwable error) {
        log.error(content.toString(), error);
    }

    public void error(Throwable error) {
        log.error("", error);
    }
}

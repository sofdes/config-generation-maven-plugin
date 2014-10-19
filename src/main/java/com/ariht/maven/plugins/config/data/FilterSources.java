package com.ariht.maven.plugins.config.data;

import com.google.common.base.Preconditions;
import org.apache.commons.lang.StringUtils;

import java.io.File;
import java.util.LinkedList;
import java.util.List;

public class FilterSources {

    /**
     * @param filterBasePath The primary filter to use
     * @param additionalFilters For example
     */
    public List<File> processFilterSources(final String projectBasePath, final String filterBasePath, final List<String> additionalFilters) {

        if (filterBasePath != null) {
            // TODO:
            throw new UnsupportedOperationException();
        }

        Preconditions.checkArgument(StringUtils.isNotBlank(projectBasePath));
        final List<String> filters = processAdditionalFilters(additionalFilters);
        if (StringUtils.isNotBlank(StringUtils.trim(filterBasePath))) {
            filters.add(StringUtils.trim(filterBasePath));
        }
        Preconditions.checkArgument(!filters.isEmpty());

        for (final String filter : filters) {

        }

        return null;
    }

    private List<String> processAdditionalFilters(final List<String> additionalFilters) {
        final List<String> processedFilters = new LinkedList<String>();
        if (additionalFilters != null && !additionalFilters.isEmpty()) {
            for (final String filter : additionalFilters) {
                if (StringUtils.isNotBlank(StringUtils.trim(filter))) {
                    processedFilters.add(StringUtils.trim(filter));
                }
            }
        }
        return processedFilters;
    }

}

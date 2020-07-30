package org.xujin.janus.core.filter;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author: gan
 * @date: 2020/4/29
 */
public class FilterSort {
    /**
     * sort filter
     * PRE_IN>>IN>>ENDPOINT>>OUT>>AFTER_OUT
     * in same filterType use filter order()
     *
     * @param originalFilters
     * @return
     */
    public static List<Filter> sort(List<Filter> originalFilters) {
        if (originalFilters == null || originalFilters.size() <= 0) {
            return new ArrayList<>();
        }
        List<Filter> sortedFilters = originalFilters.stream()
                .distinct()
                .sorted(Filter::compareTo)
                .collect(Collectors.toList());
        return sortedFilters;
    }
}

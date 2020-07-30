package org.xujin.janus.core.filter;


import org.xujin.janus.core.context.FilterContext;

import java.util.Map;

/**
 * @author: gan
 * @date: 2020/4/15
 */
public interface Filter {

    /**
     * name of filter
     *
     * @return string
     */
    String name();

    /**
     * order() must  be defined for a filter.
     * Filters may have the same  order if precedence is not.
     * important for a filter. order do not need to be sequential.
     *
     * @return the int order of a filter
     */
    int order();

    /**
     * enable filter
     */
    boolean enabled();

    /**
     * disable filter
     */
    boolean disabled();

    /**
     * true:enable
     * false:disable
     *
     * @return
     */
    boolean isEnable();

    /**
     * to classify a filter by type.
     *
     * @return FilterType
     */
    FilterType type();

    /**
     * get this filter config in the route
     *
     * @param routeId
     * @return
     */
    Map<String, String> getConfig(String routeId);

    /**
     * filter main method
     *
     * @param context
     */
    void filter(FilterContext context);

    /**
     * @param o
     * @return
     */
    int compareTo(Filter o);

}

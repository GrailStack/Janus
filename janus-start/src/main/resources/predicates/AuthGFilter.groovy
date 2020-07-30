package org.xujin.janus.filter.filters

import org.xujin.janus.core.context.FilterContext
import org.xujin.janus.core.filter.filters.AbstractSyncFilter
import org.xujin.janus.core.filter.FilterType

/**
 * @author: gan* @date: 2020/5/25
 */
class AuthGFilter extends AbstractSyncFilter {
    AuthGFilter(Map<String, Object> filterArgs) {
        super(filterArgs)
    }

    @Override
    void doFilter(FilterContext context) {
        System.out.println("authG")
    }

    @Override
    int order() {
        return 0
    }

    @Override
    FilterType type() {
        return FilterType.AFTER_OUT;
    }
}

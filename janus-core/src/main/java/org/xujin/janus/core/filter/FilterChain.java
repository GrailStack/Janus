package org.xujin.janus.core.filter;

import org.xujin.janus.core.constant.SessionContextKey;
import org.xujin.janus.core.context.FilterContext;
import org.xujin.janus.core.route.Route;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * @author: gan
 * @date: 2020/4/16
 */
public final class FilterChain {
    private static final Logger logger = LoggerFactory.getLogger(FilterChain.class);

    private FilterChain() {

    }

    /**
     * build filter chain with context and route
     *
     * @param context
     */
    public static void start(FilterContext context) {
        if (context == null) {
            throw new IllegalArgumentException("context cannot be null");
        }
        int filterStartIndex = -1;
        context.getSessionContext().put(SessionContextKey.FILTER_INDEX, filterStartIndex);
        next(context);

    }

    /**
     * run next filter
     *
     * @param context
     */
    public static void next(FilterContext context) {
        int index = (int) context.getSessionContext().get(SessionContextKey.FILTER_INDEX);
        index++;
        run(context, index);
    }

    /**
     * previous filter
     *
     * @param context
     */
    public static void previous(FilterContext context) {
        int index = (int) context.getSessionContext().get(SessionContextKey.FILTER_INDEX);
        index--;
        run(context, index);
    }

    private static void run(FilterContext context, int index) {
        context.getSessionContext().put(SessionContextKey.FILTER_INDEX, index);
        Route route = (Route) context.getSessionContext().get(SessionContextKey.FILTER_ROUTE);
        if (route == null) {
            throw new RuntimeException("no route found in " + context.toString());
        }
        List<Filter> routeFilters = route.getFilters();
        if (routeFilters == null || routeFilters.size() <= 0) {
            throw new RuntimeException("no filters found in " + route.getId());
        }
        if (index >= 0 && index < routeFilters.size()) {
            Filter filter = routeFilters.get(index);
            filter.filter(context);
        }
    }
}

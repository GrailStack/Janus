package org.xujin.janus.core.route;


import org.xujin.janus.core.constant.SessionContextKey;
import org.xujin.janus.core.context.FilterContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Set;

/**
 * @author: gan
 * @date: 2020/4/17
 */
public class RouteHandler {
    private static final Logger logger = LoggerFactory.getLogger(RouteHandler.class);
    private Set<Route> allRoutes;

    public RouteHandler(Set<Route> allRoutes) {
        this.allRoutes = allRoutes;
    }

    /**
     * match one route from entire routes use FilterContext
     * when predicate is null return null
     * when multi predicate match,return first one
     * when none predicate match,return null
     *
     * @param filterContext
     * @return
     */
    public Route lookupRoute(FilterContext filterContext) {
        if (this.allRoutes == null) {
            return null;
        }
        Route route = this.allRoutes.stream()
                .filter(e -> e.getPredicate() != null)
                .filter(e -> {
                    filterContext.getSessionContext().put(SessionContextKey.PREDICATE_ROUTE, e);
                    boolean result = e.getPredicate().test(filterContext);
                    filterContext.getSessionContext().put(SessionContextKey.PREDICATE_ROUTE, null);
                    return result;
                })
                .findFirst()
                .orElse(null);
        return route;
    }
}

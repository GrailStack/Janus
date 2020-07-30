package org.xujin.janus.core.resilience.retry;

import org.xujin.janus.core.constant.SessionContextKey;
import org.xujin.janus.core.context.FilterContext;
import org.xujin.janus.core.filter.filters.AbstractSyncFilter;
import org.xujin.janus.core.filter.FilterType;
import org.xujin.janus.core.route.Route;
import io.github.resilience4j.retry.Retry;

/**
 * @author: gan
 * @date: 2020/5/14
 */
public class RetryFilter extends AbstractSyncFilter {

    @Override
    public int order() {
        return Integer.MIN_VALUE + 1;
    }

    @Override
    public FilterType type() {
        return FilterType.PRE_IN;
    }

    @Override
    public void doFilter(FilterContext context) {
        Route route = getRoute(context);
        String routeId = route.getId();
        String retryName = routeId;
        Retry retry = RetryRepo.get(retryName);
        if (retry == null) {
            RetryRepo.addForRoute(route);
            retry = RetryRepo.get(retryName);
        }
        Retry.Context retryContext = retry.context();
        context.getSessionContext().put(SessionContextKey.RETRY_CONTEXT, retryContext);
        context.getSessionContext().put(SessionContextKey.RETRY, retry);
    }
}

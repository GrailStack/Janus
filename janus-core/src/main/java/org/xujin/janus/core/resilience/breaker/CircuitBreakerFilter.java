package org.xujin.janus.core.resilience.breaker;

import org.xujin.janus.core.constant.SessionContextKey;
import org.xujin.janus.core.context.FilterContext;
import org.xujin.janus.core.filter.filters.AbstractSyncFilter;
import org.xujin.janus.core.filter.FilterType;
import org.xujin.janus.core.route.Route;
import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author: gan
 * @date: 2020/5/11
 */
public class CircuitBreakerFilter extends AbstractSyncFilter {
    private static final Logger logger = LoggerFactory.getLogger(CircuitBreakerFilter.class);

    @Override
    public void doFilter(FilterContext context) {
        Route route = getRoute(context);
        String circuitBreakerName = route.getId();
        CircuitBreaker circuitBreaker = BreakerRepo.getCircuitBreaker(circuitBreakerName);
        if (circuitBreaker == null) {
            BreakerRepo.addForRoute(route);
            circuitBreaker = BreakerRepo.getCircuitBreaker(circuitBreakerName);
        }
        circuitBreaker.acquirePermission();
        long start = System.nanoTime();
        context.getSessionContext().put(SessionContextKey.CIRCUIT_BREAKER, circuitBreaker);
        context.getSessionContext().put(SessionContextKey.CIRCUIT_BREAKER_START_TIME, start);
    }

    @Override
    public int order() {
        return Integer.MIN_VALUE;
    }

    @Override
    public FilterType type() {
        return FilterType.PRE_IN;
    }

}

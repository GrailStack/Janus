package org.xujin.janus.core.resilience.breaker;

import org.xujin.janus.core.filter.FilterRepo;
import org.xujin.janus.core.route.Route;
import org.xujin.janus.core.util.NameUtils;
import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * each route register a circuitBreaker
 *
 * @author: gan
 * @date: 2020/5/11
 */
public class BreakerRepo {
    private static final Logger logger = LoggerFactory.getLogger(BreakerRepo.class);
    /**
     * key: route id
     */
    private static ConcurrentMap<String, CircuitBreaker> allCircuitBreaker = new ConcurrentHashMap<>();

    public static void add(String name, CircuitBreaker circuitBreaker) {
        allCircuitBreaker.putIfAbsent(name, circuitBreaker);
    }

    public static void removeIfExist(String breakerName) {
        if (allCircuitBreaker.containsKey(breakerName)) {
            allCircuitBreaker.remove(breakerName);
        }
    }

    public static void addForRoute(Route route) {
        String circuitBreakerName = route.getId();
        CircuitBreaker circuitBreaker = BreakerRepo.getCircuitBreaker(circuitBreakerName);
        if (circuitBreaker != null) {
            return;
        }
        String filterName = NameUtils.normalizeFilterName(CircuitBreakerFilter.class);
        //find custom circuitBreakerConfig in route
        CircuitBreakerFilter circuitBreakerFilter = (CircuitBreakerFilter) FilterRepo.get(filterName);


        if (circuitBreakerFilter == null || circuitBreakerFilter.getConfig(route.getId()) == null ||
                circuitBreakerFilter.getConfig(route.getId()).size() <= 0) {
            //create default circuitBreaker
            circuitBreaker = BreakerFactory.create(circuitBreakerName);
        } else {
            //create custom circuitBreaker
            circuitBreaker = BreakerFactory.create(circuitBreakerName, fromFilterArg(circuitBreakerFilter.getConfig(route.getId())));
        }
        add(circuitBreakerName, circuitBreaker);

    }

    public static CircuitBreaker getCircuitBreaker(String name) {
        return allCircuitBreaker.get(name);
    }

    private static CircuitBreakerConfig fromFilterArg(Map<String, String> filterArg) {
        CircuitBreakerConfig.Builder builder = CircuitBreakerConfig.custom();
        try {

            Optional.ofNullable(filterArg.get("failureRateThreshold"))
                    .ifPresent(e -> builder.failureRateThreshold(Float.parseFloat(e)));
            Optional.ofNullable(filterArg.get("slowCallRateThreshold"))
                    .ifPresent(e -> builder.slowCallRateThreshold(Float.parseFloat(e)));
            Optional.ofNullable(filterArg.get("slowCallDurationThreshold"))
                    .ifPresent(e -> builder.slowCallDurationThreshold(Duration.ofMillis(Long.parseLong(e))));
            Optional.ofNullable(filterArg.get("permittedNumberOfCallsInHalfOpenState"))
                    .ifPresent(e -> builder.permittedNumberOfCallsInHalfOpenState(Integer.parseInt((e))));
            Optional.ofNullable(filterArg.get("slidingWindowType"))
                    .ifPresent(e -> builder.slidingWindowType(from(e)));
            Optional.ofNullable(filterArg.get("slidingWindowSize"))
                    .ifPresent(e -> builder.slidingWindowSize(Integer.parseInt(e)));
            Optional.ofNullable(filterArg.get("minimumNumberOfCalls"))
                    .ifPresent(e -> builder.minimumNumberOfCalls(Integer.parseInt(e)));
            Optional.ofNullable(filterArg.get("waitDurationInOpenState"))
                    .ifPresent(e -> builder.waitDurationInOpenState(Duration.ofMillis(Long.parseLong(e))));
        } catch (Exception ex) {
            logger.error("circuit breaker filter arg cast to breaker config error", ex);
            throw ex;
        }
        return builder.build();
    }

    private static CircuitBreakerConfig.SlidingWindowType from(String slidingWindowType) {
        if ("TIME_BASED".equalsIgnoreCase(slidingWindowType)) {
            return CircuitBreakerConfig.SlidingWindowType.TIME_BASED;
        }
        return CircuitBreakerConfig.SlidingWindowType.COUNT_BASED;

    }
}

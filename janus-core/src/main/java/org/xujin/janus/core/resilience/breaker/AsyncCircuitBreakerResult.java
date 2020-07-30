package org.xujin.janus.core.resilience.breaker;

import org.xujin.janus.core.constant.SessionContextKey;
import org.xujin.janus.core.context.FilterContext;
import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;

/**
 * @author: gan
 * @date: 2020/5/18
 */
public class AsyncCircuitBreakerResult {
    private static final Logger logger = LoggerFactory.getLogger(AsyncCircuitBreakerResult.class);

    /**
     * process circuit breaker complete
     *
     * @param context
     */
    public static void onComplete(FilterContext context) {
        Object breakerObj = context.getSessionContext().get(SessionContextKey.CIRCUIT_BREAKER);
        if (breakerObj != null) {
            CircuitBreaker circuitBreaker = (CircuitBreaker) breakerObj;
            long start = (long) context.getSessionContext().get(SessionContextKey.CIRCUIT_BREAKER_START_TIME);
            long durationInNanos = System.nanoTime() - start;
            circuitBreaker.onSuccess(durationInNanos, TimeUnit.NANOSECONDS);
        }
    }

    /**
     * process circuit breaker error
     *
     * @param context
     * @param throwable
     */
    public static void onError(FilterContext context, Throwable throwable) {
        Object breakerObj = context.getSessionContext().get(SessionContextKey.CIRCUIT_BREAKER);
        if (breakerObj != null) {
            CircuitBreaker circuitBreaker = (CircuitBreaker) breakerObj;
            long start = (long) context.getSessionContext().get(SessionContextKey.CIRCUIT_BREAKER_START_TIME);
            long durationInNanos = System.nanoTime() - start;
            circuitBreaker.onError(durationInNanos, TimeUnit.NANOSECONDS, throwable);
        }
    }


}

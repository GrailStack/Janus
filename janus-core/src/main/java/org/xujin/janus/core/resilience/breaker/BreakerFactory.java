package org.xujin.janus.core.resilience.breaker;

import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import io.github.resilience4j.micrometer.tagged.TaggedCircuitBreakerMetrics;
import io.micrometer.core.instrument.Metrics;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * @author: gan
 * @date: 2020/5/11
 */
public class BreakerFactory {
    private static final Logger logger = LoggerFactory.getLogger(BreakerFactory.class);

    public static CircuitBreaker create(String name) {
        CircuitBreakerConfig circuitBreakerConfig = defaultConfig();
        // Create a BreakerRepo with a custom global configuration
        CircuitBreakerRegistry circuitBreakerRegistry = CircuitBreakerRegistry.of(circuitBreakerConfig);
        CircuitBreaker circuitBreaker =
                circuitBreakerRegistry.circuitBreaker(name);
        TaggedCircuitBreakerMetrics
                .ofCircuitBreakerRegistry(circuitBreakerRegistry)
                .bindTo(Metrics.globalRegistry);
        return circuitBreaker;
    }

    public static CircuitBreaker create(String name, CircuitBreakerConfig breakerConfig) {
        // Create a BreakerRepo with a custom global configuration
        CircuitBreakerRegistry circuitBreakerRegistry = CircuitBreakerRegistry.of(breakerConfig);
        CircuitBreaker circuitBreaker =
                circuitBreakerRegistry.circuitBreaker(name);
        TaggedCircuitBreakerMetrics
                .ofCircuitBreakerRegistry(circuitBreakerRegistry)
                .bindTo(Metrics.globalRegistry);
        return circuitBreaker;
    }

    public static CircuitBreakerConfig defaultConfig() {
        // Create a custom configuration for a CircuitBreaker
        CircuitBreakerConfig circuitBreakerConfig = CircuitBreakerConfig
                .custom()
                .build();
        return circuitBreakerConfig;
    }

}

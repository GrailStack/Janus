package org.xujin.janus.start.test.breaker;

import org.xujin.janus.core.resilience.breaker.BreakerRepo;
import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.xujin.janus.startup.JanusBootStrap;

import java.util.concurrent.TimeUnit;

/**
 * @author: gan
 * @date: 2020/5/15
 */
public class BreakerTest {
    @Before
    public void init() {
        JanusBootStrap.initGateway();
    }

    @After
    public void destroy() {
        JanusBootStrap.destroy();
    }

    @Test
    public void testBreakerConfig() {
        CircuitBreaker circuitBreaker = BreakerRepo.getCircuitBreaker("BreakerTestConfig");
        CircuitBreakerConfig config = circuitBreaker.getCircuitBreakerConfig();
        Assert.assertTrue(config.getFailureRateThreshold() == 50);
        Assert.assertTrue(config.getMinimumNumberOfCalls() == 3);
        Assert.assertTrue(config.getPermittedNumberOfCallsInHalfOpenState() == 20);
        Assert.assertTrue(config.getSlidingWindowSize() == 3);
        Assert.assertTrue(config.getSlowCallRateThreshold() == 100);
        Assert.assertTrue(config.getSlidingWindowType().name().equals("COUNT_BASED"));
        Assert.assertTrue(config.getSlowCallDurationThreshold().toMillis() == 1000);

    }

    @Test
    public void testOnSuccess() {
        CircuitBreaker circuitBreaker = BreakerRepo.getCircuitBreaker("BreakerTestConfig");
        for (int i = 0; i < 10; i++) {
            circuitBreaker.onSuccess(100, TimeUnit.MILLISECONDS);
        }
        Assert.assertTrue(circuitBreaker.tryAcquirePermission());
    }

    @Test
    public void testOnSuccess_slow() {
        CircuitBreaker circuitBreaker = BreakerRepo.getCircuitBreaker("BreakerTestConfig");
        for (int i = 0; i < 3; i++) {
            circuitBreaker.onSuccess(1001, TimeUnit.MILLISECONDS);
        }
        Assert.assertFalse(circuitBreaker.tryAcquirePermission());
        Assert.assertTrue(circuitBreaker.getMetrics().getNumberOfSlowCalls() == 3);
    }

    @Test
    public void testOnError() {
        CircuitBreaker circuitBreaker = BreakerRepo.getCircuitBreaker("BreakerTestConfig");
        for (int i = 0; i < 3; i++) {
            circuitBreaker.onError(1001, TimeUnit.MILLISECONDS, new RuntimeException("test"));
        }
        Assert.assertFalse(circuitBreaker.tryAcquirePermission());
        Assert.assertTrue(circuitBreaker.getMetrics().getNumberOfSlowCalls() == 3);
        Assert.assertTrue(circuitBreaker.getMetrics().getNumberOfFailedCalls() == 3);
    }
}

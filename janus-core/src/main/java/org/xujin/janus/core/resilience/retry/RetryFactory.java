package org.xujin.janus.core.resilience.retry;

import org.xujin.janus.core.context.FilterContext;
import io.github.resilience4j.micrometer.tagged.TaggedRetryMetrics;
import io.github.resilience4j.retry.Retry;
import io.github.resilience4j.retry.RetryConfig;
import io.github.resilience4j.retry.RetryRegistry;
import io.micrometer.core.instrument.Metrics;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpResponseStatus;

import java.time.Duration;

/**
 * @author: gan
 * @date: 2020/5/14
 */
public class RetryFactory {
    public static Retry create(String name) {
        RetryRegistry registry = RetryRegistry.of(defaultConfig());
        Retry retry = registry.retry(name);
        TaggedRetryMetrics
                .ofRetryRegistry(registry)
                .bindTo(Metrics.globalRegistry);
        return retry;
    }

    public static Retry create(String name, RetryConfig retryConfig) {
        RetryRegistry registry = RetryRegistry.of(retryConfig);
        Retry retry = registry.retry(name);
        TaggedRetryMetrics
                .ofRetryRegistry(registry)
                .bindTo(Metrics.globalRegistry);
        return retry;
    }

    private static RetryConfig defaultConfig() {
        RetryConfig config = RetryConfig.<FilterContext>custom()
                .maxAttempts(3)
                //500 Internal Server Error ;503 Service Unavailable
                //Get request
                .retryOnResult(context ->
                        (context.getCtx().getOriHttpResponse().status().equals(HttpResponseStatus.INTERNAL_SERVER_ERROR)
                                || context.getCtx().getOriHttpResponse().status().equals(HttpResponseStatus.SERVICE_UNAVAILABLE)
                                || context.getCtx().getOriHttpResponse().status().equals(HttpResponseStatus.GATEWAY_TIMEOUT)
                                || context.getCtx().getOriHttpResponse().status().equals(HttpResponseStatus.NOT_IMPLEMENTED)
                                || context.getCtx().getOriHttpResponse().status().equals(HttpResponseStatus.OK))
                                && context.getCtx().getOriFullHttpRequest().method().equals(HttpMethod.POST))
                .waitDuration(Duration.ofMillis(200))
                .build();
        return config;
    }
}

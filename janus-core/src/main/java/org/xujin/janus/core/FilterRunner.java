package org.xujin.janus.core;

import org.xujin.janus.core.constant.SessionContextKey;
import org.xujin.janus.core.context.FilterContext;
import org.xujin.janus.core.filter.FilterChain;
import org.xujin.janus.core.route.Route;
import org.xujin.janus.core.route.RouteHandler;
import org.xujin.janus.core.route.RouteRepo;
import org.xujin.janus.core.netty.ctx.DefaultJanusCtx;
import org.xujin.janus.monitor.JanusMetrics;
import org.xujin.janus.monitor.constant.MetricsConst;
import io.github.resilience4j.circuitbreaker.CallNotPermittedException;
import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.micrometer.core.instrument.Tags;
import io.netty.handler.codec.http.HttpResponseStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Set;

/**
 * @author: gan
 * @date: 2020/4/18
 */
public class FilterRunner {
    private static final Logger logger = LoggerFactory.getLogger(FilterRunner.class);

    /**
     * main method
     *
     * @param ctx
     */
    public static void run(DefaultJanusCtx ctx) {
        FilterContext context = new FilterContext(ctx);
        Tags tags = Tags.of(MetricsConst.TAG_KEY_URL, context.getCtx().getOriFullHttpRequest().uri());
        JanusMetrics.counter(MetricsConst.METRIC_RECEIVE_REQUEST, tags);
        try {
            //match a unique route for a request
            Route route = matchRoute(context);
            long start = System.currentTimeMillis();
            context.getSessionContext().put(SessionContextKey.FILTER_START_TIME, start);
            context.getSessionContext().put(SessionContextKey.FILTER_ROUTE, route);
            //start filter chain
            FilterChain.start(context);

        } catch (CallNotPermittedException ex) {
            CircuitBreaker circuitBreaker = (CircuitBreaker) context.getSessionContext().get(SessionContextKey.CIRCUIT_BREAKER);
            logger.error("circuit breaker name:" + circuitBreaker.getName() + " is open", ex);
            //监控埋点
//            JanusMetrics.counter(MetricsConst.METRIC_BREAKER_COUNT, tags);
//            JanusMetrics.counter(MetricsConst.METRIC_ERROR_REQUEST, tags);
            context.setTextResponse("service unavailable,message:" + ex.getMessage(), HttpResponseStatus.INTERNAL_SERVER_ERROR);
            ctx.janusToOutsideClientAsyncSend(context.getCtx().getOriHttpResponse());
            JanusMetrics.counter(MetricsConst.METRIC_ERROR_REQUEST, tags);
        } catch (Exception ex) {
            logger.error("run filter chain error", ex);
            context.setTextResponse("run filter chain error,message:" + ex.getMessage(), HttpResponseStatus.BAD_GATEWAY);
            ctx.janusToOutsideClientAsyncSend(context.getCtx().getOriHttpResponse());
            JanusMetrics.counter(MetricsConst.METRIC_ERROR_REQUEST, tags);
        }
    }

    /**
     * search route for current context
     *
     * @param context
     */
    private static Route matchRoute(FilterContext context) {
        //match one route for current context
        Set<Route> allRoutes = RouteRepo.getAllRoutes();
        Route route = new RouteHandler(allRoutes).lookupRoute(context);
        if (route != null) {
            logger.info("route:" + route.getId() + " matched for " + context.toString());
            return route;
        } else {
            throw new RuntimeException("no route match for " + context.toString());
        }
    }

}

package org.xujin.janus.start.test;

import org.xujin.janus.core.constant.SessionContextKey;
import org.xujin.janus.core.context.FilterContext;
import org.xujin.janus.core.filter.FilterChain;
import org.xujin.janus.core.route.Route;
import org.xujin.janus.core.route.RouteRepo;
import org.xujin.janus.core.netty.ctx.DefaultJanusCtx;
import io.netty.handler.codec.http.DefaultFullHttpRequest;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpVersion;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.xujin.janus.startup.JanusBootStrap;

/**
 * @author: gan
 * @date: 2020/4/17
 */
public class FilterChainTest {
    @Rule
    public ExpectedException exception = ExpectedException.none();

    @Before
    public void init() {
        JanusBootStrap.initGateway();
    }

    @After
    public void destroy() {
        JanusBootStrap.destroy();
    }

    @Test(expected = Test.None.class /* no exception expected */)
    public void testStart_null_route() {
        FilterContext context = new FilterContext(null);
        FilterChain.start(context);
    }

    @Test
    public void testStart_null_context() {
        exception.expect(IllegalArgumentException.class);
        exception.expectMessage("context cannot be null");
        FilterChain.start(null);
    }


    @Test
    public void testStart_null_ctx() {
        exception.expect(IllegalArgumentException.class);
        exception.expectMessage("DefaultJanusCtx argument illegal");
        FilterContext context = new FilterContext(null);
        Route route = RouteRepo.getAllRoutes().stream().findAny().get();
        context.getSessionContext().put(SessionContextKey.FILTER_ROUTE, route);
        FilterChain.start(context);
    }

    @Test
    public void testStart_null_request() {
        exception.expect(IllegalArgumentException.class);
        exception.expectMessage("DefaultJanusCtx argument illegal");
        FilterContext context = new FilterContext(new DefaultJanusCtx());
        Route route = RouteRepo.getAllRoutes().stream().findAny().get();
        context.getSessionContext().put(SessionContextKey.FILTER_ROUTE, route);
        FilterChain.start(context);
    }

    @Test
    public void testStart_null_ClientIp() {
        exception.expect(IllegalArgumentException.class);
        exception.expectMessage("DefaultJanusCtx argument illegal");
        DefaultJanusCtx defaultJanusCtx = new DefaultJanusCtx();
        defaultJanusCtx.setOriFullHttpRequest(new DefaultFullHttpRequest(HttpVersion.HTTP_1_1, HttpMethod.GET, ""));
        FilterContext context = new FilterContext(defaultJanusCtx);

        Route route = RouteRepo.getAllRoutes().stream().findAny().get();
        context.getSessionContext().put(SessionContextKey.FILTER_ROUTE, route);
        FilterChain.start(context);
    }

    @Test(expected = Test.None.class /* no exception expected */)
    public void testStart() {
        DefaultJanusCtx defaultJanusCtx = new DefaultJanusCtx();
        defaultJanusCtx.setOriFullHttpRequest(new DefaultFullHttpRequest(HttpVersion.HTTP_1_1, HttpMethod.GET, ""));
        defaultJanusCtx.setClientIp("127.0.0.1");
        defaultJanusCtx.setClientPort(80);
        FilterContext context = new FilterContext(defaultJanusCtx);
        Route route = RouteRepo.getAllRoutes().stream().findAny().get();
        context.getSessionContext().put(SessionContextKey.FILTER_ROUTE, route);
        FilterChain.start(context);
    }
}

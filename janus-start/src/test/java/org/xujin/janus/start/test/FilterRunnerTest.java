package org.xujin.janus.start.test;


import org.xujin.janus.core.FilterRunner;
import org.xujin.janus.core.netty.ctx.DefaultJanusCtx;
import io.netty.handler.codec.http.*;
import org.junit.*;
import org.junit.rules.ExpectedException;
import org.xujin.janus.startup.JanusBootStrap;


/**
 * @author: gan
 * @date: 2020/4/20
 */
public class FilterRunnerTest {
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


    @Test
    public void testEmptyUrl() {
        FilterRunner handler = new FilterRunner();
        FullHttpRequest httpRequest = new DefaultFullHttpRequest(HttpVersion.HTTP_1_0, HttpMethod.GET, "");
        DefaultJanusCtx janusCtx = new DefaultJanusCtx();
        janusCtx.setOriFullHttpRequest(httpRequest);
        handler.run(janusCtx);
        Assert.assertEquals(janusCtx.getOriHttpResponse().status(), HttpResponseStatus.BAD_GATEWAY);
    }


    @Test
    public void testNoRouteMatch() {
        FilterRunner handler = new FilterRunner();
        FullHttpRequest httpRequest = new DefaultFullHttpRequest(HttpVersion.HTTP_1_0, HttpMethod.GET, "http://tx.org/api/error/list?pageNo=1&pageSize=10");
        DefaultJanusCtx janusCtx = new DefaultJanusCtx();
        janusCtx.setOriFullHttpRequest(httpRequest);
        handler.run(janusCtx);
        Assert.assertEquals(janusCtx.getOriHttpResponse().status(), HttpResponseStatus.BAD_GATEWAY);
    }

    @Test
    public void testError_empty_context() {
        exception.expect(IllegalArgumentException.class);
        exception.expectMessage("context cannot be null");
        FilterRunner handler = new FilterRunner();
        handler.run(null);
    }


}

package org.xujin.janus.start.test.filter;

import org.xujin.janus.core.FilterRunner;
import org.xujin.janus.startup.JanusBootStrap;
import org.xujin.janus.core.netty.ctx.DefaultJanusCtx;
import io.netty.handler.codec.http.DefaultFullHttpRequest;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpVersion;
import org.junit.*;
import org.junit.rules.ExpectedException;

/**
 * @author: gan
 * @date: 2020/4/24
 */
public class AddResponseHeaderTest {
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
    public void testAutoParameter() throws InterruptedException {
        FilterRunner handler = new FilterRunner();
        FullHttpRequest httpRequest = new DefaultFullHttpRequest(HttpVersion.HTTP_1_0, HttpMethod.GET,
                "http://localhost:9885/AddResponseHeaderTest_Auto/sqlrecord");
        DefaultJanusCtx janusCtx = new DefaultJanusCtx();
        janusCtx.setOriFullHttpRequest(httpRequest);
        handler.run(janusCtx);
        //result return async
        Thread.sleep(1000L);
        Assert.assertTrue(janusCtx.getOriHttpResponse().headers().get("X-Request-Blue").equalsIgnoreCase("Blue"));
        Assert.assertFalse(janusCtx.getOriHttpResponse().headers().contains("X-Request-Red"));
    }

    @Test
    public void testNamedParameter() throws InterruptedException {
        FilterRunner handler = new FilterRunner();
        FullHttpRequest httpRequest = new DefaultFullHttpRequest(HttpVersion.HTTP_1_0, HttpMethod.GET,
                "http://localhost:9885/AddResponseHeaderTest_Named/sqlrecord");
        DefaultJanusCtx janusCtx = new DefaultJanusCtx();
        janusCtx.setOriFullHttpRequest(httpRequest);
        handler.run(janusCtx);
        //result return async
        Thread.sleep(1000L);
        Assert.assertTrue(janusCtx.getOriHttpResponse().headers().get("X-Request-Blue").equalsIgnoreCase("Blue"));
        Assert.assertTrue(janusCtx.getOriHttpResponse().headers().get("X-Request-Red").equalsIgnoreCase("red"));
    }
    @Test
    public void testCors() throws InterruptedException {
        FilterRunner handler = new FilterRunner();
        FullHttpRequest httpRequest = new DefaultFullHttpRequest(HttpVersion.HTTP_1_0, HttpMethod.GET,
                "http://localhost:9885/CORS/sqlrecord");
        DefaultJanusCtx janusCtx = new DefaultJanusCtx();
        janusCtx.setOriFullHttpRequest(httpRequest);
        handler.run(janusCtx);
        //result return async
        Thread.sleep(1000L);
        Assert.assertTrue(janusCtx.getOriHttpResponse().headers().contains("Access-Control-Allow-Origin"));

    }
}

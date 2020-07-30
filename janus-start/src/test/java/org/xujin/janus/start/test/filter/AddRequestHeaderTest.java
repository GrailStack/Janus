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
public class AddRequestHeaderTest {
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
    public void testAutoParameter() {
        FilterRunner handler = new FilterRunner();
        FullHttpRequest httpRequest = new DefaultFullHttpRequest(HttpVersion.HTTP_1_0, HttpMethod.GET,
                "http://localhost:9885/AddRequestHeaderTest_Auto/sqlrecord");
        DefaultJanusCtx janusCtx = new DefaultJanusCtx();
        janusCtx.setOriFullHttpRequest(httpRequest);
        handler.run(janusCtx);
        Assert.assertTrue(janusCtx.getOriFullHttpRequest().headers().get("X-Request-Blue").equalsIgnoreCase("Blue"));
        Assert.assertFalse(janusCtx.getOriFullHttpRequest().headers().contains("X-Request-Red"));
    }

    @Test
    public void testNamedParameter() {
        FilterRunner handler = new FilterRunner();
        FullHttpRequest httpRequest = new DefaultFullHttpRequest(HttpVersion.HTTP_1_0, HttpMethod.GET,
                "http://localhost:9885/AddRequestHeaderTest_Named/sqlrecord");
        DefaultJanusCtx janusCtx = new DefaultJanusCtx();
        janusCtx.setOriFullHttpRequest(httpRequest);
        handler.run(janusCtx);
        Assert.assertTrue(janusCtx.getOriFullHttpRequest().headers().get("X-Request-Blue").equalsIgnoreCase("Blue"));
        Assert.assertTrue(janusCtx.getOriFullHttpRequest().headers().get("X-Request-Red").equalsIgnoreCase("red"));
    }
}

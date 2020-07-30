package org.xujin.janus.start.test.filter;

import org.xujin.janus.core.FilterRunner;
import org.xujin.janus.core.netty.ctx.DefaultJanusCtx;
import io.netty.handler.codec.http.DefaultFullHttpRequest;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpVersion;
import org.junit.*;
import org.junit.rules.ExpectedException;
import org.xujin.janus.startup.JanusBootStrap;

import java.net.URI;

/**
 * @author: gan
 * @date: 2020/4/24
 */
public class PrefixPathFilterTest {
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
                "http://localhost:9885/PrefixPathTest_Auto/sqlrecord");
        DefaultJanusCtx janusCtx = new DefaultJanusCtx();
        janusCtx.setOriFullHttpRequest(httpRequest);
        handler.run(janusCtx);
        Assert.assertTrue(URI.create(janusCtx.getOriFullHttpRequest().uri()).getPath().startsWith("/api"));
    }

    @Test
    public void testNamedParameter() throws InterruptedException {
        FilterRunner handler = new FilterRunner();
        FullHttpRequest httpRequest = new DefaultFullHttpRequest(HttpVersion.HTTP_1_0, HttpMethod.GET,
                "http://localhost:9885/PrefixPathTest_Named/sqlrecord");
        DefaultJanusCtx janusCtx = new DefaultJanusCtx();
        janusCtx.setOriFullHttpRequest(httpRequest);
        handler.run(janusCtx);
        Assert.assertTrue(URI.create(janusCtx.getOriFullHttpRequest().uri()).getPath().startsWith("/api"));
    }

    @Test
    public void testParameterMiss() throws InterruptedException {
        FilterRunner handler = new FilterRunner();
        FullHttpRequest httpRequest = new DefaultFullHttpRequest(HttpVersion.HTTP_1_0, HttpMethod.GET,
                "http://localhost:9885/PrefixPathTest_Param_Miss/sqlrecord");
        DefaultJanusCtx janusCtx = new DefaultJanusCtx();
        janusCtx.setOriFullHttpRequest(httpRequest);
        handler.run(janusCtx);
        Assert.assertTrue(URI.create(janusCtx.getOriFullHttpRequest().uri()).getPath().startsWith("/sqlrecord"));
    }

    @Test
    public void testNoDelimiter() {
        FilterRunner handler = new FilterRunner();
        FullHttpRequest httpRequest = new DefaultFullHttpRequest(HttpVersion.HTTP_1_0, HttpMethod.GET,
                "http://localhost:9885/PrefixPathTest_No_Delimiter/sqlrecord");
        DefaultJanusCtx janusCtx = new DefaultJanusCtx();
        janusCtx.setOriFullHttpRequest(httpRequest);
        handler.run(janusCtx);
        Assert.assertTrue(URI.create(janusCtx.getOriFullHttpRequest().uri()).getPath().startsWith("/api"));
    }
}

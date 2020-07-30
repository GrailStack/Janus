package org.xujin.janus.start.test.filter;

import org.xujin.janus.core.FilterRunner;
import org.xujin.janus.core.netty.ctx.DefaultJanusCtx;
import org.xujin.janus.startup.JanusBootStrap;
import io.netty.handler.codec.http.DefaultFullHttpRequest;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpVersion;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.net.URI;

/**
 * @author: gan
 * @date: 2020/5/18
 */
public class StripPrefixTest {
    @Before
    public void init() {
        JanusBootStrap.initGateway();
    }

    @After
    public void destroy() {
        JanusBootStrap.destroy();
    }

    @Test
    public void testPartsLessZero() {
        FilterRunner handler = new FilterRunner();
        FullHttpRequest httpRequest = new DefaultFullHttpRequest(HttpVersion.HTTP_1_0, HttpMethod.GET,
                "http://localhost:9885/StripPrefix_Parts_LessZero/sqlrecord");
        DefaultJanusCtx janusCtx = new DefaultJanusCtx();
        janusCtx.setOriFullHttpRequest(httpRequest);
        handler.run(janusCtx);
        Assert.assertTrue(URI.create(janusCtx.getOriFullHttpRequest().uri()).getPath().startsWith("/StripPrefix_Parts_LessZero"));
    }
    @Test
    public void testPartsMoreThanPathLength() {
        FilterRunner handler = new FilterRunner();
        FullHttpRequest httpRequest = new DefaultFullHttpRequest(HttpVersion.HTTP_1_0, HttpMethod.GET,
                "http://localhost:9885/StripPrefix_Parts_MoreThanPathLength/sqlrecord");
        DefaultJanusCtx janusCtx = new DefaultJanusCtx();
        janusCtx.setOriFullHttpRequest(httpRequest);
        handler.run(janusCtx);
        Assert.assertTrue(URI.create(janusCtx.getOriFullHttpRequest().uri()).getPath().startsWith("/"));
        Assert.assertTrue(URI.create(janusCtx.getOriFullHttpRequest().uri()).getPath().endsWith("/"));
    }
}

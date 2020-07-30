package org.xujin.janus.core.context;


import org.xujin.janus.config.util.CheckUtils;
import org.xujin.janus.core.netty.ctx.DefaultJanusCtx;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.util.CharsetUtil;

/**
 * @author: gan
 * @date: 2020/4/17
 */
public class FilterContext {
    /**
     * ctx from Netty
     */
    private DefaultJanusCtx ctx;
    /**
     * map put temp variable
     */
    private SessionContext sessionContext;

    public FilterContext(DefaultJanusCtx ctx) {
        CheckUtils.checkNotNull(ctx, "DefaultJanusCtx cannot be null");
        CheckUtils.checkNotNull(ctx.getOriFullHttpRequest(), "DefaultJanusCtx.HttpRequest cannot be null");
        CheckUtils.checkNotNull(ctx.getClientIp(), "DefaultJanusCtx.ClientIp cannot be null");
        CheckUtils.checkParam(ctx.getClientPort() > 0, "DefaultJanusCtx.ClientPort illegal");
        this.sessionContext = new SessionContext();
        this.ctx = ctx;
    }

    public SessionContext getSessionContext() {
        return this.sessionContext;
    }

    public DefaultJanusCtx getCtx() {
        return ctx;
    }

    public void setTextResponse(String content, HttpResponseStatus status) {
        setResponse(content, status, "text/html; charset=UTF-8");
    }

    public void setJsonResponse(String content, HttpResponseStatus status) {
        setResponse(content, status, "application/json;charset=UTF-8");
    }

    public void setResponse(String content, HttpResponseStatus status, String contentType) {
        FullHttpResponse response = (FullHttpResponse) this.getCtx().getOriHttpResponse();
        ByteBuf byteBuf = Unpooled.copiedBuffer(content, CharsetUtil.UTF_8);
        if (response != null) {
            response.replace(byteBuf);
        } else {
            response = new DefaultFullHttpResponse(this.getCtx().getOriFullHttpRequest().protocolVersion(),
                    status, byteBuf);
        }
        response.headers().set(HttpHeaderNames.CONTENT_TYPE, contentType);
        response.headers().set(HttpHeaderNames.CONTENT_LENGTH, byteBuf.readableBytes());
        this.ctx.setOriHttpResponse(response);
    }

    @Override
    public String toString() {
        StringBuilder out = new StringBuilder();
        if (ctx.getOriFullHttpRequest().protocolVersion() != null) {
            out.append("protocolVersion:");
            out.append(ctx.getOriFullHttpRequest().protocolVersion());
            out.append(";");
        }
        if (ctx.getOriFullHttpRequest().uri() != null) {
            out.append("uri:");
            out.append(ctx.getOriFullHttpRequest().uri());
            out.append(";");
        }
        if (ctx.getOriFullHttpRequest().method() != null) {
            out.append("method:");
            out.append(ctx.getOriFullHttpRequest().method().name());
            out.append(";");
        }

        return out.toString();
    }
}

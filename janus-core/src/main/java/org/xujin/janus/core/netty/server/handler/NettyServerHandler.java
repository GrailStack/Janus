package org.xujin.janus.core.netty.server.handler;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.util.CharsetUtil;
import org.apache.commons.lang3.StringUtils;
import org.xujin.janus.config.ApplicationConfig;
import org.xujin.janus.core.FilterRunner;
import org.xujin.janus.core.constant.NettyConstants;
import org.xujin.janus.core.context.FilterContext;
import org.xujin.janus.core.context.SessionContext;
import org.xujin.janus.core.netty.ctx.DefaultJanusCtx;
import org.xujin.janus.core.netty.server.HttpCode;
import org.xujin.janus.daemon.admin.processor.OnlineProcessor;
import org.xujin.janus.monitor.JanusMetrics;
import org.xujin.janus.monitor.constant.MetricsConst;
import io.netty.channel.*;
import io.netty.handler.codec.http.*;
import io.netty.handler.codec.http.cookie.Cookie;
import io.netty.handler.codec.http.cookie.ServerCookieDecoder;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.util.AttributeKey;
import io.netty.util.ReferenceCountUtil;
import io.netty.util.ReferenceCounted;
import lombok.extern.slf4j.Slf4j;

import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Netty HTTP连接处理类
 *
 * @author tbkk 2019-10-29 20:07:37
 */
@Slf4j
@ChannelHandler.Sharable
public class NettyServerHandler extends ChannelInboundHandlerAdapter {

    private ThreadPoolExecutor serverHandlerPool;

    private AttributeKey<DefaultJanusCtx> ctxAttributeKey = AttributeKey.valueOf(NettyConstants.CHANNEL_CTX_KEY);

    private AtomicLong concurrentConnections = new AtomicLong();

    private static Map<String, String> returnOkUrlMap = new HashMap<String, String>();

    static {
        if (StringUtils.isNotEmpty(ApplicationConfig.getReturnOkUrl())) {
            String[] returnOkUrl = ApplicationConfig.getReturnOkUrl().split(";");
            for (int i = 0; i < returnOkUrl.length; i++) {
                returnOkUrlMap.put(returnOkUrl[i], "ok");
            }
        }
    }


    public NettyServerHandler(final ThreadPoolExecutor serverHandlerPool) {
        this.serverHandlerPool = serverHandlerPool;
        JanusMetrics.gauge(MetricsConst.METRIC_CONCURRENT_CONNECTIONS, null, concurrentConnections);
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        log.info("<---1.janus server start processing request--->");
        if (msg instanceof FullHttpRequest) {

            FullHttpRequest fullHttpRequest = (FullHttpRequest) msg;
            log.info("FullHttpRequest is : " + fullHttpRequest);

            DefaultJanusCtx currCtx = ctx.channel().attr(ctxAttributeKey).get();
            String cookieStr = fullHttpRequest.headers().get(HttpHeaderNames.COOKIE);
            Set<Cookie> cookies = null;
            if (cookieStr != null) {
                cookies = ServerCookieDecoder.STRICT.decode(cookieStr);
            }
            currCtx.setOriFullHttpRequest(fullHttpRequest);
            currCtx.setOriHttpResponse(null);
            currCtx.setIsKeepAlive(HttpUtil.isKeepAlive(fullHttpRequest));
            currCtx.setCookies(cookies);


            // 判断只有http协议解析成功，才会进行心跳检查，否则不进行心跳检查
            if (fullHttpRequest.decoderResult().isSuccess()) {
                if (isHeatBeatUrl(fullHttpRequest, currCtx)) {
                    //ctx.channel().close(); // 健康检查后关闭掉连接
                    return;
                }
            }

            //执行Janus Filter逻辑入口
            FilterRunner.run(currCtx);

        } else {
            if (msg instanceof ReferenceCounted) {
                ReferenceCountUtil.release(msg);
            }
        }
    }


    private boolean isHeatBeatUrl(FullHttpRequest request, DefaultJanusCtx context) {

        if (returnOkUrlMap.size() != 0) {
            if (returnOkUrlMap.get(request.uri()) != null) {
                writeBody(context, HttpCode.HTTP_OK_CODE, returnOkUrlMap.get(request.uri()));
                return true;
            }
        }

        // 心跳检测
        if (request.uri().equals("/health")) {
            if (OnlineProcessor.online == false) {
                writeBody(context, HttpCode.HTTP_GATEWAY_SHUTDOWN, "offline");
            } else {
                writeBody(context, HttpCode.HTTP_OK_CODE, "online");
            }

            return true;
        }
        return false;
    }

    private void writeBody(DefaultJanusCtx ctx, int httpCode, String body) {
        FilterContext context = new FilterContext(ctx);
        HttpResponseStatus status = HttpResponseStatus.valueOf(httpCode);
        context.setJsonResponse(body, status);
        ctx.janusToOutsideClientAsyncSend(context.getCtx().getOriHttpResponse());
    }

    /**
     * 当 Netty 由于 IO 错误或者处理器在处理事件时抛出的异常时,会调用exceptionCaught
     *
     * @param ctx
     * @param cause
     */
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        log.error("provider netty server caught exception", cause);
        DefaultJanusCtx janusCtx = ctx.channel().attr(ctxAttributeKey).get();
        if (janusCtx != null) {
            Optional.ofNullable(janusCtx.getOutSideClientExceptionCaughtCallback()).ifPresent(cb -> cb.runCallBack(janusCtx, cause));
            janusCtx.releaseReferenceCounted();
        }
        ctx.close();
        super.exceptionCaught(ctx, cause);
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent) {
            IdleStateEvent idleEvt = (IdleStateEvent) evt;
            if (idleEvt == IdleStateEvent.WRITER_IDLE_STATE_EVENT) {
                log.debug(" janus-cmd  client write an idle channel.");
            } else if (idleEvt == IdleStateEvent.READER_IDLE_STATE_EVENT) {
                log.debug(" janus-cmd  client read an idle channel.");
            } else if (idleEvt == IdleStateEvent.ALL_IDLE_STATE_EVENT) {
                log.debug(" janus-cmd  client all an idle channel.");
            }
        } else {
            super.userEventTriggered(ctx, evt);
        }
    }

    /**
     * 当前channel不活跃的时候,会触发channelInactive
     *
     * @param ctx
     * @throws Exception
     */
    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        concurrentConnections.decrementAndGet();
        DefaultJanusCtx janusCtx = ctx.channel().attr(ctxAttributeKey).get();
        if (janusCtx != null) {
            Optional.ofNullable(janusCtx.getOutSideClientChannelInactiveCallback()).ifPresent(cb -> cb.runCallBack(janusCtx));
            janusCtx.releaseReferenceCounted();
        }
        super.channelInactive(ctx);
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        log.info("channelActive： " + ctx.channel().remoteAddress().toString());
        concurrentConnections.incrementAndGet();
        JanusMetrics.counter(MetricsConst.METRIC_CONNECTIONS,null);

        String host = ((InetSocketAddress) ctx.channel().remoteAddress()).getAddress().getHostAddress();
        int port = ((InetSocketAddress) ctx.channel().remoteAddress()).getPort();
        DefaultJanusCtx newJanusCtx = new DefaultJanusCtx();
        newJanusCtx.setOutSideClientChannel(ctx.channel());
        newJanusCtx.setClientIp(host);
        newJanusCtx.setClientPort(port);
        //将新CTX加入到channel中
        ctx.channel().attr(ctxAttributeKey).set(newJanusCtx);
        super.channelActive(ctx);
    }

}

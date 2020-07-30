package org.xujin.janus.core.netty.ctx;

import io.netty.handler.codec.http.HttpHeaderNames;
import org.xujin.janus.core.constant.Protocol;
import org.xujin.janus.core.netty.client.NettyClientSender;
import org.xujin.janus.core.netty.client.callback.JanusCtxCallback;
import org.xujin.janus.core.netty.client.callback.JanusExceptionCallback;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponse;
import io.netty.handler.codec.http.cookie.Cookie;
import io.netty.util.ReferenceCountUtil;
import io.netty.util.ReferenceCounted;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.net.URI;
import java.util.Set;

@Slf4j
@Data
public class DefaultJanusCtx implements IJanusCtx {

    private volatile Channel outSideClientChannel;
    private volatile Channel innerServerChannel;

    private Boolean isKeepAlive = true;
    private volatile FullHttpRequest oriFullHttpRequest;
    private volatile HttpResponse oriHttpResponse;
    private String clientIp;
    private int clientPort;
    private Set<Cookie> cookies;

    private JanusCtxCallback execResponseCallBack;
    private JanusExceptionCallback outSideClientExceptionCaughtCallback;
    private JanusCtxCallback outSideClientChannelInactiveCallback;
    private JanusExceptionCallback innerServerExceptionCaughtCallback;
    private JanusCtxCallback innerServerChannelInactiveCallback;

    /**
     *  Janus网关向内部Server异步发送
     * @param httpRequest
     * @throws Exception
     */
    @Override
    public void janusToInnerServerAsyncSend(HttpRequest httpRequest) throws Exception {
        URI requestUri = URI.create(httpRequest.uri());
        String address = requestUri.getHost() + ":" + getPort(requestUri);
        ReferenceCountUtil.retain(httpRequest);

        //后端Server容器是Tomcat9会进行check,统一设置
        httpRequest.headers().set(HttpHeaderNames.HOST,address);
        new NettyClientSender().asyncSend(address, httpRequest, this);
    }

    private int getPort(URI requestUri) {

        if (requestUri.getPort() != -1) {
            return requestUri.getPort();
        }
        if (Protocol.HTTP.toString().equalsIgnoreCase(requestUri.getScheme())) {
            return Protocol.HTTP.getDefaultPort();
        }
        if (Protocol.HTTPS.toString().equalsIgnoreCase(requestUri.getScheme())) {
            return Protocol.HTTPS.getDefaultPort();
        }
        throw new RuntimeException("miss port in" + requestUri.toString());
    }

    public synchronized void releaseReferenceCounted() {
        if (oriFullHttpRequest != null) {
            while ((oriFullHttpRequest).refCnt() != 0) {
                ReferenceCountUtil.release(oriFullHttpRequest);
            }
            oriFullHttpRequest = null;
        }

        if (oriHttpResponse != null && oriHttpResponse instanceof ReferenceCounted) {
            while (((ReferenceCounted) oriHttpResponse).refCnt() != 0) {
                ReferenceCountUtil.release(oriHttpResponse);
            }
            oriHttpResponse = null;
        }
    }

    @Override
    public void janusToOutsideClientAsyncSend(HttpResponse responseToClient) {
        try {

            log.info("responseToClient : " + responseToClient);
            if (outSideClientChannel != null && outSideClientChannel.isActive() && outSideClientChannel.isWritable()) {
                ChannelFuture future = outSideClientChannel.writeAndFlush(responseToClient);
                log.info("<-----5.finally,write response to the client-------->");
                //ChannelFuture future = outSideClientChannelHandlerCtx.channel().writeAndFlush(LastHttpContent.EMPTY_LAST_CONTENT);
                //               ChannelFuture future = outSideClientChannelHandlerCtx.channel().writeAndFlush(responseToClient);
                if (!isKeepAlive) {
                    future.addListener(ChannelFutureListener.CLOSE);
                } else {
                    future.addListener(new ChannelFutureListener() {
                        @Override
                        public void operationComplete(ChannelFuture future) {
                            releaseReferenceCounted();
                        }
                    });
                }
            } else {

                log.error("client channel is already closed, can't response to client");
            }
        } catch (Exception e) {
            log.error("close client channel failed, channel info: ", e);
        } finally {
            //releaseReferenceCounted();
        }
    }

}

package org.xujin.janus.core.netty.client.handler;

import org.xujin.janus.core.constant.NettyConstants;
import org.xujin.janus.core.netty.ctx.DefaultJanusCtx;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.HttpResponse;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.util.AttributeKey;
import lombok.extern.slf4j.Slf4j;

import java.util.Optional;

/**
 * rpc netty client handler
 *
 * @author tbkk
 */
@Slf4j
public class NettyClientHandler extends ChannelInboundHandlerAdapter {

    private AttributeKey<DefaultJanusCtx> ctxAttributeKey = AttributeKey.valueOf(NettyConstants.CHANNEL_CTX_KEY);

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        log.info("<---3.Janus client Handler Process the returned response----->");
        if (msg instanceof HttpResponse) {
            HttpResponse httpResponse = (HttpResponse) msg;
            //从Netty Channel中获取Call Back
//            AttributeKey<ExecResponseCallback> attributeKey = AttributeKey.valueOf("CALL_BACK");
//            ExecResponseCallback responseCallback = ctx.channel().attr(attributeKey).get();
//            if (responseCallback != null) {
//                responseCallback.exec(oriHttpResponse);
//            }
            //根据key从Channel中获取返回结果,返回给客户端
            DefaultJanusCtx janusCtx = ctx.channel().attr(ctxAttributeKey).get();
            if (janusCtx !=  null) {
                janusCtx.setOriHttpResponse(httpResponse);
                Optional.ofNullable(janusCtx.getExecResponseCallBack()).ifPresent(cb -> cb.runCallBack(janusCtx));
            }
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        DefaultJanusCtx janusCtx = ctx.channel().attr(ctxAttributeKey).get();
        if (janusCtx != null) {
            Optional.ofNullable(janusCtx.getInnerServerExceptionCaughtCallback()).ifPresent(cb->cb.runCallBack(janusCtx, cause));
        }
        ctx.close();
        super.exceptionCaught(ctx, cause);
    }
    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        DefaultJanusCtx janusCtx = ctx.channel().attr(ctxAttributeKey).get();
        if (janusCtx != null) {
            Optional.ofNullable(janusCtx.getInnerServerChannelInactiveCallback()).ifPresent(cb->cb.runCallBack(janusCtx));
        }
        log.info(" CLIENT channelInactive!");
        super.channelInactive(ctx);
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent) {
            ctx.channel().close();      // close idle channel
            log.debug("netty client close an idle channel.");
        } else {
            super.userEventTriggered(ctx, evt);
        }
    }

}

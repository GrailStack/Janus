package org.xujin.janus.damon.client;


import org.xujin.janus.damon.exchange.CmdFutureResponsePool;
import org.xujin.janus.damon.exchange.NettyMsg;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;


@Slf4j
public class NettyClientHandler extends SimpleChannelInboundHandler<NettyMsg> {

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, NettyMsg msg) throws Exception {

        if (msg.getSync()) {
            CmdFutureResponsePool.getSingleton().notifyInvokerFuture(msg.getTranceId(), msg);
        } else {
            ClientMsgProcessorPool.getSingleton().invokeProcessor(msg.getCmdMsg());

        }

    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        log.error(" janus-cmd netty client caught exception", cause);
        ctx.close();
    }
}

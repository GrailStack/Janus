package org.xujin.janus.damon.idle;

import io.netty.channel.ChannelHandlerContext;

public interface IIdleHandler {
    public void execute(ChannelHandlerContext ctx);
}

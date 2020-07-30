package org.xujin.janus.daemon.handler;

import org.xujin.janus.daemon.admin.client.AdminRequests;
import org.xujin.janus.damon.idle.IIdleHandler;
import io.netty.channel.ChannelHandlerContext;

/**
 * 当Netty Client过了30秒没有和Janus Admin通讯时,抛出事件,
 * 此时Server需要发送心跳给Admin
 * @author xujin
 */
public class JanusWriteIdleHandler implements IIdleHandler {

    @Override
    public void execute(ChannelHandlerContext ctx) {
        AdminRequests.instance().sendHeartBeatMsg();
    }
}

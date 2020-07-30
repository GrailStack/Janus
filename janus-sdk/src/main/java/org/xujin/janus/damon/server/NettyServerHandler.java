package org.xujin.janus.damon.server;

import org.xujin.janus.damon.exchange.JanusCmdMsg;
import org.xujin.janus.damon.exchange.NettyMsg;
import org.xujin.janus.damon.processer.ICmdMsgProcessor;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ThreadPoolExecutor;

/**
 *
 */
@Slf4j
public class NettyServerHandler extends SimpleChannelInboundHandler<NettyMsg> {
    private ThreadPoolExecutor serverHandlerPool;
    private Map<String, ICmdMsgProcessor> processerMap = new HashMap<>();

    public NettyServerHandler(final ThreadPoolExecutor serverHandlerPool, Map processerMap) {
        this.serverHandlerPool = serverHandlerPool;
        this.processerMap = processerMap;
    }


    @Override
    public void channelRead0(final ChannelHandlerContext ctx, final NettyMsg msg) throws Exception {

        try {
            serverHandlerPool.execute(() ->{
                JanusCmdMsg janusCmdMsg = msg.getCmdMsg();
                if (processerMap.containsKey(janusCmdMsg.getMethod())){
                    JanusCmdMsg cmdResponse = processerMap.get(janusCmdMsg.getMethod()).execute(janusCmdMsg);

                    //如果是同步需要返回消息
                    if (msg.getSync())
                    {
                        NettyMsg repMsg = new NettyMsg();
                        repMsg.setTranceId(msg.getTranceId());
                        repMsg.setCmdMsg(cmdResponse);
                        repMsg.setSync(msg.getSync());
                        ctx.writeAndFlush(repMsg);
                    }
                }
            });

        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }

    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
    	log.error(" janus-cmd provider netty server caught exception", cause);
        ctx.close();
    }
}

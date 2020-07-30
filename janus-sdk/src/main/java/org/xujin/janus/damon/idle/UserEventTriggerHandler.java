package org.xujin.janus.damon.idle;

import io.netty.channel.*;
import io.netty.handler.timeout.IdleStateEvent;
import lombok.extern.slf4j.Slf4j;

/**
 *
 */
@Slf4j
@ChannelHandler.Sharable
public class UserEventTriggerHandler extends ChannelDuplexHandler {
    private IIdleHandler readIdleHandler;
    private IIdleHandler writeIdleHandler;
    private IIdleHandler allIdleHandler;

    public UserEventTriggerHandler(IIdleHandler readIdleHandler, IIdleHandler writeIdleHandler, IIdleHandler allIdleHandler){
        this.readIdleHandler = readIdleHandler;
        this.writeIdleHandler = writeIdleHandler;
        this.allIdleHandler = allIdleHandler;
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent){
            IdleStateEvent idleEvt = (IdleStateEvent) evt;
            if (idleEvt == IdleStateEvent.READER_IDLE_STATE_EVENT)
            {
                if (readIdleHandler != null) {
                    readIdleHandler.execute(ctx);
                }
//                log.info("长时间未收到心跳信息，关闭连接！");
//                ctx.channel().close();
            } else if (idleEvt == IdleStateEvent.WRITER_IDLE_STATE_EVENT)
            {
                if (writeIdleHandler != null) {
                    writeIdleHandler.execute(ctx);
                }
            }else if (idleEvt == IdleStateEvent.ALL_IDLE_STATE_EVENT)
            {
                if (allIdleHandler != null) {
                    allIdleHandler.execute(ctx);
                }
            }
        } else {
            super.userEventTriggered(ctx, evt);
        }
    }

}

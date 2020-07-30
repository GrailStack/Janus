package org.xujin.janus.damon;

import org.xujin.janus.damon.client.ClientIdleHandlerPool;
import org.xujin.janus.damon.client.ClientMsgProcessorPool;
import org.xujin.janus.damon.client.connect.ConnectClientPool;
import org.xujin.janus.damon.exception.JanusCmdException;
import org.xujin.janus.damon.exchange.CmdFutureResponse;
import org.xujin.janus.damon.exchange.CmdFutureResponsePool;
import org.xujin.janus.damon.exchange.JanusCmdMsg;
import org.xujin.janus.damon.exchange.NettyMsg;
import org.xujin.janus.damon.idle.IIdleHandler;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

@Slf4j
@Data
public class JanusCmdClient {

    private static class Holder {
        private static final JanusCmdClient janusCmdClient = new JanusCmdClient();
    }

    public static JanusCmdClient getSingleton() {
        return JanusCmdClient.Holder.janusCmdClient;
    }

    private JanusCmdClient() {
    }

    private long SYNC_TIME_OUT = 3000;
    private AtomicLong atomicLongSeqNo = new AtomicLong(0);

    public void setProcessorMap(Map processerMap){
        ClientMsgProcessorPool.getSingleton().setProcesserMap(processerMap);
    }

    public void setReadIdle(int readIdleTime, IIdleHandler readIdleHandler){
        ClientIdleHandlerPool.getSingleton().setReadIdleTime(readIdleTime);
        ClientIdleHandlerPool.getSingleton().setReadIdleHandler(readIdleHandler);
    }

    public void setWriteIdle(int writeIdleTime, IIdleHandler writeIdleHandler){
        ClientIdleHandlerPool.getSingleton().setWriteIdleTime(writeIdleTime);
        ClientIdleHandlerPool.getSingleton().setWriteIdleHandler(writeIdleHandler);
    }

    public void setAllIdle(int allIdleTime, IIdleHandler allIdleHandler){
        ClientIdleHandlerPool.getSingleton().setAllIdleTime(allIdleTime);
        ClientIdleHandlerPool.getSingleton().setAllIdleHandler(allIdleHandler);
    }

    /**
     * 同步往admin发消息
     *
     * @param cmdMsg
     * @throws Exception
     */
    public JanusCmdMsg syncSend(String remoteAddress, JanusCmdMsg cmdMsg) throws Exception {
        NettyMsg requestCmdMsg = new NettyMsg();
        requestCmdMsg.setCmdMsg(cmdMsg);
        requestCmdMsg.setSync(true);
        requestCmdMsg.setTranceId(String.format("%s-%d", LocalDateTime.now().toString(), atomicLongSeqNo.getAndIncrement()));

        CmdFutureResponse futureResponse = new CmdFutureResponse(requestCmdMsg);
        CmdFutureResponsePool.getSingleton().setInvokerFuture(requestCmdMsg.getTranceId(), futureResponse);
        ConnectClientPool.getSingleton().asyncSend(requestCmdMsg, remoteAddress);

        //此处block
        NettyMsg repCmdMsg = futureResponse.get(SYNC_TIME_OUT, TimeUnit.MILLISECONDS);
        if (repCmdMsg == null) {
            throw new JanusCmdException("同步发送应答为Null");
        }
        return repCmdMsg.getCmdMsg();
    }

    public void asyncSend(String remoteAddress, JanusCmdMsg cmdMsg) throws Exception {
        NettyMsg nettyMsg = new NettyMsg();
        nettyMsg.setCmdMsg(cmdMsg);
        nettyMsg.setSync(false);
        nettyMsg.setTranceId(String.format("%s-%d", LocalDateTime.now().toString(), atomicLongSeqNo.getAndIncrement()));
        ConnectClientPool.getSingleton().asyncSend(nettyMsg, remoteAddress);
    }
}

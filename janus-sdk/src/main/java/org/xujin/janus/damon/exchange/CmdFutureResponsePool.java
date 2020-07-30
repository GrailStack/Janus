package org.xujin.janus.damon.exchange;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;


@Slf4j
public class CmdFutureResponsePool {

    private static class Holder {
        private static final CmdFutureResponsePool CMD_FUTURE_RESPONSE_POOL = new CmdFutureResponsePool();
    }

    public static CmdFutureResponsePool getSingleton(){
        return CmdFutureResponsePool.Holder.CMD_FUTURE_RESPONSE_POOL;
    }

    private CmdFutureResponsePool()
    {
    }

    private ConcurrentMap<String, CmdFutureResponse> futureResponsePool = new ConcurrentHashMap<>();

    public void setInvokerFuture(String tranceId, CmdFutureResponse futureResponse){
        futureResponsePool.put(tranceId, futureResponse);
    }

    public void removeInvokerFuture(String tranceId){
        futureResponsePool.remove(tranceId);
    }

    public void notifyInvokerFuture(String tranceId, final NettyMsg msg){

        final CmdFutureResponse futureResponse = futureResponsePool.get(tranceId);
        if (futureResponse == null) {
            return;
        }
        futureResponse.setResponseAndNotify(msg);
        // do remove
        futureResponsePool.remove(tranceId);

    }
}

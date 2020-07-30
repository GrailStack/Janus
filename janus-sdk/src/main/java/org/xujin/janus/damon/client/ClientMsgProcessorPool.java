package org.xujin.janus.damon.client;

import org.xujin.janus.damon.exchange.JanusCmdMsg;
import org.xujin.janus.damon.processer.ICmdMsgProcessor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author tbkk 2019-10-19
 */
@Slf4j
public class ClientMsgProcessorPool {

    private static class Holder {
        private static final ClientMsgProcessorPool connectClientPool = new ClientMsgProcessorPool();
    }


    public static ClientMsgProcessorPool getSingleton() {
        return Holder.connectClientPool;
    }

    private ClientMsgProcessorPool() {
    }


    @Setter
    private Map<String, ICmdMsgProcessor> processerMap = new ConcurrentHashMap<>();

    public void invokeProcessor(JanusCmdMsg msg) {
        if (processerMap.containsKey(msg.getMethod())) {
            processerMap.get(msg.getMethod()).execute(msg);
        }
    }
}

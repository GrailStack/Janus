package org.xujin.janus.damon.client;

import org.xujin.janus.damon.idle.IIdleHandler;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

/**
 * @author tbkk 2019-10-19
 */
@Slf4j
@Data
public class ClientIdleHandlerPool {

    private static class Holder {
        private static final ClientIdleHandlerPool connectClientPool = new ClientIdleHandlerPool();
    }


    public static ClientIdleHandlerPool getSingleton() {
        return Holder.connectClientPool;
    }

    private ClientIdleHandlerPool() {
    }

    private int readIdleTime = 0;
    private IIdleHandler readIdleHandler;

    private int writeIdleTime = 0;
    private IIdleHandler writeIdleHandler;

    private int allIdleTime = 0;
    private IIdleHandler allIdleHandler;

}

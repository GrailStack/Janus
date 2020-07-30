package org.xujin.janus.damon.utils;

import org.xujin.janus.damon.exception.JanusCmdException;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author tbkk 2019-02-18
 */
public class ThreadPoolUtil {

    /**
     * make server thread pool
     *
     * @param serverType
     * @return
     */
    public static ThreadPoolExecutor makeServerThreadPool(final String serverType){
        ThreadPoolExecutor serverHandlerPool = new ThreadPoolExecutor(
                60,
                300,
                60L,
                TimeUnit.SECONDS,
                new LinkedBlockingQueue<>(1000),
                r -> new Thread(r, "janus-cmd, "+serverType+"-serverHandlerPool-" + r.hashCode()),
                (r, executor) -> {
                    throw new JanusCmdException("janus-cmd "+serverType+" Thread pool is EXHAUSTED!");
                });		// default maxThreads 300, minThreads 60

        return serverHandlerPool;
    }

}

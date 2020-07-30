package org.xujin.janus.core.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @author: gan
 * @date: 2020/4/29
 */
public class ThreadFactoryHelper implements ThreadFactory {
    private static Logger log = LoggerFactory.getLogger(ThreadFactoryHelper.class);

    private final AtomicLong threadNumber = new AtomicLong(1);

    private final String namePrefix;

    private final boolean daemon;

    private static final ThreadGroup threadGroup = new ThreadGroup("Janus");

    public static ThreadGroup getThreadGroup() {
        return threadGroup;
    }

    public static ThreadFactory create(String namePrefix, boolean daemon) {
        return new ThreadFactoryHelper(namePrefix, daemon);
    }

    private ThreadFactoryHelper(String namePrefix, boolean daemon) {
        this.namePrefix = namePrefix;
        this.daemon = daemon;
    }

    @Override
    public Thread newThread(Runnable r) {
        Thread thread = new Thread(threadGroup, r,
                threadGroup.getName() + "-" + namePrefix + "-" + threadNumber.getAndIncrement());
        thread.setDaemon(daemon);
        if (thread.getPriority() != Thread.NORM_PRIORITY) {
            thread.setPriority(Thread.NORM_PRIORITY);
        }
        return thread;
    }
}

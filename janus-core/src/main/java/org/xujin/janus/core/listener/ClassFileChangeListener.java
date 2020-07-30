package org.xujin.janus.core.listener;

import org.xujin.janus.config.observer.ClassFileChangedObserver;
import org.xujin.janus.core.dynamic.DynamicFileManger;

/**
 * @author: gan
 * @date: 2020/5/28
 */
public class ClassFileChangeListener {
    public static void listen() {
        ClassFileChangedObserver.addListener(e -> {
            DynamicFileManger.doPoll();
        });
    }
}

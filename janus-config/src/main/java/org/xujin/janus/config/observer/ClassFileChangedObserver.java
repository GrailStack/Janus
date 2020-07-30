package org.xujin.janus.config.observer;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Consumer;

/**
 * @author: gan
 * @date: 2020/5/28
 */
public class ClassFileChangedObserver {
    private static List<Consumer> classFileConsumers = new CopyOnWriteArrayList<>();

    public static void addListener(Consumer consumer) {
        classFileConsumers.add(consumer);
    }

    public static void notifyListeners() {
        classFileConsumers.stream().forEach(consumer -> consumer.accept(null));
    }
}

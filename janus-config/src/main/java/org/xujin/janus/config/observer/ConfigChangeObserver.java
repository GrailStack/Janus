package org.xujin.janus.config.observer;

import org.xujin.janus.client.cmo.ConfigChangeCmd;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Consumer;

/**
 * @author: gan
 * @date: 2020/5/20
 */
public class ConfigChangeObserver {
    private static List<Consumer<ConfigChangeCmd>> configConsumers = new CopyOnWriteArrayList<>();

    public static void addListener(Consumer<ConfigChangeCmd> consumer) {
        configConsumers.add(consumer);
    }

    public static void notifyListeners(ConfigChangeCmd configChangeCmd) {
        configConsumers.stream().forEach(consumer -> consumer.accept(configChangeCmd));
    }
}

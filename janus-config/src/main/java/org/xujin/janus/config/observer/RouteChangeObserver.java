package org.xujin.janus.config.observer;

import org.xujin.janus.client.cmo.RouteChangeCmd;
import org.xujin.janus.client.cmo.RouteChangeDTO;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Consumer;

/**
 * @author: gan
 * @date: 2020/5/20
 */
public class RouteChangeObserver {
    private static List<Consumer<RouteChangeDTO>> configConsumers = new CopyOnWriteArrayList<>();

    public static void addListener(Consumer<RouteChangeDTO> consumer) {
        configConsumers.add(consumer);
    }

    public static void notifyListeners(RouteChangeDTO routeChangeCmd) {
        configConsumers.stream().forEach(consumer -> consumer.accept(routeChangeCmd));
    }
}

package org.xujin.janus.config.admin.processer;

import org.xujin.janus.client.cmo.RouteChangeCmd;
import org.xujin.janus.config.observer.RouteChangeObserver;
import org.xujin.janus.damon.exchange.JanusCmdMsg;

/**
 * Janus Server提供给Admin的配置下发接口
 * @author xujin
 */
public class RouteChangeProcessor extends AbstractProcessor {
    @Override
    public JanusCmdMsg doExecute(Object payload) {
        if (payload == null) {
            throw new RuntimeException("payload cannot be null");
        }
        RouteChangeCmd routeChangeCmd = (RouteChangeCmd) payload;
        if (routeChangeCmd == null || routeChangeCmd.getRouteChangeDTOS().isEmpty()) {
            return successResponse();
        }
        routeChangeCmd.getRouteChangeDTOS().stream().forEach(e -> RouteChangeObserver.notifyListeners(e));
        return successResponse();
    }
}

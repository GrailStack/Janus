package org.xujin.janus.config.app;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * @author: gan
 * @date: 2020/4/17
 */
public class RoutesConfig {
    private List<RouteConfig> routes = new CopyOnWriteArrayList<>();

    public void setRoutes(List<RouteConfig> routes) {
        this.routes = routes;
    }

    public List<RouteConfig> getRoutes() {
        return routes;
    }

    public RouteConfig getRoute(String routeId) {
        return routes.stream().filter(r -> r.getId().equalsIgnoreCase(routeId)).findAny().orElse(null);
    }

    public void removeRoute(String routeId) {
        RouteConfig routeConfigFind = getRoute(routeId);
        routes.remove(routeConfigFind);
    }

    public void updateRoute(RouteConfig routeConfig) {
        RouteConfig routeConfigFind = getRoute(routeConfig.getId());
        //id、order cannot update
        if (routeConfig.getProtocol() != null) {
            routeConfigFind.setProtocol(routeConfig.getProtocol());
        }
        if (routeConfig.getLoadBalancerName() != null) {
            routeConfigFind.setLoadBalancerName(routeConfig.getLoadBalancerName());
        }
        if (routeConfig.getMetadata() != null) {
            routeConfigFind.setMetadata(routeConfig.getMetadata());
        }
        if (routeConfig.getFilters() != null) {
            routeConfigFind.setFilters(routeConfig.getFilters());
        }
        if (routeConfig.getPredicates() != null) {
            routeConfigFind.setPredicates(routeConfig.getPredicates());
        }
        if (routeConfig.getServiceName() != null) {
            routeConfigFind.setServiceName(routeConfig.getServiceName());
        }

        if (routeConfig.getHosts() != null) {
            routeConfigFind.setHosts(routeConfig.getHosts());
        }
    }
}

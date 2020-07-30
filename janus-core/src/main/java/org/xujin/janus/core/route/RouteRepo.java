package org.xujin.janus.core.route;

import org.xujin.janus.monitor.JanusMetrics;
import org.xujin.janus.monitor.constant.MetricsConst;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @author: gan
 * @date: 2020/4/17
 */
public class RouteRepo {
    private static final Logger logger = LoggerFactory.getLogger(RouteRepo.class);
    /**
     * route repo
     */
    private static Set<Route> allRoutes = new CopyOnWriteArraySet<>();
    private static AtomicLong routeCount = new AtomicLong();

    public static void init() {
        RouteLoader.loadFromConfig();
        JanusMetrics.gauge(MetricsConst.METRIC_ROUTE_COUNT, null, routeCount);
    }

    public static void add(Route route) {
        Route find = get(route.getId());
        if (find != null) {
            throw new RuntimeException("routeId:" + route.getId() + " already exist ");
        }
        allRoutes.add(route);
        routeCount.incrementAndGet();
    }

    public static void add(List<Route> routes) {
        if (routes == null || routes.size() <= 0) {
            return;
        }
        routes.stream().forEach(e -> add(e));
    }

    public static void remove(String routeId) {
        if (routeId == null) {
            throw new NullPointerException("routeId is null for remove");
        }
        Route toRemoveRoute = get(routeId);
        if (toRemoveRoute != null) {
            allRoutes.remove(toRemoveRoute);
            routeCount.decrementAndGet();
        } else {
            throw new RuntimeException("routeId:" + routeId + " not exist");
        }
    }

    public static Route get(String routeId) {
        return allRoutes.stream()
                .filter(e -> e.getId().equalsIgnoreCase(routeId))
                .findAny()
                .orElse(null);
    }

    public static void update(Route route) {
        if (route == null) {
            throw new NullPointerException("route is null for update");
        }
        Route toUpdateRoute = get(route.getId());
        if (toUpdateRoute != null) {
            if (route.getProtocol() != null) {
                toUpdateRoute.setProtocol(route.getProtocol());
            }
            if (route.getLoadBalancer() != null) {
                toUpdateRoute.setLoadBalancer(route.getLoadBalancer());
            }
            if (route.getMetadata() != null) {
                toUpdateRoute.setMetadata(route.getMetadata());
            }
            if (route.getFilters() != null) {
                toUpdateRoute.setFilters(route.getFilters());
            }
            if (route.getPredicate() != null) {
                toUpdateRoute.setPredicate(route.getPredicate());
            }

            if (route.getServiceName() != null) {
                toUpdateRoute.setServiceName(route.getServiceName());
            }

            if (route.getHosts() != null) {
                toUpdateRoute.setHosts(route.getHosts());
            }
        } else {
            throw new RuntimeException("routeId:" + route.getId() + " not exist for update");
        }
    }

    public static Set<Route> getAllRoutes() {
        return allRoutes;
    }

}

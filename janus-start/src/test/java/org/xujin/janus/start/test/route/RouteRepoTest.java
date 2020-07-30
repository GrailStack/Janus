package org.xujin.janus.start.test.route;

import org.xujin.janus.core.route.Route;
import org.xujin.janus.core.route.RouteRepo;
import org.xujin.janus.core.route.RouteLoader;
import org.junit.Assert;
import org.junit.Test;

import java.net.URI;

/**
 * @author: gan
 * @date: 2020/5/9
 */
public class RouteRepoTest {
    @Test(expected = RuntimeException.class)
    public void testRemoveRouteNotExist() {
        RouteRepo.remove("route2");
    }
    @Test(expected = Test.None.class)
    public void testRemoveRoute() {
        RouteLoader.loadFromConfig();
        RouteRepo.remove("route2");
    }

    @Test
    public void testUpdateRoute() {
        RouteLoader.loadFromConfig();
        Route.Builder builder = new Route.Builder();
        builder.loadBalancer(null)
                .filters(null)
                .predicate(null)
                .id("route2")
                .metadata(null)
                .order(-1)
                .protocol("https");
        Route route = builder.build();
        RouteRepo.update(route);
        Route routeFind = RouteRepo.getAllRoutes().stream()
                .filter(e -> e.getId().equalsIgnoreCase("route2")).findFirst().orElse(null);
        //Assert.assertTrue(routeFind.getProtocol()).toString().equalsIgnoreCase("https://baidu.com"));
    }
}

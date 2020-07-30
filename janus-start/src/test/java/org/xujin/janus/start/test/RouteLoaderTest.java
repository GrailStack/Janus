package org.xujin.janus.start.test;

import org.xujin.janus.core.route.Route;
import org.xujin.janus.core.route.RouteRepo;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

/**
 * @author: gan
 * @date: 2020/4/18
 */
public class RouteLoaderTest {
    @Rule
    public ExpectedException exception = ExpectedException.none();

    @Test(expected = Test.None.class)
    public void testError_empty_all_args() {
        RouteRepo routeRepo = new RouteRepo();
        routeRepo.add((Route) null);
    }

}

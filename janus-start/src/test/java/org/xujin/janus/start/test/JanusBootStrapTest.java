package org.xujin.janus.start.test;


import org.junit.Test;
import org.xujin.janus.startup.JanusBootStrap;

/**
 * @author: gan
 * @date: 2020/4/20
 */
public class JanusBootStrapTest {

    @Test(expected = Test.None.class)
    public void testInitRoutes() {
        JanusBootStrap.initGateway();
        JanusBootStrap.destroy();
    }
}

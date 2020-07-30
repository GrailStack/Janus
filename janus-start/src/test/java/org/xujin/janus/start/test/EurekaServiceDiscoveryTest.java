package org.xujin.janus.start.test;

import org.xujin.janus.registry.ServerNode;
import org.xujin.janus.registry.eurake.EurekaRegistryServiceImpl;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

/**
 * @author: gan
 * @date: 2020/4/21
 */
public class EurekaServiceDiscoveryTest {
    EurekaRegistryServiceImpl eurekaServiceDiscovery;

    @Before
    public void init() throws Exception {
        eurekaServiceDiscovery = new EurekaRegistryServiceImpl();
        eurekaServiceDiscovery.initialize("http://eureka.springcloud.cn/eureka","janus","127.0.0.1",8080,null);
    }

    @Test
    public void testGetService() {
        List<ServerNode> serverNodeList = eurekaServiceDiscovery.getServerNode("janus-admin");
        serverNodeList.forEach(e -> System.out.println(e.getHost() + ":" + e.getPort() + ";" + e.getServiceName()));
        Assert.assertTrue(serverNodeList.size() > 0);
    }
}

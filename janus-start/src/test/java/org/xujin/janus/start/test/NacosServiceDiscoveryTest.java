package org.xujin.janus.start.test;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.xujin.janus.registry.ServerNode;
import org.xujin.janus.registry.eurake.EurekaRegistryServiceImpl;
import org.xujin.janus.registry.nacos.NacosRegistryServiceImpl;

import java.util.List;

/**
 * @author: gan
 * @date: 2020/4/21
 */
public class NacosServiceDiscoveryTest {
    NacosRegistryServiceImpl nacosRegistryService;

    @Before
    public void init() throws Exception {
        nacosRegistryService = new NacosRegistryServiceImpl();
        nacosRegistryService.initialize("http://127.0.0.1:8848","janus","127.0.0.1",8080,null);
    }

    @Test
    public void testGetService() {
        List<ServerNode> serverNodeList = nacosRegistryService.getServerNode("janus");
        serverNodeList.forEach(e -> System.out.println(e.getHost() + ":" + e.getPort() + ";" + e.getServiceName()));
        Assert.assertTrue(serverNodeList.size() > 0);
    }
}

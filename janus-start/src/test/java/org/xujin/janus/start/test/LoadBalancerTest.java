package org.xujin.janus.start.test;


import org.xujin.janus.registry.ServerNode;
import org.xujin.janus.registry.loadbalancer.IpConsistentHashLoadBalancer;
import org.xujin.janus.registry.loadbalancer.LoadBalancerParam;
import org.xujin.janus.registry.loadbalancer.RandomLoadBalancer;
import org.xujin.janus.registry.loadbalancer.RoundRobinLoadBalancer;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

/**
 * @author: gan
 * @date: 2020/4/21
 */
public class LoadBalancerTest {
    @Test
    public void testRoundRobin() {
        List<ServerNode> serverNode = mockServerNode();
        RoundRobinLoadBalancer roundRobinLoadBalance = RoundRobinLoadBalancer.instance();
        for (int i = 0; i < 10; i++) {
            String server = roundRobinLoadBalance.select(serverNode).getHost();
            System.out.println(server);
            Assert.assertTrue(server != null);
        }
    }

    @Test
    public void testRandom() {
        List<ServerNode> serverNode = mockServerNode();
        RandomLoadBalancer randomLoadBalance = RandomLoadBalancer.instance();
        for (int i = 0; i < 10; i++) {
            String server = randomLoadBalance.select(serverNode).getHost();
            System.out.println(server);
            Assert.assertTrue(server != null);
        }
    }

    @Test
    public void testIpHash() {
        List<ServerNode> serverNode = mockServerNode();
        LoadBalancerParam loadBalancerParam = new LoadBalancerParam("127.0.0.110", 80);
        IpConsistentHashLoadBalancer randomLoadBalance = IpConsistentHashLoadBalancer.instance();
        String serverFirst = randomLoadBalance.select(serverNode, loadBalancerParam).getHost();
        for (int i = 0; i < 10; i++) {
            String server = randomLoadBalance.select(serverNode, loadBalancerParam).getHost();
            System.out.println(server);
            Assert.assertTrue(serverFirst.equalsIgnoreCase(server));

        }
    }

    private List<ServerNode> mockServerNode() {
        ServerNode serverNode1 = new ServerNode("mock", "127.0.0.1");
        ServerNode serverNode2 = new ServerNode("mock", "127.0.0.2");
        ServerNode serverNode3 = new ServerNode("mock", "127.0.0.3");
        ServerNode serverNode4 = new ServerNode("mock", "127.0.0.4");
        ServerNode serverNode5 = new ServerNode("mock", "127.0.0.5");
        ServerNode serverNode6 = new ServerNode("mock", "127.0.0.6");
        ServerNode serverNode7 = new ServerNode("mock", "127.0.0.7");

        List<ServerNode> serverNodes = new ArrayList<>();
        serverNodes.add(serverNode1);
        serverNodes.add(serverNode2);
        serverNodes.add(serverNode3);
        serverNodes.add(serverNode4);
        serverNodes.add(serverNode5);
        serverNodes.add(serverNode6);
        serverNodes.add(serverNode7);
        return serverNodes;

    }
}

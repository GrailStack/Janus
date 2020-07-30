package org.xujin.janus.registry.loadbalancer;

import org.xujin.janus.registry.ServerNode;


import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

/**
 * @author: gan
 * @date: 2020/4/21
 */
public class RandomLoadBalancer implements LoadBalancer {
    private static class InstanceHolder {
        private static final RandomLoadBalancer INSTANCE = new RandomLoadBalancer();
    }

    private RandomLoadBalancer() {
    }

    public static RandomLoadBalancer instance() {
        return InstanceHolder.INSTANCE;
    }

    @Override
    public ServerNode select(List<ServerNode> serverNode) {
        final int len = serverNode.size();
        if (len > 0) {
            return serverNode.get(ThreadLocalRandom.current().nextInt(len));
        }
        return null;
    }

    @Override
    public ServerNode select(List<ServerNode> serverNode, LoadBalancerParam loadBalancerParam) {
        return select(serverNode);
    }
}

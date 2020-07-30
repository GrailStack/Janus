package org.xujin.janus.registry.loadbalancer;

import org.xujin.janus.registry.ServerNode;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Round robin load balance.
 *
 * @author: gan
 * @date: 2020/4/21
 */
public class RoundRobinLoadBalancer implements LoadBalancer {
    private static class InstanceHolder {
        private static final RoundRobinLoadBalancer INSTANCE = new RoundRobinLoadBalancer();
    }

    private RoundRobinLoadBalancer() {

    }

    public static RoundRobinLoadBalancer instance() {
        return InstanceHolder.INSTANCE;
    }

    private AtomicInteger currentIndex = new AtomicInteger(0);

    @Override
    public ServerNode select(List<ServerNode> serverNode) {
        final int len = serverNode.size();
        if (len > 0) {
            int index = currentIndex.getAndIncrement() % len;
            if (index < 0) {
                currentIndex.set(0);
                index = currentIndex.getAndIncrement();
            }
            return serverNode.get(index);
        }
        return null;
    }

    @Override
    public ServerNode select(List<ServerNode> serverNode, LoadBalancerParam loadBalancerParam) {
        return select(serverNode);
    }
}

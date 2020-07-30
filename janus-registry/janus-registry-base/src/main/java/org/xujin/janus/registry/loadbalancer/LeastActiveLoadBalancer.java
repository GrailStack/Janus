package org.xujin.janus.registry.loadbalancer;

import org.xujin.janus.registry.ServerNode;


import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * TODO
 *
 * @author: gan
 * @date: 2020/4/25
 */
public class LeastActiveLoadBalancer implements LoadBalancer {
    /**
     * key: serverNode
     * value: active connect count on this server
     * process:
     * serverNode selected count++
     * receiveData from this serverNode count--
     */
    private static Map<ServerNode, AtomicInteger> sessionCount = new ConcurrentHashMap<>();

    @Override
    public ServerNode select(List<ServerNode> serverNode) {
        throw new UnsupportedOperationException("unsupported now");
    }

    @Override
    public ServerNode select(List<ServerNode> serverNode, LoadBalancerParam loadBalancerParam) {
        return select(serverNode);
    }
}

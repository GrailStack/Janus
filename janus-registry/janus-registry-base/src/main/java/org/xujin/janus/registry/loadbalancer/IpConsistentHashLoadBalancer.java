package org.xujin.janus.registry.loadbalancer;

import org.xujin.janus.registry.ServerNode;


import java.util.List;

/**
 * @author: gan
 * @date: 2020/4/24
 */
public class IpConsistentHashLoadBalancer implements LoadBalancer {
    private static class InstanceHolder {
        private static final IpConsistentHashLoadBalancer INSTANCE = new IpConsistentHashLoadBalancer();
    }

    private IpConsistentHashLoadBalancer() {

    }

    public static IpConsistentHashLoadBalancer instance() {
        return InstanceHolder.INSTANCE;
    }

    @Override
    public ServerNode select(List<ServerNode> serverNode) {
        return select(serverNode, null);
    }

    @Override
    public ServerNode select(List<ServerNode> serverNode, LoadBalancerParam loadBalancerParam) {
        if (loadBalancerParam == null || loadBalancerParam.getIp() == null) {
            throw new IllegalArgumentException("IpConsistentHash must has ip param");
        }
        long hash = (loadBalancerParam.getIp() + ":" + loadBalancerParam.getPort()).hashCode();
        final int len = serverNode.size();
        if (len > 0) {
            long index = Math.abs(hash % len);
            return serverNode.get((int) index);
        }
        return null;
    }
}

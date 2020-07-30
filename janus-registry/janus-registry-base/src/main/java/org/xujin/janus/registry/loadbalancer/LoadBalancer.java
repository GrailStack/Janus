package org.xujin.janus.registry.loadbalancer;

import org.xujin.janus.registry.ServerNode;


import java.util.List;

/**
 * @author: gan
 * @date: 2020/4/21
 */
public interface LoadBalancer {

    /**
     * select one server from list
     *
     * @param serverNode
     * @return
     */
    ServerNode select(List<ServerNode> serverNode);

    /**
     * select with param
     *
     * @param serverNode
     * @param loadBalancerParam
     * @return
     */
    ServerNode select(List<ServerNode> serverNode, LoadBalancerParam loadBalancerParam);
}

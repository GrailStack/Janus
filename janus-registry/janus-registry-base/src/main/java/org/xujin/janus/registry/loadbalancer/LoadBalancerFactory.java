package org.xujin.janus.registry.loadbalancer;


/**
 * @author: gan
 * @date: 2020/4/21
 */
public class LoadBalancerFactory {
    public static final String ROUND_ROBIN_NAME = "Round-Robin";
    public static final String RANDOM_NAME = "Random";
    public static final String IP_CONSISTENT_HASH_NAME = "ip-consistent-hash";

    /**
     * create loadBalancer by name
     * default return RoundRobin
     *
     * @param name
     * @return
     */
    public static LoadBalancer create(String name) {
        if (name == null || name.isEmpty()) {
            return RoundRobinLoadBalancer.instance();
        } else if (ROUND_ROBIN_NAME.equalsIgnoreCase(name.trim())) {
            return RoundRobinLoadBalancer.instance();
        } else if (RANDOM_NAME.equalsIgnoreCase(name.trim())) {
            return RandomLoadBalancer.instance();
        } else if (IP_CONSISTENT_HASH_NAME.equalsIgnoreCase(name.trim())) {
            return IpConsistentHashLoadBalancer.instance();
        } else {
            throw new RuntimeException("unsupported serviceDiscovery " + name);
        }
    }
}

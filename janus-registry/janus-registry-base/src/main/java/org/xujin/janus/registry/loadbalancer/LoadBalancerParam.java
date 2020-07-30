package org.xujin.janus.registry.loadbalancer;

/**
 * @author: gan
 * @date: 2020/5/19
 */
public class LoadBalancerParam {
    private String ip;
    private Integer port;

    public LoadBalancerParam(String ip, Integer port) {
        this.ip = ip;
        this.port = port;
    }

    public String getIp() {
        return ip;
    }

    public Integer getPort() {
        return port;
    }
}

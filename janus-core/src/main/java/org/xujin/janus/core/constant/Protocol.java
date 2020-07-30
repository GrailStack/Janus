package org.xujin.janus.core.constant;

/**
 * @author: gan
 * @date: 2020/4/25
 */
public enum Protocol {

    HTTP("http协议","http"),
    HTTPS("https协议","https"),
    REST_LB_SC("Spring Cloud通过注册中心LB","lb://sc"),
    REST_LB_HOSTS("通过Ip列表LB","lb://hosts"),
    RPC_LB_DUBBO("dubbo通过注册中心LB","lb://dubbo"),
    RPC_LB_GRPC("grpc通过注册中心LB","lb://grpc");

    private  String name;
    private String value;

    Protocol(String name, String value) {
        this.name = name;
        this.value = value;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    private int defaultPort;

    Protocol(int defaultPort) {
        this.defaultPort = defaultPort;
    }

    public int getDefaultPort() {
        return this.defaultPort;
    }
}

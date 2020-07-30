package org.xujin.janus.registry;

import java.util.Objects;

/**
 * @author: gan
 * @date: 2020/4/21
 */
public class ServerNode {
    /**
     * The id of the registered service instance.
     *
     * @return nullable
     */
    private String id;

    /**
     * The name of service that current instance belongs to.
     *
     * @return non-null
     */
    private String serviceName;

    /**
     * The hostname of the registered service instance.
     *
     * @return non-null
     */
    private String host;

    /**
     * The port of the registered service instance.
     *
     * @return the positive integer if present
     */
    private Integer port;

    public ServerNode(String serviceName, String host) {
        if (serviceName == null || serviceName.isEmpty()) {
            throw new IllegalArgumentException("serviceName cannot be empty");
        }
        if (host == null || host.isEmpty()) {
            throw new IllegalArgumentException("host cannot be empty");
        }
        this.serviceName = serviceName;
        this.host = host;
    }

    public ServerNode( String host,int port) {
        if (host == null || host.isEmpty()) {
            throw new IllegalArgumentException("host cannot be empty");
        }
        this.host = host;
        this.port=port;
    }


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public Integer getPort() {
        return port;
    }

    public void setPort(Integer port) {
        this.port = port;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ServerNode)) {
            return false;
        }
        ServerNode that = (ServerNode) o;
        return getHost().equals(that.getHost()) &&
                getPort().equals(that.getPort());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getHost(), getPort());
    }
}

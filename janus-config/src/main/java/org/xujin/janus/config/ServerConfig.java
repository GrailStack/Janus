package org.xujin.janus.config;

import org.xujin.janus.config.app.DynamicClassConfig;
import org.xujin.janus.config.app.JanusInfluxConfig;
import org.xujin.janus.config.app.RouteConfig;
import org.xujin.janus.config.app.RoutesConfig;
import org.xujin.janus.config.netty.NettyClientConfig;
import org.xujin.janus.config.netty.NettyServerConfig;
import org.xujin.janus.config.util.CheckUtils;


import java.io.Serializable;


public class ServerConfig implements Serializable {


    private NettyServerConfig serverConf;
    private NettyClientConfig clientConf;
    private RoutesConfig routesConfig;
    private String[] globalFilters;
    private DynamicClassConfig dynamicClass;
    private JanusInfluxConfig janusInfluxConfig;

    public NettyServerConfig getServerConf() {
        return serverConf;
    }

    public void setServerConf(NettyServerConfig serverConf) {
        this.serverConf = serverConf;
    }

    public NettyClientConfig getClientConf() {
        return clientConf;
    }

    public void setClientConf(NettyClientConfig clientConf) {
        this.clientConf = clientConf;
    }


    public DynamicClassConfig getDynamicClass() {
        return dynamicClass;
    }

    public void setDynamicClass(DynamicClassConfig dynamicClassConfig) {
        this.dynamicClass = dynamicClassConfig;
    }

    public RoutesConfig getRoutesConfig() {
        return routesConfig;
    }

    public void setRoutesConfig(RoutesConfig routeConfigList) {
        this.routesConfig = routeConfigList;
    }

    public String[] getGlobalFilters() {
        return globalFilters;
    }

    public void setGlobalFilters(String[] globalFilters) {
        this.globalFilters = globalFilters;
    }

    public JanusInfluxConfig getJanusInfluxConfig() {
        return janusInfluxConfig;
    }

    public void setJanusInfluxConfig(JanusInfluxConfig janusInfluxConfig) {
        this.janusInfluxConfig = janusInfluxConfig;
    }

    public RouteConfig getRouteConfig(String routeId) {
        if (this.routesConfig == null) {
            return null;
        }
        return routesConfig.getRoute(routeId);
    }

    public void checkMember() {
        CheckUtils.checkNotNull(this.getServerConf(), "serverConf cannot null");
        CheckUtils.checkNotNull(this.getClientConf(), "clientConf cannot null");
        CheckUtils.checkNotNull(this.getDynamicClass(), "dynamicClass cannot null");
    }
}

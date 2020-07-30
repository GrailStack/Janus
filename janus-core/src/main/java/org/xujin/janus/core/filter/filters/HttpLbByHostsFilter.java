package org.xujin.janus.core.filter.filters;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xujin.janus.core.constant.Protocol;
import org.xujin.janus.core.context.FilterContext;
import org.xujin.janus.core.route.Route;
import org.xujin.janus.core.util.UriBuilder;
import org.xujin.janus.registry.ServerNode;
import org.xujin.janus.registry.loadbalancer.LoadBalancer;
import org.xujin.janus.registry.loadbalancer.LoadBalancerParam;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

/**
 * 不走服务注册与发现转发后端Http请求
 * @author xujin
 */
public class HttpLbByHostsFilter extends AbstractHttpInvokeFilter {

    private static final Logger logger = LoggerFactory.getLogger(HttpLbByHostsFilter.class);
    private static final String backendProtocol = Protocol.HTTP.toString();

    @Override
    public void doFilter(FilterContext context) {
        Route route= getRoute(context);
        String protocol = getRoute(context).getProtocol();
        if (!Protocol.REST_LB_HOSTS.getValue().equalsIgnoreCase(protocol)) {
            skipInvoke(context);
            return;
        }
        LoadBalancer loadBalancer = route.getLoadBalancer();
        ServerNode serverNode = lookUpServer(loadBalancer, route, context);
        String requestUriStr = context.getCtx().getOriFullHttpRequest().uri();
        URI requestUri = URI.create(requestUriStr);
        URI newRequestUri = UriBuilder.from(requestUri)
                .replaceScheme(backendProtocol)
                .replaceHost(serverNode.getHost())
                .replacePort(serverNode.getPort())
                .build();
        context.getCtx().getOriFullHttpRequest().setUri(newRequestUri.toString());
        invoke(context);
    }
    @Override
    public int order() {
        return 40;
    }

    private ServerNode lookUpServer(LoadBalancer loadBalancer, Route route, FilterContext httpContext) {
        LoadBalancerParam loadBalancerParam = new LoadBalancerParam(httpContext.getCtx().getClientIp()
                , httpContext.getCtx().getClientPort());
        List<String> hosts=route.getHosts();
        List<ServerNode> serverNodes = new ArrayList<>();
        for(String host:hosts){
            String [] hostStr=host.split(":");
            ServerNode serverNode=new ServerNode(hostStr[0],Integer.valueOf(hostStr[1]).intValue());
            serverNodes.add(serverNode);
        }
        ServerNode serverNode = loadBalancer.select(serverNodes, loadBalancerParam);
        if (serverNode == null) {
            throw new RuntimeException("no server found for " + route.getServiceName());
        }
        return serverNode;
    }
}

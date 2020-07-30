package org.xujin.janus.core.filter.filters;

import org.xujin.janus.core.constant.Protocol;
import org.xujin.janus.core.context.FilterContext;
import org.xujin.janus.core.route.Route;
import org.xujin.janus.registry.RegistryServiceRepo;
import org.xujin.janus.registry.loadbalancer.LoadBalancer;
import org.xujin.janus.core.util.UriBuilder;
import org.xujin.janus.registry.RegistryService;
import org.xujin.janus.registry.ServerNode;
import org.xujin.janus.registry.loadbalancer.LoadBalancerParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.util.List;

/**
 * Invoke HttpFilter
 * dispatch load balance request to other service
 * get service address from config center
 *
 * @author: gan
 * @date: 2020/4/21
 */
public class LBHttpFilter extends AbstractHttpInvokeFilter {
    private static final Logger logger = LoggerFactory.getLogger(LBHttpFilter.class);
    private static final String backendProtocol = Protocol.HTTP.toString();

    @Override
    public void doFilter(FilterContext context) {
        Route route=getRoute(context);
        String protocol = route.getProtocol();
        LoadBalancer loadBalancer = route.getLoadBalancer();
        if (!Protocol.REST_LB_SC.getValue().equalsIgnoreCase(protocol)) {
            skipInvoke(context);
            return;
        }
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

    private ServerNode lookUpServer(LoadBalancer loadBalancer, Route route, FilterContext httpContext) {
        RegistryService serviceDiscovery = RegistryServiceRepo.getRegistryService();
        LoadBalancerParam loadBalancerParam = new LoadBalancerParam(httpContext.getCtx().getClientIp()
                , httpContext.getCtx().getClientPort());
        List<ServerNode> serverNodes = serviceDiscovery.getServerNode(route.getServiceName());
        ServerNode serverNode = loadBalancer.select(serverNodes, loadBalancerParam);
        if (serverNode == null) {
            throw new RuntimeException("no server found for " + route.getServiceName());
        }
        return serverNode;
    }

    @Override
    public int order() {
        return 40;
    }
}

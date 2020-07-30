package org.xujin.janus.core.route;


import org.xujin.janus.config.app.RouteConfig;
import org.xujin.janus.core.context.FilterContext;
import org.xujin.janus.core.filter.Filter;
import org.xujin.janus.registry.loadbalancer.LoadBalancer;

import java.net.URI;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Predicate;

/**
 * @author: gan
 * @date: 2020/4/17
 */
public class Route {
    /**
     * route name
     */
    private String id;

    /**
     * 协议类型
     */
    private String protocol;

    /**
     * 如果协议是lb://sc,ServiceName就是服务名
     */
    private  String serviceName;


    private List<String> hosts;

    /**
     * route order
     */
    private int order;

    /**
     * route relate filters;not include global filter
     */
    private List<Filter> filters;

    /**
     * java predicate
     */
    private Predicate<FilterContext> predicate;

    /**
     * 负载均衡
     */
    private LoadBalancer loadBalancer;

    /**
     * extend properties
     */
    private Map<String, Object> metadata;


    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public List<String> getHosts() {
        return hosts;
    }

    public void setHosts(List<String> hosts) {
        this.hosts = hosts;
    }

    private Route(String id, String protocol, int order,
                  Predicate<FilterContext> predicate,
                  List<Filter> filters, Map<String, Object> metadata,
                  LoadBalancer loadBalancer,String serviceName,List<String> hosts) {
        this.id = id;
        this.protocol = protocol;
        this.order = order;
        this.predicate = predicate;
        this.filters = filters;
        this.metadata = metadata;
        this.loadBalancer = loadBalancer;
        this.serviceName=serviceName;
        this.hosts=hosts;
    }

    public String getId() {
        return id;
    }


    public int getOrder() {
        return order;
    }


    public List<Filter> getFilters() {
        return filters;
    }

    public String getProtocol() {
        return protocol;
    }

    public void setProtocol(String protocol) {
        this.protocol = protocol;
    }

    public LoadBalancer getLoadBalancer() {
        return loadBalancer;
    }


    public Predicate<FilterContext> getPredicate() {
        return predicate;
    }

    public Map<String, Object> getMetadata() {
        return metadata;
    }



    public void setFilters(List<Filter> filters) {
        this.filters = filters;
    }

    public void setPredicate(Predicate<FilterContext> predicate) {
        this.predicate = predicate;
    }

    public void setLoadBalancer(LoadBalancer loadBalancer) {
        this.loadBalancer = loadBalancer;
    }

    public void setMetadata(Map<String, Object> metadata) {
        this.metadata = metadata;
    }

    public void addFilter(Filter filter) {
        //1.find index to insert by order
        int index = 0;
        for (int i = 0; i < filters.size(); i++) {
            if (filters.get(i).type().getIndex() > filter.type().getIndex()) {
                index = i;
                break;
            }
            if (filters.get(i).order() >= filter.order()) {
                index = i;
                break;
            }
        }
        //2.add
        filters.add(index, filter);
    }

    public static Builder builder(RouteConfig routeConfig) {
        return new Builder().id(routeConfig.getId())
                .protocol(routeConfig.getProtocol())
                .order(routeConfig.getOrder())
                .serviceName(routeConfig.getServiceName())
                .hosts(routeConfig.getHosts())
                .metadata(routeConfig.getMetadata());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Route)) {
            return false;
        }
        Route route = (Route) o;
        return getId().equals(route.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId());
    }

    public static class Builder {
        private String id;
        private int order;
        private List<Filter> filters;
        private String protocol;
        private Predicate<FilterContext> predicate;
        private Map<String, Object> metadata;
        private LoadBalancer loadBalancer;
        private String serviceName;
        private List<String> hosts;

        public Builder id(String id) {
            this.id = id;
            return this;
        }

        public Builder order(int order) {
            this.order = order;
            return this;
        }

        public Builder filters(List<Filter> filters) {
            this.filters = filters;
            return this;
        }

        public Builder protocol(String protocol) {
            this.protocol = protocol;
            return this;
        }

        public Builder loadBalancer(LoadBalancer loadBalancer) {
            this.loadBalancer = loadBalancer;
            return this;
        }

        public Builder serviceName(String serviceName) {
            this.serviceName = serviceName;
            return this;
        }

        public Builder hosts(List<String> hosts) {
            this.hosts = hosts;
            return this;
        }

        public Builder metadata(Map<String, Object> metadata) {
            this.metadata = metadata;
            return this;
        }

        public Builder predicate(Predicate<FilterContext> predicate) {
            this.predicate = predicate;
            return this;
        }

        public Builder and(Predicate<FilterContext> predicate) {
            if (predicate == null) {
                throw new IllegalArgumentException("cannot call and() on null predicate");
            }
            this.predicate = this.predicate.and(predicate);
            return this;
        }

        public Builder or(Predicate<FilterContext> predicate) {
            if (predicate == null) {
                throw new IllegalArgumentException("cannot call or() on null predicate");
            }
            this.predicate = this.predicate.or(predicate);
            return this;
        }

        public Builder negate() {
            if (predicate == null) {
                throw new IllegalArgumentException("cannot call negate() on null predicate");
            }
            this.predicate = this.predicate.negate();
            return this;
        }

        public Route build() {
            return new Route(this.id, this.protocol, this.order, this.predicate, this.filters, this.metadata,
                    this.loadBalancer,this.serviceName,this.hosts);
        }
    }

}

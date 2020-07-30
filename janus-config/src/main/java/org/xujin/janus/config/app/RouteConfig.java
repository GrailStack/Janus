package org.xujin.janus.config.app;

import org.xujin.janus.config.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author: gan
 * @date: 2020/4/17
 */
public class RouteConfig {

    private String id;

    private List<PredicatesConfig> predicates;

    private List<FilterConfig> filters;

    private String serviceName;

    private String protocol;

    private Map<String, Object> metadata;

    private int order = 0;

    private String loadBalancerName;

    private List<String> hosts;

    public RouteConfig() {
    }

    /**
     * text should like: routeid=uri,predicates1=p1,predicates2=p2......
     *
     * @param text
     */
    public RouteConfig(String text) {
        int eqIdx = text.indexOf('=');
        if (eqIdx <= 0) {
            throw new IllegalArgumentException("Unable to parse RouteDefinition text '"
                    + text + "'" + ", must be of the form name=value");
        }
        setId(text.substring(0, eqIdx));

        String[] args = StringUtils.tokenizeToStringArray(text.substring(eqIdx + 1), ",");
        setProtocol(args[0]);
        this.predicates=new ArrayList<>();
        for (int i = 1; i < args.length; i++) {
            this.predicates.add(new PredicatesConfig(args[i]));
        }
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public List<PredicatesConfig> getPredicates() {
        return predicates;
    }

    public void setPredicates(List<PredicatesConfig> predicates) {
        this.predicates = predicates;
    }

    public List<FilterConfig> getFilters() {
        return filters;
    }

    public void setFilters(List<FilterConfig> filters) {
        this.filters = filters;
    }

    public String getProtocol() {
        return protocol;
    }

    public void setProtocol(String protocol) {
        this.protocol = protocol;
    }

    public int getOrder() {
        return order;
    }

    public void setOrder(int order) {
        this.order = order;
    }

    public Map<String, Object> getMetadata() {
        return metadata;
    }

    public void setMetadata(Map<String, Object> metadata) {
        this.metadata = metadata;
    }

    public String getLoadBalancerName() {
        return loadBalancerName;
    }

    public void setLoadBalancerName(String loadBalancerName) {
        this.loadBalancerName = loadBalancerName;
    }

    public FilterConfig getFilterConfig(String filterName) {
        return filters.stream().filter(f -> f.getName().equalsIgnoreCase(filterName.trim())).findAny().orElse(null);
    }

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
}

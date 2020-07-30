package org.xujin.janus.core.filter.filters;

import org.xujin.janus.config.ConfigRepo;
import org.xujin.janus.config.app.FilterConfig;
import org.xujin.janus.config.app.RouteConfig;
import org.xujin.janus.core.constant.SessionContextKey;
import org.xujin.janus.core.context.FilterContext;
import org.xujin.janus.core.filter.Filter;
import org.xujin.janus.core.route.Route;
import org.xujin.janus.core.util.NameUtils;

import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author: gan
 * @date: 2020/4/15
 */
public abstract class AbstractFilter implements Filter {
    private AtomicBoolean enable = new AtomicBoolean(true);

    @Override
    public final String name() {
        return NameUtils.normalizeFilterName(getClass());
    }

    @Override
    public boolean enabled() {
        return enable.compareAndSet(false, true);
    }

    @Override
    public boolean disabled() {
        return enable.compareAndSet(true, false);
    }

    @Override
    public boolean isEnable() {
        return this.enable.get();
    }

    @Override
    public Map<String, String> getConfig(String routeId) {
        Map<String, String> filterConfigMap = Collections.emptyMap();
        RouteConfig routeConfig = ConfigRepo.getServerConfig().getRouteConfig(routeId);
        if (routeConfig == null) {
            return filterConfigMap;
        }
        List<FilterConfig> filterConfigs = routeConfig.getFilters();
        if (filterConfigs == null) {
            return filterConfigMap;
        }
        FilterConfig filterConfig = filterConfigs.stream()
                .filter(config -> config.getName() != null)
                .filter(config -> this.name().equalsIgnoreCase(config.getName().trim()))
                .findAny()
                .orElse(null);
        return filterConfig == null ? filterConfigMap : filterConfig.getArgs();
    }

    /**
     * first,compare filter type index
     * then,compare filter order
     *
     * @param o
     * @return
     */
    @Override
    public int compareTo(Filter o) {
        if (type().getIndex() > o.type().getIndex()) {
            return 1;
        } else if (type().getIndex() < o.type().getIndex()) {
            return -1;
        } else {
            if (order() > o.order()) {
                return 1;
            } else if (order() < o.order()) {
                return -1;
            }
        }
        return 0;
    }

    /**
     * compare type and name
     *
     * @param o
     * @return
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof AbstractFilter)) {
            return false;
        }
        AbstractFilter that = (AbstractFilter) o;
        return Objects.equals(type(), that.type()) &&
                Objects.equals(name(), that.name());
    }

    @Override
    public int hashCode() {
        return Objects.hash(type(), name());
    }

    protected Route getRoute(FilterContext filterContext) {
        Route route = (Route) filterContext.getSessionContext().get(SessionContextKey.FILTER_ROUTE);
        if (route == null) {
            throw new RuntimeException("no route found in " + filterContext.toString());
        }
        return route;
    }

    protected Map<String, String> getConfig(FilterContext filterContext) {
        Route route = getRoute(filterContext);
        String routeId = route.getId();
        return getConfig(routeId);

    }

    protected String getAutoGenerateKey(Map<String, String> config) {
        if (config == null) {
            return null;
        }
        String autoGenKey = config.keySet().stream()
                .filter(e -> e.startsWith(FilterConfig.AUTO_GENERATE_ARG_KEY))
                .findFirst()
                .orElse(null);
        return autoGenKey;
    }
}

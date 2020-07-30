package org.xujin.janus.core.predicates;

import org.xujin.janus.config.ConfigRepo;
import org.xujin.janus.config.app.PredicatesConfig;
import org.xujin.janus.config.app.RouteConfig;
import org.xujin.janus.core.constant.SessionContextKey;
import org.xujin.janus.core.context.FilterContext;
import org.xujin.janus.core.route.Route;
import org.xujin.janus.core.util.NameUtils;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

/**
 * @author: gan
 * @date: 2020/4/17
 */
public abstract class AbstractPredicate implements Predicate<FilterContext> {

    /**
     * auto generate predicate name;
     * rule:remove Predicate end prefix
     * example:PathRegexPredicate name is Path
     *
     * @return
     */
    public String name() {
        return NameUtils.normalizePredicateName(getClass());
    }

    protected Route getRoute(FilterContext filterContext) {
        return (Route) filterContext.getSessionContext().get(SessionContextKey.PREDICATE_ROUTE);
    }

    protected Map<String, String> getPredicateConfig(FilterContext filterContext) {
        String routeId = getRoute(filterContext).getId();
        return getPredicateConfig(routeId);
    }

    protected Map<String, String> getPredicateConfig(String routeId) {
        Map<String, String> predicateConfigMap = Collections.EMPTY_MAP;
        RouteConfig routeConfig = ConfigRepo.getServerConfig().getRouteConfig(routeId);
        if (routeConfig == null) {
            return predicateConfigMap;
        }
        List<PredicatesConfig> predicateConfigs = routeConfig.getPredicates();
        if (predicateConfigs == null) {
            return predicateConfigMap;
        }
        PredicatesConfig predicatesConfig = predicateConfigs.stream()
                .filter(config -> config.getName() != null)
                .filter(config -> this.name().equalsIgnoreCase(config.getName().trim()))
                .findAny()
                .orElse(null);
        return predicatesConfig == null ? predicateConfigMap : predicatesConfig.getArgs();
    }

}

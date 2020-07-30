package org.xujin.janus.core.route;


import org.xujin.janus.config.ConfigRepo;
import org.xujin.janus.config.app.FilterConfig;
import org.xujin.janus.config.app.PredicatesConfig;
import org.xujin.janus.config.app.RouteConfig;
import org.xujin.janus.config.app.RoutesConfig;
import org.xujin.janus.core.predicates.PredicateRepo;
import org.xujin.janus.core.filter.Filter;
import org.xujin.janus.core.filter.FilterRepo;
import org.xujin.janus.core.filter.FilterSort;
import org.xujin.janus.registry.loadbalancer.LoadBalancer;
import org.xujin.janus.registry.loadbalancer.LoadBalancerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * load route from config file
 *
 * @author: gan
 * @date: 2020/4/29
 */
public class RouteLoader {
    private static final Logger logger = LoggerFactory.getLogger(RouteLoader.class);

    public static void loadFromConfig() {
        RoutesConfig routeConfigList = ConfigRepo.getServerConfig().getRoutesConfig();
        if (routeConfigList == null
                || routeConfigList.getRoutes() == null || routeConfigList.getRoutes().size() <= 0) {
            logger.debug("no route config found ");
            return;
        }
        //get route form routeConfig
        List<Route> allRoutes = routeConfigList.getRoutes().stream()
                .filter(e -> e.getId() != null)
                .map(e -> convertToRoute(e))
                .collect(Collectors.toList());
        RouteRepo.add(allRoutes);
    }

    public static Route convertToRoute(RouteConfig routeConfig) {
        if (routeConfig == null) {
            return null;
        }
        Predicate predicate = combinePredicate(routeConfig);
        List<Filter> filters = combineFilters(routeConfig);
        filters = FilterSort.sort(filters);
        String loadBalancerName = routeConfig.getLoadBalancerName();
        LoadBalancer loadBalancer = LoadBalancerFactory.create(loadBalancerName);
        Route route = Route.builder(routeConfig)
                .predicate(predicate)
                .filters(filters)
                .loadBalancer(loadBalancer)
                .metadata(routeConfig.getMetadata())
                .build();
        return route;

    }


    /**
     * combine system filter and user config filters
     *
     * @param routeConfig
     * @return
     */
    private static List<Filter> combineFilters(RouteConfig routeConfig) {
        List<Filter> resultFilter = new ArrayList<>();
        /**
         * system filter: the filters all request must execute
         */
        List<Filter> systemFilters = FilterRepo.getSystemFilters();
        resultFilter.addAll(systemFilters);
        List<Filter> globalFilters = getGlobalFilters();
        resultFilter.addAll(globalFilters);

        //get filters from config
        List<FilterConfig> filterConfigs = routeConfig.getFilters();
        if (filterConfigs == null) {
            return resultFilter;
        }
        List<Filter> configFilter = filterConfigs.stream()
                .map(e -> FilterRepo.get(e.getName()))
                .filter(e -> e != null)
                .collect(Collectors.toList());
        if (configFilter == null || configFilter.size() <= 0) {
            return resultFilter;
        }
        // if a filter config in global and private;use private
        resultFilter.addAll(configFilter);
        return resultFilter;
    }

    private static List<Filter> getGlobalFilters() {
        String[] globalFilterNames = ConfigRepo.getServerConfig().getGlobalFilters();
        List<Filter> globalFilters = new ArrayList<>();
        if (globalFilterNames == null || globalFilterNames.length <= 0) {
            return globalFilters;
        }
        for (int i = 0; i < globalFilterNames.length; i++) {
            Filter filter = FilterRepo.get(globalFilterNames[i]);
            if (filter != null) {
                globalFilters.add(filter);
            }
        }
        return globalFilters;
    }

    private static Predicate combinePredicate(RouteConfig routeConfig) {
        List<PredicatesConfig> predicatesConfigs = routeConfig.getPredicates();
        if (predicatesConfigs == null || predicatesConfigs.size() <= 0) {
            return null;
        }
        //get predicate from config
        Predicate predicateFirst = PredicateRepo.get(predicatesConfigs.get(0).getName());
        if (predicateFirst == null) {
            return null;
        }
        for (int i = 1; i < predicatesConfigs.size(); i++) {
            Predicate predicate = PredicateRepo.get(predicatesConfigs.get(i).getName());
            if (predicate != null) {
                predicateFirst = predicateFirst.and(predicate);
            }
        }
        return predicateFirst;
    }

}

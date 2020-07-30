package org.xujin.janus.core.listener;

import com.google.gson.Gson;
import org.xujin.janus.client.cmo.OperationEnum;
import org.xujin.janus.client.cmo.RouteChangeDTO;
import org.xujin.janus.client.cmo.RouteChangeTypeEnum;
import org.xujin.janus.config.ConfigRepo;
import org.xujin.janus.config.app.FilterConfig;
import org.xujin.janus.config.app.RouteConfig;
import org.xujin.janus.config.app.RoutesConfig;
import org.xujin.janus.config.observer.RouteChangeObserver;
import org.xujin.janus.config.util.CheckUtils;
import org.xujin.janus.core.filter.Filter;
import org.xujin.janus.core.filter.FilterRepo;
import org.xujin.janus.core.resilience.breaker.BreakerRepo;
import org.xujin.janus.core.resilience.breaker.CircuitBreakerFilter;
import org.xujin.janus.core.resilience.retry.RetryFilter;
import org.xujin.janus.core.resilience.retry.RetryRepo;
import org.xujin.janus.core.route.Route;
import org.xujin.janus.core.route.RouteLoader;
import org.xujin.janus.core.route.RouteRepo;
import org.xujin.janus.core.util.NameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.List;

/**
 * @author: gan
 * @date: 2020/5/26
 */
public class RouteChangeListener {
    private static final Logger logger = LoggerFactory.getLogger(RouteChangeListener.class);

    public static void listen() {
        RouteChangeObserver.addListener(routeChangeDTOS -> {
            logger.info("receive route change from admin,content:" + new Gson().toJson(routeChangeDTOS));

            CheckUtils.checkNotNull(routeChangeDTOS.getOperation(), "operation cannot be null");
            CheckUtils.checkNotNull(routeChangeDTOS.getRouteChangeType(), "changeType cannot be null");
            CheckUtils.checkNotNull(routeChangeDTOS.getDataJson(), "data cannot be null");
            //路由变化基本信息变化
            if (RouteChangeTypeEnum.BASE_CONFIG.equals(routeChangeDTOS.getRouteChangeType())) {
                changeBaseConfig(routeChangeDTOS);
                //路由的Filter配置变化
            } else if (RouteChangeTypeEnum.FILTER_CONFIG.equals(routeChangeDTOS.getRouteChangeType())) {
                changeFilterConfig(routeChangeDTOS);
            } else {
                throw new RuntimeException("unsupported change type");
            }
        });
    }

    /**
     * 处理路由配置变化,包括了Filter变化处理
     *
     * @param routeChangeCmd
     */
    private static void changeBaseConfig(RouteChangeDTO routeChangeCmd) {
        RouteConfig changedRouteConfig = new Gson().fromJson(routeChangeCmd.getDataJson(), RouteConfig.class);
        if (changedRouteConfig.getId() == null || changedRouteConfig.getId().isEmpty()) {
            CheckUtils.checkNonEmpty(routeChangeCmd.getRouteId(), "must has routeId");
            changedRouteConfig.setId(routeChangeCmd.getRouteId());
        }
        RoutesConfig routeConfigList = ConfigRepo.getServerConfig().getRoutesConfig();
        //容错
        if (routeConfigList == null) {
            routeConfigList = new RoutesConfig();
            ConfigRepo.getServerConfig().setRoutesConfig(routeConfigList);
        }
        removeRouteResilience(routeChangeCmd.getRouteId(), changedRouteConfig.getFilters());
        /**
         * 新增路由配置，增加到路由配置集合中去、增加到路由Repo中去
         */
        if (OperationEnum.ADD.equals(routeChangeCmd.getOperation())) {
            //add to config
            routeConfigList.getRoutes().add(changedRouteConfig);
            Route route = RouteLoader.convertToRoute(changedRouteConfig);
            RouteRepo.add(route);
        }
        /**
         * 更新路由配置，更新路由配置集合中的路由配置，更新路由Repo里的路由
         */
        else if (OperationEnum.UPDATE.equals(routeChangeCmd.getOperation())) {
            RouteConfig routeConfigFind = routeConfigList.getRoute(changedRouteConfig.getId());
            if (routeConfigFind == null) {
                throw new RuntimeException(changedRouteConfig.getId() + " not exist for update");
            }
            routeConfigList.updateRoute(changedRouteConfig);
            Route route = RouteLoader.convertToRoute(changedRouteConfig);
            RouteRepo.update(route);
        }
        /**
         * 删除路由，删除路由配置集合中的路由配置、删除路由Repo里的路由、删除路由对应的熔断器、重试器
         */
        else if (OperationEnum.DELETE.equals(routeChangeCmd.getOperation())) {
            RouteConfig routeConfigFind = routeConfigList.getRoute(changedRouteConfig.getId());
            if (routeConfigFind == null) {
                throw new RuntimeException(changedRouteConfig.getId() + " not exist for delete");
            }
            routeConfigList.removeRoute(changedRouteConfig.getId());
            RouteRepo.remove(changedRouteConfig.getId());

        } else {
            throw new RuntimeException("unsupported operation");
        }
    }

    /**
     * Filter参数变更处理；单独抽一种类型处理，因为Filter可能会比较多，这样一个filter变化时可以避免传输太多数据
     *
     * @param routeChangeCmd
     */
    private static void changeFilterConfig(RouteChangeDTO routeChangeCmd) {
        String routeId = routeChangeCmd.getRouteId();
        CheckUtils.checkNonEmpty(routeId, "must has routeId");

        //get routeConfig from repo by routeId
        RoutesConfig routeConfigList = ConfigRepo.getServerConfig().getRoutesConfig();
        CheckUtils.checkNotNull(routeConfigList, routeChangeCmd.getRouteId() + " not exist");
        RouteConfig routeConfig = routeConfigList.getRoute(routeId);
        CheckUtils.checkNotNull(routeConfig, routeChangeCmd.getRouteId() + " not exist");

        //convert json to FilterConfig
        FilterConfig changedFilterConfig = new Gson().fromJson(routeChangeCmd.getDataJson(), FilterConfig.class);
        CheckUtils.checkNonEmpty(changedFilterConfig.getName(), "filter must has name");


        removeRouteResilience(routeId, Arrays.asList(changedFilterConfig));
        /**
         * 新增Filter配置，加入到对应路由配置中，增加到路由实例的Filter实例对应顺序位置中；前提Filter实例已经存在FilterRepo中
         */
        if (OperationEnum.ADD.equals(routeChangeCmd.getOperation())) {
            CheckUtils.checkNonEmpty(changedFilterConfig.getName(), "filter name cannot be empty");

            if (routeConfig.getFilterConfig(changedFilterConfig.getName().trim()) != null) {
                throw new RuntimeException(changedFilterConfig.getName() + " already exist in " + routeConfig.getId());
            }
            routeConfig.getFilters().add(changedFilterConfig);
            Filter filter = FilterRepo.get(changedFilterConfig.getName().trim());
            RouteRepo.get(routeId).addFilter(filter);
        }
        /**
         * 更新Filter参数；更新到对应路由配置中，路由实例不需要变更
         */
        else if (OperationEnum.UPDATE.equals(routeChangeCmd.getOperation())) {
            CheckUtils.checkNonEmpty(changedFilterConfig.getName(), "filter name cannot be empty");
            FilterConfig toUpdate = routeConfig.getFilterConfig(changedFilterConfig.getName().trim());
            CheckUtils.checkNotNull(toUpdate, changedFilterConfig.getName() + " not exist in " + routeConfig.getId());
            toUpdate.getArgs().clear();
            changedFilterConfig.getArgs().keySet().forEach(param -> {
                toUpdate.getArgs().putIfAbsent(param, changedFilterConfig.getArgs().get(param));
            });

        } else if (OperationEnum.DELETE.equals(routeChangeCmd.getOperation())) {
            CheckUtils.checkNonEmpty(changedFilterConfig.getName(), "filter name cannot be empty");
            routeConfig.getFilters().removeIf(e -> e.getName().equalsIgnoreCase(changedFilterConfig.getName().trim()));
            Filter filter = FilterRepo.get(changedFilterConfig.getName());
            RouteRepo.get(routeId).getFilters().remove(filter);
        } else {
            throw new RuntimeException("unsupported operation");
        }
    }

    /**
     * 由于熔断器和重试器的参数无法实现动态替换，所以涉及到熔断器和重试参数变更的时候，移除路由对应的熔断器和重试器；
     * 在执行到熔断器和重试器的filter时，如果发现找不到该路由对应的，则新建
     *
     * @param routeId
     * @param changedFilterConfigs
     */
    private static void removeRouteResilience(String routeId, List<FilterConfig> changedFilterConfigs) {
        if (changedFilterConfigs == null) {
            return;
        }
        changedFilterConfigs.forEach(param -> {
            //如果涉及到熔断器、重试器参数的变更，需要移除Repo中的熔断器和重试器
            if (param.getName().equalsIgnoreCase(NameUtils.normalizeFilterName(CircuitBreakerFilter.class))) {
                BreakerRepo.removeIfExist(routeId);
            }
            if (param.getName().equalsIgnoreCase(NameUtils.normalizeFilterName(RetryFilter.class))) {
                RetryRepo.removeIfExist(routeId);
            }
        });
    }
}

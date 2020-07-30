package org.xujin.janus.startup;

import org.xujin.janus.JanusServerApplication;
import org.xujin.janus.core.dynamic.DynamicFileManger;
import org.xujin.janus.core.listener.ClassFileChangeListener;
import org.xujin.janus.core.listener.ConfigChangeListener;
import org.xujin.janus.core.listener.RouteChangeListener;
import org.xujin.janus.core.predicates.StaticPredicateLoader;
import org.xujin.janus.core.route.RouteRepo;
import org.xujin.janus.core.netty.utils.IpUtils;
import org.xujin.janus.filter.StaticFilterLoader;
import org.xujin.janus.monitor.JanusMetricsInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xujin.janus.config.AdminConfigLoader;
import org.xujin.janus.config.ApplicationConfig;
import org.xujin.janus.config.LocalConfigLoader;
import org.xujin.janus.registry.RegistryServiceRepo;

import java.util.HashMap;
import java.util.Map;

/**
 * run when server up
 *
 * @author: gan
 * @date: 2020/4/20
 */
public class JanusBootStrap {
    private static Logger logger = LoggerFactory.getLogger(JanusBootStrap.class);

    public static void initGateway() {

        try {
            //I.初始化本地静态配置
            ApplicationConfig.init();
            //I.静态Filter 加载
            StaticFilterLoader.load();
            //I.静态Predicate 加载
            StaticPredicateLoader.load();
            //I.监听配置文件的变化
            ConfigChangeListener.listen();
            //I.监听route配置变化
            RouteChangeListener.listen();
            //I.监听plugin文件变化
            ClassFileChangeListener.listen();
            //I.初始化注册中心,用于拉取动态配置
            initRegisterCenter();
            //II.加载配置文件(从本地配置文件或者远程配置中心)
            if (ApplicationConfig.getLocal()) {
                LocalConfigLoader.load();
            } else {
                AdminConfigLoader.load();
            }
            //III.动态加载Filter Class、Predicate Class
            DynamicFileManger.startPoller();
            //III.初始化JanusMetrics
            JanusMetricsInitializer.init();
            //IV.初始化路由
            RouteRepo.init();

        } catch (Exception ex) {
            logger.error("init janus error,message:"+ex.getMessage(),ex);
            DynamicFileManger.stopPoller();
            RegistryServiceRepo.destroy();
            JanusServerApplication.fail(ex.getMessage());
        }

    }

    /**
     * execute on server down
     */
    public static void destroy() {
        RegistryServiceRepo.destroy();
    }

    /**
     * 初始化注册中心
     */
    public static void initRegisterCenter() {
        String appName = ApplicationConfig.getApplicationName();
        int appPort = ApplicationConfig.getApplicationPort();

        String registryType = ApplicationConfig.getRegistryType();
        String registryUrl = ApplicationConfig.getRegistryUrl();
        String cluster = ApplicationConfig.getCluster();
        //注册cluster到注册中心，标识机器所属的集群
        Map<String, String> metadata = new HashMap<>(1);
        metadata.put("cluster", cluster);
        RegistryServiceRepo.init(registryType, registryUrl, appName,
                IpUtils.getIp(), appPort, metadata);
    }


}

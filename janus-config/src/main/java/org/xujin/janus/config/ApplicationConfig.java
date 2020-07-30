package org.xujin.janus.config;

import org.xujin.janus.config.util.ConfigFileUtils;
import org.xujin.janus.config.util.EnvUtils;

import java.util.Properties;

/**
 * 程序的基础配置，如程序当前所处的环境、所在集群、各环境注册中心的地址等，必须事先配置在本地静态文件中
 *
 * @author: gan
 * @date: 2020/4/21
 */
public class ApplicationConfig {
    private static final String APPLICATION_FILE = "application.properties";
    private static String applicationVersion;
    private static String env;
    private static Boolean local;
    private static String cluster;
    private static String registryUrl;
    private static String registryType;
    private static String applicationName;
    private static String adminName;
    private static String returnOkUrl;
    private static int applicationPort;

    public static void init() {
        Properties properties = ConfigFileUtils.readProperties(APPLICATION_FILE);
        env = EnvUtils.getEnv();
        cluster = EnvUtils.getCluster();
        local=Boolean.valueOf(EnvUtils.getLocal());
        applicationVersion = (String) properties.get("project.version");
        registryUrl = (String) properties.get("register.url." + env.toLowerCase());
        registryType = (String) properties.get("register.type");
        applicationName = (String) properties.get("application.name");
        adminName = (String) properties.get("admin.name");
        returnOkUrl=(String) properties.get("return.ok.url");
        applicationPort = Integer.parseInt(properties.get("application.port").toString());
    }


    public static String getApplicationVersion() {
        return applicationVersion;
    }

    public static String getEnv() {
        return env;
    }

    public static String getRegistryUrl() {
        return registryUrl;
    }

    public static String getRegistryType() {
        return registryType;
    }

    public static String getApplicationName() {
        return applicationName;
    }

    public static int getApplicationPort() {
        return applicationPort;
    }

    public static String getCluster() {
        return cluster;
    }

    public static String getAdminName() {
        return adminName;
    }

    public static void setAdminName(String adminName) {
        ApplicationConfig.adminName = adminName;
    }

    public static String getReturnOkUrl() {
        return returnOkUrl;
    }

    public static void setReturnOkUrl(String returnOkUrl) {
        ApplicationConfig.returnOkUrl = returnOkUrl;
    }

    public static Boolean getLocal() {
        return local;
    }

    public static void setLocal(Boolean local) {
        ApplicationConfig.local = local;
    }
}

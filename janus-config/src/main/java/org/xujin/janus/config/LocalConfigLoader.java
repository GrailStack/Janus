package org.xujin.janus.config;


import org.xujin.janus.config.app.RoutesConfig;
import org.xujin.janus.config.util.ConfigFileUtils;

/**
 * @author: gan
 * @date: 2020/4/21
 */
public class LocalConfigLoader {
    private static final String SERVER_CONFIG_FILE = "server.yaml";
    private static final String ROUTE_CONFIG_FILE = "routes.yaml";

    public static void load() {
        ServerConfig serverConfig = ConfigFileUtils.readYaml(SERVER_CONFIG_FILE, ServerConfig.class);
        RoutesConfig routeConfigList = ConfigFileUtils.readYaml(ROUTE_CONFIG_FILE, RoutesConfig.class);
        serverConfig.setRoutesConfig(routeConfigList);
        ConfigRepo.init(serverConfig);
    }


}

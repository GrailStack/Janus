package org.xujin.janus.registry;


import java.util.Map;

/**
 * @author: gan
 * @date: 2020/4/22
 */
public class RegistryServiceRepo {
    /**
     * 1.dynamic get registryService instance  by adminServer configure
     */
    private static RegistryService registryService;

    public static synchronized void init(String registryName, String registryServiceUrls, String appName, String clientIp,
                                         int clientPort, Map<String, String> metadata) {
        registryService = RegistryServiceFactory.create(registryName);
        try {
            registryService.initialize(registryServiceUrls, appName, clientIp, clientPort, metadata);
        } catch (Exception exception) {
            throw new RuntimeException("init registryService error," + exception.toString(), exception);
        }
    }

    public static synchronized void destroy() {
        if (registryService != null) {
            registryService.destroy();
        }
    }

    public static RegistryService getRegistryService() {
        return registryService;
    }
}

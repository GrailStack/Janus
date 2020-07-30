package org.xujin.janus.registry;

import org.xujin.janus.registry.eurake.EurekaRegistryServiceImpl;

/**
 * @author: xujin
 * @date: 2020/4/21
 */
public class RegistryServiceFactory {
    private static final String EUREKA_NAME = "eureka";

    public static RegistryService create(String name) {
        if (EUREKA_NAME.equalsIgnoreCase(name)) {
            return new EurekaRegistryServiceImpl();
        } else {
            throw new RuntimeException("unsupported serviceDiscovery " + name);
        }
    }
}

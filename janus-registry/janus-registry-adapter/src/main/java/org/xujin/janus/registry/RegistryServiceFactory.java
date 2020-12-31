package org.xujin.janus.registry;

import org.xujin.janus.registry.eurake.EurekaRegistryServiceImpl;
import org.xujin.janus.registry.nacos.NacosRegistryServiceImpl;

/**
 * @author: xujin
 * @date: 2020/4/21
 */
public class RegistryServiceFactory {
    private static final String EUREKA_NAME = "eureka";

    private static final String NACOS_NAME = "nacos";

    public static RegistryService create(String name) {
        if (EUREKA_NAME.equalsIgnoreCase(name)) {
            return new EurekaRegistryServiceImpl();
        } else if(NACOS_NAME.equals(name)){
            return new NacosRegistryServiceImpl();
        } else {
            throw new RuntimeException("unsupported serviceDiscovery " + name);
        }
    }
}

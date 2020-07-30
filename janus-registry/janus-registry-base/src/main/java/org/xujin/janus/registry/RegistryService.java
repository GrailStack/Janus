package org.xujin.janus.registry;

import java.net.URL;
import java.util.List;
import java.util.Map;

/**
 * @author: gan
 * @date: 2020/4/21
 */
public interface RegistryService {
    /**
     * Initializes the {@link RegistryService}
     *
     * @param registryUrl the {@link URL url} to connect service registry
     * @param clientIp
     * @param clientPort
     * @throws Exception If met with error
     */
    void initialize(String registryUrl, String appName, String clientIp, int clientPort, Map<String, String> metadata) throws Exception;

    /**
     * set service status down
     */
    void destroy();

    /**
     * get serverNode from the register center by serviceName
     *
     * @param serviceName
     * @return
     */
    List<ServerNode> getServerNode(String serviceName);
}

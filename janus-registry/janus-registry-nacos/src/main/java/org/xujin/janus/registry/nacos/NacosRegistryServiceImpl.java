package org.xujin.janus.registry.nacos;

import org.xujin.janus.registry.RegistryService;
import org.xujin.janus.registry.ServerNode;

import java.util.List;
import java.util.Map;

public class NacosRegistryServiceImpl implements RegistryService {

    @Override
    public void initialize(String registryUrl, String appName, String clientIp, int clientPort, Map<String, String> metadata) throws Exception {

    }

    @Override
    public void destroy() {

    }

    @Override
    public List<ServerNode> getServerNode(String serviceName) {
        return null;
    }
}

package org.xujin.janus.registry.nacos;

import com.alibaba.nacos.api.exception.NacosException;
import com.alibaba.nacos.api.naming.pojo.Instance;
import com.alibaba.nacos.client.naming.NacosNamingService;
import com.alibaba.nacos.client.naming.utils.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xujin.janus.registry.RegistryService;
import org.xujin.janus.registry.ServerNode;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.stream.Collectors;


public class NacosRegistryServiceImpl implements RegistryService {

    private static Logger logger = LoggerFactory.getLogger(NacosRegistryServiceImpl.class);

    private NacosNamingService nacosService;

    private static final String GROUP = "DEFAULT_GROUP";

    private Instance instance;


    @Override
    public void initialize(String registryUrl, String appName, String clientIp, int clientPort, Map<String, String> metadata) throws Exception {
        if (registryUrl == null || registryUrl.isEmpty()) {
            throw new IllegalArgumentException("registryUrls cannot be null");
        }
        Properties nacosConfigProperties = buildNacosConfigProperties(registryUrl);
        this.nacosService = new NacosNamingService(nacosConfigProperties);
        this.instance = getNacosInstance(clientIp, clientPort, metadata);
        this.nacosService.registerInstance(appName, GROUP, instance);
    }

    private Properties buildNacosConfigProperties(String registryUrl) {
        Properties properties = new Properties();
        properties.setProperty("serverAddr", registryUrl);
        return properties;
    }

    private Instance getNacosInstance(String clientIp, int clientPort, Map<String, String> metadata) {
        Instance instance = new Instance();
        instance.setIp(clientIp);
        instance.setPort(clientPort);
        //instance.setWeight(null);
        //instance.setClusterName(null);
        instance.setMetadata(metadata);
        return instance;
    }

    @Override
    public void destroy() {
        if (nacosService != null) {
            try {
                nacosService.deregisterInstance(this.instance.getServiceName(), instance);
            } catch (NacosException ex) {
                logger.error("destroy server fail", ex);
            }
        }
    }

    @Override
    public List<ServerNode> getServerNode(String serviceName) {
        try {
            List<Instance> instances = this.nacosService.getAllInstances(serviceName);
            if (!CollectionUtils.isEmpty(instances)) {
                return instances.stream().map(instance -> {
                    ServerNode serverNode = new ServerNode(instance.getServiceName(), instance.getIp());
                    serverNode.setPort(instance.getPort());
                    serverNode.setId(instance.getInstanceId());
                    return serverNode;
                }).collect(Collectors.toList());
            }
        } catch (NacosException ex) {
            logger.error("get serverNoe fail", ex);
            return Collections.emptyList();
        }
        return Collections.emptyList();
    }
}

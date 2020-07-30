package org.xujin.janus.registry.eurake;

import com.netflix.appinfo.ApplicationInfoManager;
import com.netflix.appinfo.EurekaInstanceConfig;
import com.netflix.appinfo.InstanceInfo;
import com.netflix.config.ConfigurationManager;
import com.netflix.discovery.DefaultEurekaClientConfig;
import com.netflix.discovery.DiscoveryClient;
import com.netflix.discovery.EurekaClient;
import com.netflix.discovery.EurekaClientConfig;
import com.netflix.discovery.shared.Application;
import org.xujin.janus.registry.RegistryService;
import org.xujin.janus.registry.ServerNode;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URL;
import java.net.URLDecoder;
import java.util.*;

/**
 * @author: gan
 * @date: 2020/4/21
 */
public class EurekaRegistryServiceImpl implements RegistryService {
    private EurekaClient eurekaClient;
    private ApplicationInfoManager applicationInfoManager;

    @Override
    public void initialize(String registryUrls, String appName, String clientIp, int clientPort, Map<String, String> metadata) throws Exception {
        if (registryUrls == null || registryUrls.isEmpty()) {
            throw new IllegalArgumentException("registryUrls cannot be null");
        }
        Properties eurekaConfigProperties = buildEurekaConfigProperties(registryUrls);
        initConfigurationManager(eurekaConfigProperties);
        initEurekaClient(appName, clientIp, clientPort, metadata);
    }

    @Override
    public void destroy() {
        if (eurekaClient != null) {
            this.eurekaClient.shutdown();
        }
    }

    private void initEurekaClient(String appName, String ipAddress, int port, Map<String, String> metadata) {
        if (eurekaClient != null) {
            return;
        }
        initApplicationInfoManager(appName, ipAddress, port, metadata);
        EurekaClient eurekaClient = createEurekaClient();
        this.applicationInfoManager.setInstanceStatus(InstanceInfo.InstanceStatus.UP);
        // set eurekaClient
        this.eurekaClient = eurekaClient;
    }

    private EurekaClient createEurekaClient() {
        EurekaClientConfig eurekaClientConfig = new DefaultEurekaClientConfig();
        DiscoveryClient eurekaClient = new DiscoveryClient(applicationInfoManager, eurekaClientConfig);
        return eurekaClient;
    }

    private void initApplicationInfoManager(String appName, String ipAddress, int port, Map<String, String> metadata) {
        EurekaInstanceConfig eurekaInstanceConfig = buildEurekaInstanceConfig(appName, ipAddress, port, metadata);
        this.applicationInfoManager = new ApplicationInfoManager(eurekaInstanceConfig, (ApplicationInfoManager.OptionalArgs) null);
    }

    private EurekaInstanceConfig buildEurekaInstanceConfig(String appName, String ipAddress, int port, Map<String, String> metadata) {
        ConfigurableEurekaInstanceConfig eurekaInstanceConfig = new ConfigurableEurekaInstanceConfig()
                .setInstanceId(appName)
                .setAppname(appName)
                .setIpAddress(ipAddress)
                .setNonSecurePort(port);
        if (metadata != null) {
            eurekaInstanceConfig.setMetadataMap(metadata);
        }
        return eurekaInstanceConfig;
    }

    /**
     * Initialize {@link ConfigurationManager}
     *
     * @param eurekaConfigProperties the Eureka's {@link ConfigurationManager}
     */
    private void initConfigurationManager(Properties eurekaConfigProperties) {
        ConfigurationManager.loadProperties(eurekaConfigProperties);
    }

    @Override
    public List<ServerNode> getServerNode(String serviceName) {
        Application application = this.eurekaClient.getApplication(serviceName);

        if (application == null) {
            return new ArrayList<>();
        }

        List<InstanceInfo> infos = application.getInstances();
        List<ServerNode> instances = new ArrayList<>();
        for (InstanceInfo info : infos) {
            instances.add(buildServiceNode(info));
        }
        return instances;
    }

    private ServerNode buildServiceNode(InstanceInfo instanceInfo) {
        ServerNode serverNode = new ServerNode(instanceInfo.getAppName(), instanceInfo.getHostName());
        serverNode.setPort(instanceInfo.getPort());
        serverNode.setId(instanceInfo.getId());
        return serverNode;
    }

    /**
     * Build the Properties whose {@link java.util.Map.Entry entries} are retrieved from URL
     *
     * @param registryUrls the {@link URL url} to connect Eureka
     * @return non-null
     */
    private Properties buildEurekaConfigProperties(String registryUrls) throws UnsupportedEncodingException {
        Properties properties = new Properties();
        Map<String, String> parameters = splitQuery(registryUrls);
        setDefaultProperties(registryUrls, properties);
        parameters.entrySet().stream()
                .filter(this::filterEurekaProperty)
                .forEach(propertyEntry -> {
                    properties.setProperty(propertyEntry.getKey(), propertyEntry.getValue());
                });
        return properties;
    }

    private boolean filterEurekaProperty(Map.Entry<String, String> propertyEntry) {
        String propertyName = propertyEntry.getKey();
        return propertyName.startsWith("eureka.");
    }

    private Map<String, String> splitQuery(String urls) throws UnsupportedEncodingException {
        URI uri;
        if (urls.contains(",")) {
            uri = URI.create(urls.split(",")[0]);
        } else {
            uri = URI.create(urls);
        }
        Map<String, String> queryPairs = new LinkedHashMap<String, String>();
        String query = uri.getQuery();
        if (query == null) {
            return queryPairs;
        }
        String[] pairs = query.split("&");
        for (String pair : pairs) {
            int idx = pair.indexOf("=");
            queryPairs.put(URLDecoder.decode(pair.substring(0, idx), "UTF-8"), URLDecoder.decode(pair.substring(idx + 1), "UTF-8"));
        }
        return queryPairs;
    }

    private void setDefaultProperties(String registryUrls, Properties properties) {
        setDefaultServiceUrl(registryUrls, properties);
        setDefaultInitialInstanceInfoReplicationIntervalSeconds(properties);
        setShouldFetchRegistry(properties);
    }

    private void setDefaultServiceUrl(String registryUrls, Properties properties) {
        properties.setProperty("eureka.serviceUrl.default", registryUrls);
    }

    private void setDefaultInitialInstanceInfoReplicationIntervalSeconds(Properties properties) {
        properties.setProperty("eureka.appinfo.initial.replicate.time", "0");
    }

    private void setShouldFetchRegistry(Properties properties) {
        properties.setProperty("eureka.client.shouldFetchRegistry", "true");
    }
}

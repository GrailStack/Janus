package org.xujin.janus.config.admin.client;

import org.xujin.janus.config.ApplicationConfig;
import org.xujin.janus.damon.JanusCmdClient;
import org.xujin.janus.damon.exchange.JanusCmdMsg;
import org.xujin.janus.registry.RegistryService;
import org.xujin.janus.registry.RegistryServiceRepo;
import org.xujin.janus.registry.ServerNode;
import org.xujin.janus.registry.loadbalancer.LoadBalancer;
import org.xujin.janus.registry.loadbalancer.LoadBalancerFactory;

import java.util.List;

/**
 * Janus Server Call Admin
 *
 * @author xujin
 */
public class AbstractRequest {
    private static final String CLUSTER = ApplicationConfig.getCluster();
    private static final String VERSION = ApplicationConfig.getApplicationVersion();


    /**
     * 同步接口
     *
     * @param method
     * @param payload
     * @return
     * @throws Exception
     */
    public Object syncSend(String method, Object payload) throws Exception {
        JanusCmdMsg janusCmdMsg = JanusCmdMsg.builder()
                .cluster(CLUSTER)
                .version(VERSION)
                .method(method)
                .payload(payload)
                .needCallBack(false)
                .buildRequest();
        JanusCmdClient janusCmdClient = JanusCmdClient.getSingleton();
        JanusCmdMsg response = janusCmdClient.syncSend("127.0.0.1:8085", janusCmdMsg);
        if (response == null) {
            return null;
        }
        if (response.getCode() != 0) {
            throw new RuntimeException(response.getMessage());
        }
        return response.getPayload();
    }

    /**
     * 异步接口
     * requestID 为null不发送异步回调；否则，发送异步回调
     * @param method
     * @param requestID
     * @param payload
     * @throws Exception
     */
    public void asyncSend(String method,String requestID, Object payload) throws Exception {
        boolean needCallBack = true;
        if (requestID == null) {
            needCallBack = false;
        }
        JanusCmdMsg janusCmdMsg = JanusCmdMsg.builder()
                .cluster(CLUSTER)
                .version(VERSION)
                .method(method)
                .payload(payload)
                .requestID(requestID)
                .needCallBack(needCallBack)
                .buildRequest();
        JanusCmdClient janusCmdClient = JanusCmdClient.getSingleton();
        janusCmdClient.asyncSend(getAdminRemoteAddress(), janusCmdMsg);

    }

    private static String getAdminRemoteAddress() {
        String adminName = ApplicationConfig.getAdminName();
        RegistryService serviceDiscovery = RegistryServiceRepo.getRegistryService();
        List<ServerNode> serverNodes = serviceDiscovery.getServerNode(adminName);

        LoadBalancer loadBalancer = LoadBalancerFactory.create("Round-Robin");
        ServerNode serverNode = loadBalancer.select(serverNodes, null);
        if (serverNode == null) {
            throw new RuntimeException(adminName + " instance not found");
        }
        return serverNode.getHost() + ":" + serverNode.getPort();
    }
}

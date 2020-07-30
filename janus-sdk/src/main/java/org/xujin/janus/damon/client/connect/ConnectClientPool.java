package org.xujin.janus.damon.client.connect;


import org.xujin.janus.damon.client.NettyConnectClient;
import org.xujin.janus.damon.exception.JanusCmdException;
import org.xujin.janus.damon.exchange.NettyMsg;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * @author tbkk 2019-10-19
 */
@Slf4j
public class ConnectClientPool {

    private static class Holder {
        private static final ConnectClientPool connectClientPool = new ConnectClientPool();
    }

    public static ConnectClientPool getSingleton() {
        return Holder.connectClientPool;
    }

    /**
     * async send
     */
    public void asyncSend(NettyMsg nettyMsg, String address) throws Exception {

        try {
            ConnectClient connectClient = this.getConnectClient(address);
            connectClient.send(nettyMsg);
        } catch (Exception e) {
            throw e;
        }

    }

    private volatile ConcurrentMap<String, ConnectClient> connectClientMap = new ConcurrentHashMap<>();
    private volatile ConcurrentMap<String, Object> connectClientLockMap = new ConcurrentHashMap<>();

    public void clear() {
        if (connectClientMap.size() > 0) {
            for (String key : connectClientMap.keySet()) {
                ConnectClient clientConnecter = connectClientMap.get(key);
                clientConnecter.close();
            }
            connectClientMap.clear();
        }
    }

    private ConnectClient getConnectClient(String address) throws Exception {

        ConnectClient connectClient = connectClientMap.get(address);
        if (connectClient != null && connectClient.isValidate()) {
            return connectClient;
        }

        // lock
        Object clientLock = connectClientLockMap.get(address);
        if (clientLock == null) {
            connectClientLockMap.putIfAbsent(address, new Object());
            clientLock = connectClientLockMap.get(address);
        }

        // remove-create new client
        synchronized (clientLock) {

            // get-valid client, avlid repeat
            connectClient = connectClientMap.get(address);
            if (connectClient != null && connectClient.isValidate()) {
                return connectClient;
            }

            // remove old
            if (connectClient != null) {
                connectClient.close();
                connectClientMap.remove(address);
            }

            ConnectClient newConnectClient = new NettyConnectClient();
            try {
                newConnectClient.init(address);
                connectClientMap.put(address, newConnectClient);
            } catch (Exception e) {
                newConnectClient.close();
                throw new JanusCmdException("无法连接到目标Ip地址: " + address);
            }

            return newConnectClient;
        }

    }

}

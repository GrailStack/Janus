package org.xujin.janus.damon.client.connect;

import org.xujin.janus.damon.exchange.NettyMsg;

/**
 * 连接客户端抽象类
 */
public abstract class ConnectClient {

    public abstract void init(String address) throws Exception;

    public abstract void close();

    public abstract boolean isValidate();


    public abstract void send(NettyMsg nettyMsg) throws Exception ;

    private ConnectClientPool connectClientPool = ConnectClientPool.getSingleton();

}

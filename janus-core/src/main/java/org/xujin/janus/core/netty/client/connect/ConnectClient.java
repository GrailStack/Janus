package org.xujin.janus.core.netty.client.connect;


import org.xujin.janus.core.netty.ctx.DefaultJanusCtx;
import io.netty.handler.codec.http.HttpRequest;
import lombok.extern.slf4j.Slf4j;

/**
 * @author tbkk
 */
@Slf4j
public abstract class ConnectClient {

    // ---------------------- iface ----------------------

    public abstract void init(String address) throws Exception;

    public abstract void close();

    public abstract boolean isValidate();

    /**
     *
     * @param httpRequest 发送http请求
     * @return void
     */
    public abstract void send(HttpRequest httpRequest, DefaultJanusCtx ctx) throws Exception ;

    private ConnectClientPool connectClientPool = ConnectClientPool.getSingleton();

}

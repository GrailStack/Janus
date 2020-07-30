package org.xujin.janus.core.netty.ctx;

import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponse;

/**
 * 网关Server对内外的发送的统一接口
 * @author xujin
 */
public interface IJanusCtx {
    /**
     * 网关Server往内网源服务发送
     * @param httpRequest
     * @throws Exception
     */
    public  void janusToInnerServerAsyncSend(HttpRequest httpRequest) throws Exception;

    /**
     * 返回给客户端的应答
     * @param responseToClient
     */
    public  void janusToOutsideClientAsyncSend(HttpResponse responseToClient);
}

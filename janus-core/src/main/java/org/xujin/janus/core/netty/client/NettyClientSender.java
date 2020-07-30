package org.xujin.janus.core.netty.client;



import org.xujin.janus.core.netty.client.connect.ConnectClientPool;
import org.xujin.janus.core.netty.ctx.DefaultJanusCtx;
import io.netty.handler.codec.http.HttpRequest;
import lombok.extern.slf4j.Slf4j;

/**
 * netty client
 *
 * @author tbkk
 */
@Slf4j
public class NettyClientSender implements Client {

	@Override
	public void asyncSend(String address, HttpRequest httpRequest, DefaultJanusCtx ctx) throws Exception {
		ConnectClientPool.getSingleton().asyncSend(httpRequest, address, ctx);

	}
}

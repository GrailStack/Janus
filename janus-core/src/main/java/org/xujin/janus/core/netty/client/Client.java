package org.xujin.janus.core.netty.client;

import org.xujin.janus.core.netty.ctx.DefaultJanusCtx;
import io.netty.handler.codec.http.HttpRequest;


/**
 * @author tbkk
 */
public interface Client {

	/**
	 * async send, bind requestId and future-response
	 *
	 * @param address
	 * @param httpRequest
	 * @return
	 * @throws Exception
	 */
	public void asyncSend(String address, HttpRequest httpRequest, DefaultJanusCtx ctx) throws Exception;

}

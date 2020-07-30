package org.xujin.janus.core.netty.client.callback;

import org.xujin.janus.core.netty.ctx.DefaultJanusCtx;

/**
 * @author tbkk
 */
public interface JanusExceptionCallback {

    public void runCallBack(DefaultJanusCtx ctx, Throwable cause);
}

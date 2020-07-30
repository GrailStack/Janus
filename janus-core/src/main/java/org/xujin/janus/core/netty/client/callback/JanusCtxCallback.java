package org.xujin.janus.core.netty.client.callback;

import org.xujin.janus.core.netty.ctx.DefaultJanusCtx;

/**
 * @author tbkk
 */
public interface JanusCtxCallback {

    public void runCallBack(DefaultJanusCtx ctx);
}

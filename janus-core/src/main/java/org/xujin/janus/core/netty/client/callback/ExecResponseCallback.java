package org.xujin.janus.core.netty.client.callback;

import io.netty.handler.codec.http.HttpResponse;

/**
 * @author tbkk
 */
public interface ExecResponseCallback {

    public void exec(HttpResponse httpResponse) throws Exception;

}

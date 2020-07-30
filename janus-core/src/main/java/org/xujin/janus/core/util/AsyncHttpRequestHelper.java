package org.xujin.janus.core.util;

import org.xujin.janus.core.netty.ctx.DefaultJanusCtx;
import io.netty.handler.codec.http.HttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.function.Consumer;

/**
 * @author: gan
 * @date: 2020/5/18
 */
public class AsyncHttpRequestHelper {
    private static final Logger logger = LoggerFactory.getLogger(AsyncHttpRequestHelper.class);

    /**
     * send http request async
     *
     * @param defaultJanusCtx
     */
    public static void asyncSendHttpRequest(DefaultJanusCtx defaultJanusCtx, Consumer<HttpResponse> completeSupplier,
                                            Consumer<Throwable> errorSupplier) {
        try {

            defaultJanusCtx.setExecResponseCallBack(ctx -> {
                completeSupplier.accept(ctx.getOriHttpResponse());
            });
            defaultJanusCtx.setInnerServerChannelInactiveCallback(ctx -> {
                //与Server的链接断开，正常断开还是异常断开？？
                logger.info("client close");
            });
            defaultJanusCtx.setInnerServerExceptionCaughtCallback((ctx, throwable) -> {
                errorSupplier.accept(throwable);
            });
            defaultJanusCtx.janusToInnerServerAsyncSend(defaultJanusCtx.getOriFullHttpRequest());

        } catch (Exception exception) {
            logger.error("execute asyncSendHttpRequest error", exception);
            errorSupplier.accept(exception);
        }
    }
}


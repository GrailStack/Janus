package org.xujin.janus.core.resilience.retry;

import org.xujin.janus.core.constant.SessionContextKey;
import org.xujin.janus.core.context.FilterContext;
import io.github.resilience4j.retry.Retry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.function.Function;

/**
 * @author: gan
 * @date: 2020/5/18
 */
public class AsyncRetryResult {
    private static final Logger logger = LoggerFactory.getLogger(AsyncRetryResult.class);

    /**
     * process retry complete
     *
     * @param context
     */
    public static void onComplete(FilterContext context, Function retryFunction) {
        Object retryObject = context.getSessionContext().get(SessionContextKey.RETRY_CONTEXT);
        if (retryObject != null) {

            Retry.Context retryContext = (Retry.Context) retryObject;
            final boolean validationOfResult = retryContext.onResult(context);
            if (!validationOfResult) {
                retryContext.onComplete();
                return;
            }
            retryFunction.apply(null);
            logger.error("exec retry............>>>>>>>>>>>>>>>>");
        }
    }

    /**
     * process retry error
     *
     * @param context
     * @param throwable
     */
    public static void onError(FilterContext context, Throwable throwable, Function retryFunction) {
        Object retryObject = context.getSessionContext().get(SessionContextKey.RETRY_CONTEXT);
        if (retryObject != null) {
            Retry.Context retryContext = (Retry.Context) retryObject;

            if (throwable instanceof Exception) {
                Retry retry = (Retry) context.getSessionContext().get(SessionContextKey.RETRY);
                try {
                    retryContext.onError((Exception) throwable);
                } catch (Exception e) {
                    return;
                }
            } else {
                return;
            }
            retryFunction.apply(context);
            logger.error("exec retry error............>>>>>>>>>>>>>>>>");
        }
    }
}

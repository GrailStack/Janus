package org.xujin.janus.core.filter.filters;

import org.xujin.janus.core.constant.SessionContextKey;
import org.xujin.janus.core.context.FilterContext;
import org.xujin.janus.core.filter.FilterChain;
import org.xujin.janus.core.filter.FilterType;
import org.xujin.janus.core.resilience.breaker.AsyncCircuitBreakerResult;
import org.xujin.janus.core.resilience.retry.AsyncRetryResult;
import org.xujin.janus.core.util.AsyncHttpRequestHelper;
import io.netty.handler.codec.http.HttpResponseStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author: gan
 * @date: 2020/4/27
 */
public abstract class AbstractHttpInvokeFilter extends AbstractAsyncFilter {
    private static final Logger logger = LoggerFactory.getLogger(AbstractHttpInvokeFilter.class);

    @Override
    public FilterType type() {
        return FilterType.INVOKE;
    }

    /**
     * @param context
     */
    public void invoke(FilterContext context) {
        //use netty client send async http request

        if (context.getSessionContext().get(SessionContextKey.MOCK_RESPONSE) != null) {
            complete(context);
            return;
        }

        AsyncHttpRequestHelper.asyncSendHttpRequest(context.getCtx(), httpResponse -> {
            context.getCtx().setOriHttpResponse(httpResponse);
            AsyncCircuitBreakerResult.onComplete(context);
            AsyncRetryResult.onComplete(context, t -> {
                FilterChain.previous(context);
                return null;
            });
            complete(context);
        }, exception -> {
            context.setTextResponse(exception.getMessage(), HttpResponseStatus.GATEWAY_TIMEOUT);
            AsyncCircuitBreakerResult.onError(context, exception);
            AsyncRetryResult.onError(context, exception, t -> {
                FilterChain.previous(context);
                return null;
            });
            error(context, exception);
        });
    }

    public void skipInvoke(FilterContext filterContext) {
        complete(filterContext);
    }

}

package org.xujin.janus.core.filter.filters;

import org.xujin.janus.core.constant.SessionContextKey;
import org.xujin.janus.core.context.FilterContext;
import org.xujin.janus.core.filter.FilterType;
import org.xujin.janus.monitor.JanusMetrics;
import org.xujin.janus.monitor.constant.MetricsConst;
import io.micrometer.core.instrument.Tags;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpResponseStatus;

import java.net.URI;
import java.util.concurrent.TimeUnit;

/**
 * the last filter
 *
 * @author: gan
 * @date: 2020/4/27
 */
public class WriteToClientFilter extends AbstractSyncFilter {

    @Override
    public int order() {
        return Integer.MAX_VALUE;
    }

    @Override
    public FilterType type() {
        return FilterType.AFTER_OUT;
    }

    @Override
    public void doFilter(FilterContext context) {
        URI uri = URI.create(context.getCtx().getOriFullHttpRequest().uri());
        FullHttpResponse fullHttpResponse = (FullHttpResponse) context.getCtx().getOriHttpResponse();
        if (fullHttpResponse == null) {
            context.setTextResponse("response is null", HttpResponseStatus.OK);
            fullHttpResponse = (FullHttpResponse) context.getCtx().getOriHttpResponse();
        }
        //执行janusToOutsideClientAsyncSend之后会将FullHttpRequest重置为Null
        context.getCtx().janusToOutsideClientAsyncSend(fullHttpResponse);

        long start = (long) context.getSessionContext().get(SessionContextKey.FILTER_START_TIME);
        long durationInMillis = System.currentTimeMillis() - start;

        Tags tags = Tags.of(MetricsConst.TAG_KEY_URL, uri.getPath());
        tags = tags.and(Tags.of(MetricsConst.TAG_KEY_REMOTE_IP, uri.getHost()));
        JanusMetrics.timer(MetricsConst.METRIC_FINISH_REQUEST, tags, durationInMillis, TimeUnit.MILLISECONDS);
    }
}

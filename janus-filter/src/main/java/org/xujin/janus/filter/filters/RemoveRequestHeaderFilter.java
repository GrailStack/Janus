package org.xujin.janus.filter.filters;

import org.xujin.janus.core.context.FilterContext;
import org.xujin.janus.core.filter.FilterType;
import io.netty.handler.codec.http.HttpHeaders;

import java.util.Map;

/**
 * @author: gan
 * @date: 2020/4/24
 * filters:
 * - RemoveRequestHeader=X-Request-Red,X-Request-Blue
 * or
 * <p>
 * filters:
 * - name: RemoveRequestHeader
 * args:
 * headers:X-Request-Red,X-Request-Blue
 */
public class RemoveRequestHeaderFilter extends AbstractHeaderFilter {

    @Override
    public int order() {
        return 10;
    }

    @Override
    public FilterType type() {
        return FilterType.INBOUND;
    }

    @Override
    public void doFilter(FilterContext context) {
        Map<String, String> filterConfig = getConfig(context);
        HttpHeaders requestHeaders = context.getCtx().getOriFullHttpRequest().headers();
        removeHeaders(filterConfig, requestHeaders);
    }
}

package org.xujin.janus.filter.filters;

import org.xujin.janus.core.context.FilterContext;
import org.xujin.janus.core.filter.FilterType;
import io.netty.handler.codec.http.HttpHeaders;

import java.util.Map;

/**
 * @author: gan
 * @date: 2020/4/24
 * filters:
 * - RemoveResponseHeader=X-Request-Red,X-Request-Blue
 * or
 * <p>
 * filters:
 * - name: RemoveResponseHeader
 * args:
 * headers:X-Request-Red,X-Request-Blue
 */
public class RemoveResponseHeaderFilter extends AbstractHeaderFilter {

    @Override
    public int order() {
        return 10;
    }

    @Override
    public FilterType type() {
        return FilterType.OUTBOUND;
    }

    @Override
    public void doFilter(FilterContext context) {
        Map<String, String> filterConfig = getConfig(context);
        HttpHeaders requestHeaders = context.getCtx().getOriHttpResponse().headers();
        removeHeaders(filterConfig, requestHeaders);
    }
}

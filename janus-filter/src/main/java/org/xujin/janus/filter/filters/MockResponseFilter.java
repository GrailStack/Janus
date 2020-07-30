package org.xujin.janus.filter.filters;

import org.xujin.janus.core.constant.SessionContextKey;
import org.xujin.janus.core.context.FilterContext;
import org.xujin.janus.core.filter.FilterType;
import org.xujin.janus.core.filter.filters.AbstractSyncFilter;
import io.netty.handler.codec.http.*;

import java.net.URI;
import java.util.Map;

/**
 * filters:
 * - name: PathMapping
 * args:
 * "/a":"{key1:value1}"
 * "/b":"{}"
 *
 * @author: gan
 * @date: 2020/6/4
 */
public class MockResponseFilter extends AbstractSyncFilter {
    @Override
    public void doFilter(FilterContext context) {
        FullHttpRequest fullHttpRequest = context.getCtx().getOriFullHttpRequest();
        String requestUriStr = fullHttpRequest.uri();
        URI requestUri = URI.create(requestUriStr);
        Map<String, String> filterConfig = getConfig(context);
        String path = requestUri.getPath();
        String response = mockHttpResponse(path, filterConfig);
        if (response == null) {
            return;
        }
        context.setJsonResponse(response, HttpResponseStatus.OK);
        context.getSessionContext().put(SessionContextKey.MOCK_RESPONSE, true);
    }

    private String mockHttpResponse(String path, Map<String, String> filterConfig) {
        String responseContent = "mock empty content";
        if (filterConfig == null || filterConfig.isEmpty()) {
            return responseContent;
        }
        responseContent = filterConfig.get(path);
        return responseContent;
    }

    @Override
    public int order() {
        return 1000;
    }

    @Override
    public FilterType type() {
        return FilterType.INBOUND;
    }
}

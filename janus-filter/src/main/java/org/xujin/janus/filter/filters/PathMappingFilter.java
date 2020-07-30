package org.xujin.janus.filter.filters;

import org.xujin.janus.core.context.FilterContext;
import org.xujin.janus.core.filter.FilterType;
import org.xujin.janus.core.filter.filters.AbstractSyncFilter;
import org.xujin.janus.core.util.UriBuilder;
import io.netty.handler.codec.http.FullHttpRequest;

import java.net.URI;
import java.util.Map;

/**
 * filters:
 * - name: PathMapping
 * args:
 *  "/a":"/a1"
 *  "/b":"/b1"
 *
 * @author: gan
 * @date: 2020/6/3
 */
public class PathMappingFilter extends AbstractSyncFilter {
    @Override
    public void doFilter(FilterContext context) {
        FullHttpRequest fullHttpRequest = context.getCtx().getOriFullHttpRequest();
        Map<String, String> filterConfig = getConfig(context);
        String requestUriStr = fullHttpRequest.uri();
        URI requestUri = URI.create(requestUriStr);
        String path = requestUri.getPath();
        String newPath = getMappedPath(path, filterConfig);
        if (newPath == null) {
            return;
        }
        URI newRequestUri = UriBuilder.from(requestUri).replacePath(newPath).build();
        fullHttpRequest.setUri(newRequestUri.toString());
    }

    private String getMappedPath(String path, Map<String, String> filterConfig) {
        if (filterConfig == null || filterConfig.isEmpty()) {
            return null;
        }
        if (!filterConfig.containsKey(path)) {
            return null;
        }
        String newPath = filterConfig.get(path);
        if (newPath == null || newPath.isEmpty()) {
            return null;
        }
        return newPath;

    }

    @Override
    public int order() {
        return 40;
    }

    @Override
    public FilterType type() {
        return FilterType.INBOUND;
    }
}

package org.xujin.janus.core.filter.filters;

import org.xujin.janus.core.constant.Protocol;
import org.xujin.janus.core.context.FilterContext;
import org.xujin.janus.core.route.Route;
import org.xujin.janus.core.util.UriBuilder;
import io.netty.handler.codec.http.HttpHeaderNames;

import java.net.URI;
import java.util.List;

/**
 * EndPoint HttpFilter
 * dispatch http request to other service
 *
 * @author: gan
 * @date: 2020/4/21
 */
public class HttpFilter extends AbstractHttpInvokeFilter {

    @Override
    public void doFilter(FilterContext context) {
        Route route = getRoute(context);
        String backendUriScheme = route.getProtocol();
        if (!Protocol.HTTP.getValue().equalsIgnoreCase(backendUriScheme)
                && !Protocol.HTTPS.toString().equalsIgnoreCase(backendUriScheme)) {
            skipInvoke(context);
            return;
        }
        List<String> backendHosts=route.getHosts();
        if(null==backendHosts||backendHosts.size()==0){
            return;
        }
        String host= backendHosts.get(0);
        String[] hotsStr= host.split(":");
        String requestUriStr = context.getCtx().getOriFullHttpRequest().uri();
        URI requestUri = URI.create(requestUriStr);
        URI newRequestUri = UriBuilder.from(requestUri)
                .replaceScheme(backendUriScheme)
                .replaceHost(String.valueOf(hotsStr[0]))
                .replacePort(Integer.valueOf(hotsStr[1]).intValue())
                .build();
        context.getCtx().getOriFullHttpRequest().setUri(newRequestUri.toString());
        invoke(context);
    }

    @Override
    public int order() {
        return 40;
    }
}

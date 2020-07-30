package org.xujin.janus.filter.filters;

import org.xujin.janus.core.context.FilterContext;
import org.xujin.janus.core.filter.filters.AbstractSyncFilter;
import org.xujin.janus.core.filter.FilterType;
import org.xujin.janus.core.util.UriBuilder;
import io.netty.handler.codec.http.FullHttpRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.util.Map;
import java.util.Optional;

/**
 * @author: gan
 * @date: 2020/4/24
 * filters:
 * - PrefixPath=/append
 * or
 * <p>
 * filters:
 * - name: PrefixPath
 * args:
 * prefix: /append
 */
public class PrefixPathFilter extends AbstractSyncFilter {
    private static final Logger logger = LoggerFactory.getLogger(PrefixPathFilter.class);
    private static final String PREFIX_ARG_KEY = "prefix";
    private static final String PATH_DELIMITER = "/";


    @Override
    public void doFilter(FilterContext context) {
        FullHttpRequest fullHttpRequest = context.getCtx().getOriFullHttpRequest();
        PrefixConfig prefixConfig = new PrefixConfig(getConfig(context));
        if (prefixConfig.getPrefix() == null) {
            return;
        }
        String requestUriStr = fullHttpRequest.uri();
        URI requestUri = URI.create(requestUriStr);
        String newPath = getNewPath(requestUri, prefixConfig.getPrefix());
        URI newRequestUri = UriBuilder.from(requestUri).replacePath(newPath).build();
        fullHttpRequest.setUri(newRequestUri.toString());
    }

    @Override
    public int order() {
        return 30;
    }

    @Override
    public FilterType type() {
        return FilterType.INBOUND;
    }

    private String getNewPath(URI requestUri, String prefix) {
        String path = requestUri.getRawPath();
        String newPath = prefix + path;
        if (!newPath.startsWith(PATH_DELIMITER)) {
            newPath = PATH_DELIMITER + newPath;
        }
        return newPath;
    }


    private class PrefixConfig {
        private String prefix;

        public PrefixConfig(Map<String, String> config) {
            if(config==null){
                return;
            }
            //config without parameter name
            String autoGenKey = getAutoGenerateKey(config);
            if (autoGenKey != null) {
                Optional.ofNullable(config.get(autoGenKey)).ifPresent(this::setPrefix);
            } else {
                //config with parameter name
                Optional.ofNullable(config.get("prefix")).ifPresent(this::setPrefix);
            }
        }

        public String getPrefix() {
            return prefix;
        }

        public void setPrefix(String prefix) {
            this.prefix = prefix;
        }
    }

}

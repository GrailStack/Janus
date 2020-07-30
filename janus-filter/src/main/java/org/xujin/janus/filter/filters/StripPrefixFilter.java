package org.xujin.janus.filter.filters;

import org.xujin.janus.core.context.FilterContext;
import org.xujin.janus.core.filter.filters.AbstractSyncFilter;
import org.xujin.janus.core.filter.FilterType;
import org.xujin.janus.config.util.StringUtils;
import org.xujin.janus.core.util.UriBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.util.Arrays;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @author: gan
 * @date: 2020/4/18
 * filters:
 * - StripPrefix=1
 * or
 * <p>
 * filters:
 * - name: StripPrefix
 * args:
 * parts: 1
 */
public class StripPrefixFilter extends AbstractSyncFilter {
    private static final Logger logger = LoggerFactory.getLogger(StripPrefixFilter.class);
    private static final String PATH_DELIMITER = "/";


    @Override
    public void doFilter(FilterContext context) {
        StripPrefixConfig stripPrefixConfig = new StripPrefixConfig(getConfig(context));
        int parts = stripPrefixConfig.getParts();
        if (parts <= 0) {
            return;
        }
        String requestUriStr = context.getCtx().getOriFullHttpRequest().uri();
        URI requestUri = URI.create(requestUriStr);
        URI newRequestUri = UriBuilder.from(requestUri).replacePath(getNewPath(requestUri, parts)).build();
        context.getCtx().getOriFullHttpRequest().setUri(newRequestUri.toString());
    }

    private String getNewPath(URI requestUri, int parts) {
        String path = requestUri.getRawPath();
        //split path with "/",then skip parts,finally join with "/"
        String newPath = PATH_DELIMITER + Arrays.stream(StringUtils.tokenizeToStringArray(path, PATH_DELIMITER))
                .skip(parts).collect(Collectors.joining(PATH_DELIMITER));
        // if path end with "/",add "/" to newPath end
        newPath += (newPath.length() > 1 && path.endsWith(PATH_DELIMITER) ? PATH_DELIMITER : "");
        return newPath;
    }

    @Override
    public int order() {
        return 20;
    }

    @Override
    public FilterType type() {
        return FilterType.INBOUND;
    }

    private class StripPrefixConfig {
        private int parts = -1;

        public StripPrefixConfig(Map<String, String> config) {
            if (config == null) {
                return;
            }
            //config without parameter name
            String autoGenKey = getAutoGenerateKey(config);
            if (autoGenKey != null) {
                Optional.ofNullable(config.get(autoGenKey)).ifPresent(this::setParts);
            } else {
                //config with parameter name
                Optional.ofNullable(config.get("parts")).ifPresent(this::setParts);
            }
        }

        public int getParts() {
            return parts;
        }

        private void setParts(String partsConfig) {
            this.setParts(Integer.parseInt(partsConfig));
        }

        public void setParts(int parts) {
            this.parts = parts;
        }
    }
}

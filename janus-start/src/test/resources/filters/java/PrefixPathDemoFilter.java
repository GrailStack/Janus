package org.xujin.janus.core.app.filter.filters;

import org.xujin.janus.core.context.FilterContext;
import org.xujin.janus.core.filter.FilterType;
import org.xujin.janus.core.filter.filters.AbstractSyncFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.util.Map;

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
 * prefix: 1
 */
public class PrefixPathDemoFilter extends AbstractSyncFilter {
    private static final Logger logger = LoggerFactory.getLogger(PrefixPathDemoFilter.class);
    private static final String PREFIX_ARG_KEY = "prefix";
    private String prefix;

    public PrefixPathDemoFilter(Map filterArgs) {
        super(filterArgs);
    }

    @Override
    public void doFilter(FilterContext context) {
        System.out.println("dynamic  java filter running ......");
    }

    @Override
    public int filterOrder() {
        return 0;
    }

    @Override
    public FilterType filterType() {
        return FilterType.INBOUND;
    }

    private String getNewPath(URI requestUri) {
        String path = requestUri.getRawPath();
        String newPath = prefix + path;
        return newPath;
    }

}

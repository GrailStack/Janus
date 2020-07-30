package org.xujin.janus.core.predicates;

import org.xujin.janus.config.util.StringUtils;
import org.xujin.janus.core.context.FilterContext;
import io.netty.handler.codec.http.FullHttpRequest;

import java.net.URI;
import java.util.Map;

/**
 * @author: gan
 * @date: 2020/4/17
 * predicates: # and ; if need or add a new route
 * - name: PathPrecise
 * args:
 * path: /delay/1,/delay/2
 * or
 * <p>
 * predicates: # and ; if need or add a new route
 * - PathPrecise = /api/service
 */
public class PathPrecisePredicate extends AbstractPredicate {
    private static final String PATH_ARG_KEY = "paths";
    private String[] paths;

    @Override
    public boolean test(FilterContext context) {
        FullHttpRequest fullHttpRequest = context.getCtx().getOriFullHttpRequest();
        if (fullHttpRequest == null || fullHttpRequest.uri() == null) {
            return Boolean.FALSE;
        }
        Map<String, String> predicateConfig = getPredicateConfig(context);
        setPaths(predicateConfig);
        String uriStr = fullHttpRequest.uri();
        URI uri = URI.create(uriStr);
        String path = uri.getPath();
        for (int i = 0; i < paths.length; i++) {
            if (paths[i].equalsIgnoreCase(path)) {
                return Boolean.TRUE;
            }
        }
        return Boolean.FALSE;
    }

    private void setPaths(Map<String, String> predicateConfig) {
        String path;
        if (predicateConfig.containsKey(PATH_ARG_KEY)) {
            path = predicateConfig.get(PATH_ARG_KEY);
        } else {
            path = predicateConfig.get(predicateConfig.keySet().stream().findFirst().orElse(""));
        }
        paths = StringUtils.tokenizeToStringArray(path, ",");
    }
    
}

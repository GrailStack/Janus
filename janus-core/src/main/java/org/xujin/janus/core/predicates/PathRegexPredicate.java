package org.xujin.janus.core.predicates;

import org.xujin.janus.core.context.FilterContext;
import org.xujin.janus.config.util.StringUtils;
import io.netty.handler.codec.http.FullHttpRequest;

import java.net.URI;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * @author: gan
 * @date: 2020/4/17
 * predicates: # and ; if need or add a new route
 * - name: PathRegex
 * args:
 * pattern: /delay/{timeout},/delay/{timeout1}
 * or
 * <p>
 * predicates: # and ; if need or add a new route
 * - PathRegex = /api/service,/api/admin
 */
public class PathRegexPredicate extends AbstractPredicate {
    private static final String PATTERN_ARG_KEY = "patterns";
    private List<Pattern> pathPatterns;

    @Override
    public boolean test(FilterContext context) {
        FullHttpRequest fullHttpRequest=context.getCtx().getOriFullHttpRequest();
        if (fullHttpRequest == null || fullHttpRequest.uri() == null) {
            return Boolean.FALSE;
        }
        Map<String,String> predicateConfig=getPredicateConfig(context);
        setPathPatterns(predicateConfig);
        String uriStr = fullHttpRequest.uri();
        URI uri = URI.create(uriStr);
        String path = uri.getPath();
        for (int i = 0; i < pathPatterns.size(); i++) {
            if (pathPatterns.get(i).matcher(path).matches()) {
                return Boolean.TRUE;
            }
        }
        return Boolean.FALSE;
    }

    private void setPathPatterns( Map<String,String> predicateConfig) {
        String path;
        if (predicateConfig.containsKey(PATTERN_ARG_KEY)) {
            path = predicateConfig.get(PATTERN_ARG_KEY);
        } else {
            path = predicateConfig.get(predicateConfig.keySet().stream().findFirst().orElse(""));
        }
        String[] regexPaths = StringUtils.tokenizeToStringArray(path, ",");
        pathPatterns = Arrays.stream(regexPaths).map(e -> Pattern.compile(e)).collect(Collectors.toList());
    }


}

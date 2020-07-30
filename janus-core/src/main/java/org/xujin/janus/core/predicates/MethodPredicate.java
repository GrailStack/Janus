package org.xujin.janus.core.predicates;

import org.xujin.janus.config.util.StringUtils;
import org.xujin.janus.core.context.FilterContext;
import io.netty.handler.codec.http.FullHttpRequest;

import java.util.Map;

/**
 * @author: gan
 * @date: 2020/4/17
 * predicates: # and ; if need or add a new route
 * - name: Method
 * args:
 * methods:Get,Put
 * value:myCookieValue
 * or
 * <p>
 * predicates: # and ; if need or add a new route
 * - Method = Get,Put
 */
public class MethodPredicate extends AbstractPredicate {
    private static final String METHOD_ARG_KEY = "methods";
    private String[] methods;

    @Override
    public boolean test(FilterContext context) {
        FullHttpRequest fullHttpRequest = context.getCtx().getOriFullHttpRequest();
        if (fullHttpRequest == null || fullHttpRequest.uri() == null) {
            return Boolean.FALSE;
        }
        Map<String, String> predicateConfig = getPredicateConfig(context);
        setMethods(predicateConfig);
        String method = fullHttpRequest.method().name();
        for (String configMethod : methods) {
            if (configMethod != null && configMethod.equalsIgnoreCase(method)) {
                return true;
            }
        }
        return false;
    }

    private void setMethods(Map<String, String> predicateConfig) {
        String method;
        if (predicateConfig.containsKey(METHOD_ARG_KEY)) {
            method = predicateConfig.get(METHOD_ARG_KEY);
        } else {
            method = predicateConfig.get(predicateConfig.keySet().stream().findFirst().orElse(""));
        }
        methods = StringUtils.tokenizeToStringArray(method, ",");
    }


}

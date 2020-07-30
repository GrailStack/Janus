package org.xujin.janus.core.predicates;

import org.xujin.janus.core.context.FilterContext;
import org.xujin.janus.config.util.StringUtils;
import io.netty.handler.codec.http.cookie.Cookie;

import java.util.*;
import java.util.regex.Pattern;

/**
 * @author: gan
 * @date: 2020/4/17
 * predicates: # and ; if need or add a new route
 * - name: Cookie
 * args:
 * name:myCookie
 * value:myCookieValue
 * or
 * <p>
 * predicates: # and ; if need or add a new route
 * - Cookie = myCookie:myCookieValue
 */
public class CookiePredicate extends AbstractPredicate {
    private static final String NAME_ARG_KEY = "name";
    private static final String VALUE_ARG_KEY = "value";
    private Map<String, Pattern> cookiePatterns;

    @Override
    public boolean test(FilterContext context) {
        Map<String, String> predicateConfig = getPredicateConfig(context);
        setCookiePatterns(predicateConfig);
        Set<Cookie> cookies = context.getCtx().getCookies();
        if (cookies == null || cookies.size() <= 0) {
            return false;
        }
        for (Cookie cookie : cookies) {
            Pattern valuePattern = cookiePatterns.get(cookie.name());
            if (valuePattern == null) {
                return false;
            }
            if (valuePattern.matcher(cookie.value()).matches()) {
                return Boolean.TRUE;
            }
        }
        return false;
    }

    private void setCookiePatterns(Map<String, String> predicateConfig) {
        String name;
        String value;
        String nameWithValue;
        if (predicateConfig.containsKey(NAME_ARG_KEY) && predicateConfig.containsKey(VALUE_ARG_KEY)) {
            name = predicateConfig.get(NAME_ARG_KEY);
            value = predicateConfig.get(VALUE_ARG_KEY);
        } else {
            nameWithValue = predicateConfig.get(predicateConfig.keySet().stream().findFirst().orElse(""));
            String[] nameWithValues = StringUtils.tokenizeToStringArray(nameWithValue, ":");
            name = nameWithValues[0];
            value = nameWithValues[1];
        }
        cookiePatterns = new HashMap<>(1);
        cookiePatterns.put(name, Pattern.compile(value));
    }


}

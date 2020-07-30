package org.xujin.janus.core.app.predicates;

import org.xujin.janus.core.context.FilterContext;
import org.xujin.janus.core.predicates.AbstractPredicate;

import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * @author: gan
 * @date: 2020/4/17
 * predicates: # and ; if need or add a new route
 * - name: Path
 * args:
 * pattern: /delay/{timeout}
 * or
 * <p>
 * predicates: # and ; if need or add a new route
 * - Path = /api/service
 */
public class DynamicPathPredicate extends AbstractPredicate {
    private static final String PATTERN_ARG_KEY = "pattern";
    private List<Pattern> pathPatterns;

    public DynamicPathPredicate(Map<String, String> predicateArgs) {
        super(predicateArgs);
    }

    @Override
    public boolean test(FilterContext context) {
        return Boolean.TRUE;
    }


}

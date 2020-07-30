package org.xujin.janus.core.predicates;

/**
 * @author: gan
 * @date: 2020/4/29
 */
public class StaticPredicateLoader {

    public static final CookiePredicate COOKIE_PREDICATE = new CookiePredicate();
    public static final PathRegexPredicate PATH_REGEX_PREDICATE = new PathRegexPredicate();
    public static final PathPrecisePredicate PATH_PRECISE_PREDICATE = new PathPrecisePredicate();
    public static final MethodPredicate METHOD_PREDICATE = new MethodPredicate();

    public static void load() {
        PredicateRepo.add(COOKIE_PREDICATE);
        PredicateRepo.add(PATH_REGEX_PREDICATE);
        PredicateRepo.add(PATH_PRECISE_PREDICATE);
        PredicateRepo.add(METHOD_PREDICATE);
    }
}

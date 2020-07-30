package org.xujin.janus.core.predicates;

import org.xujin.janus.core.util.NameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Predicate;

/**
 * Predicate class can dynamic compile.
 *
 * @author: gan
 * @date: 2020/4/22
 */
public class PredicateRepo {
    private static final Logger logger = LoggerFactory.getLogger(PredicateRepo.class);
    private static Map<String, Predicate> allPredicates = new ConcurrentHashMap<>();

    public static void add(Predicate predicate) {
        String key = NameUtils.normalizePredicateName(predicate.getClass());
        allPredicates.putIfAbsent(key, predicate);
    }

    public static void addOrUpdate(Predicate predicate) {
        String key = NameUtils.normalizePredicateName(predicate.getClass());
        if (allPredicates.get(key) == null) {
            allPredicates.put(key, predicate);
        } else {
            allPredicates.replace(key, predicate);
        }
    }

    public static Map<String, Predicate> getAllPredicates() {
        return allPredicates;
    }

    public static Predicate get(String predicateName) {
        if (allPredicates == null || allPredicates.size() <= 0) {
            logger.warn("allPredicates is empty");
            return null;
        }
        Predicate predicate = allPredicates.get(predicateName.trim());
        if (predicate == null) {
            logger.warn(predicateName + " not in allPredicates");
            return null;
        }
        return predicate;
    }

}

package org.xujin.janus.core.util;

import org.xujin.janus.core.filter.Filter;

import java.util.function.Predicate;


/**
 * @author: gan
 * @date: 2020/4/18
 */
public final class NameUtils {

    private NameUtils() {
        throw new AssertionError("Must not instantiate utility class.");
    }


    public static String normalizePredicateName(
            Class<? extends Predicate> clazz) {
        return removeGarbage(clazz.getSimpleName()
                .replace("Predicate", ""));
    }

    public static String normalizeFilterName(
            Class<? extends Filter> clazz) {
        return removeGarbage(clazz.getSimpleName()
                .replace("Filter", ""));
    }

    private static String removeGarbage(String s) {
        int garbageIdx = s.indexOf("$Mockito");
        if (garbageIdx > 0) {
            return s.substring(0, garbageIdx);
        }

        return s;
    }

}

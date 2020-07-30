package org.xujin.janus.monitor.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.function.Consumer;
import java.util.function.Function;

/**
 * Desc:
 *
 * @author yage.luan
 * @date 2020/5/19 16:42
 **/
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class SysPropertyUtil {

    public static void ifExistThenSet(String property, Consumer<String> c) {
        String value = System.getProperty(property);
        if (value != null) {
            c.accept(value);
        }
    }

    public static <T> void ifExistThenSet(String property, Function<String, T> converter, Consumer<T> c) {
        String value = System.getProperty(property);
        if (value != null) {
            c.accept(converter.apply(value));
        }
    }
}

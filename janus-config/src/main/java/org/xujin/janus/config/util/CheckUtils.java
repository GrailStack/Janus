package org.xujin.janus.config.util;

/**
 * @author: gan
 * @date: 2020/6/2
 */
public class CheckUtils {
    public static void checkNotNull(Object field, String message) {
        if (field == null) {
            throw new NullPointerException(message);
        }
    }
    public static void checkNull(Object field, String message) {
        if (field != null) {
            throw new NullPointerException(message);
        }
    }
    public static void checkNonEmpty(String field, String message) {
        if (field == null || field.isEmpty()) {
            throw new IllegalArgumentException(message);
        }
    }
    public static void checkParam(boolean expect,String message){
        if(!expect){
            throw new IllegalArgumentException(message);
        }
    }
}

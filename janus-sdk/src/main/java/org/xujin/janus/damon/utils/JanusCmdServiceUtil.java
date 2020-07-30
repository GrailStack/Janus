package org.xujin.janus.damon.utils;

/**
 * @author tbkk 2019-02-18
 */
public class JanusCmdServiceUtil {

    public static String genServiceKey(String iface, String version) {
        String serviceKey = iface;
        if (version != null && version.trim().length() > 0) {
            serviceKey += "#".concat(version);
        }
        return serviceKey;
    }
}

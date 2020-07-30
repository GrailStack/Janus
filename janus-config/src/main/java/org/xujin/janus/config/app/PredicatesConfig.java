package org.xujin.janus.config.app;


import org.xujin.janus.config.util.StringUtils;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author: gan
 * @date: 2020/4/17
 */
public class PredicatesConfig {
    private String name;

    private Map<String, String> args;

    public PredicatesConfig() {

    }

    public PredicatesConfig(String name, Map<String, String> args) {
        this.name = name;
        this.args = args;
    }

    /**
     * text should like: name=arg1,arg2,arg3......
     *
     * @param text
     */
    public PredicatesConfig(String text) {
        int eqIdx = text.indexOf('=');
        if (eqIdx <= 0) {
            throw new IllegalArgumentException("Unable to parse PredicateDefinition text '"
                    + text + "'" + ", must be of the form name=value");
        }
        setName(text.substring(0, eqIdx));

        String[] argsString = StringUtils.tokenizeToStringArray(text.substring(eqIdx + 1), ",");
        this.args = new LinkedHashMap<>();
        for (int i = 0; i < argsString.length; i++) {
            this.args.put(i + "", argsString[i]);
        }
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Map<String, String> getArgs() {
        return args;
    }

    public void setArgs(Map<String, String> args) {
        this.args = args;
    }

    public void addArg(String key, String value) {
        this.args.put(key, value);
    }

}

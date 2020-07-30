package org.xujin.janus.config.app;



import org.xujin.janus.config.util.StringUtils;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author: gan
 * @date: 2020/4/17
 */
public class FilterConfig {
    public static final String AUTO_GENERATE_ARG_KEY = "auto_generate_arg_key";
    private String name;

    private Map<String, String> args;

    public FilterConfig() {

    }

    public FilterConfig(String name, Map<String, String> args) {
        this.name = name;
        this.args = args;
    }

    /**
     * text should like: name=arg1,arg2,arg3...... or only have name
     *
     * @param text
     */
    public FilterConfig(String text) {
        int eqIdx = text.indexOf('=');
        if (eqIdx <= 0) {
            setName(text);
        } else {
            setName(text.substring(0, eqIdx));

            String[] argsStr = StringUtils.tokenizeToStringArray(text.substring(eqIdx + 1), ",");
            this.args = new LinkedHashMap<>();
            for (int i = 0; i < argsStr.length; i++) {
                this.args.put(AUTO_GENERATE_ARG_KEY + i, argsStr[i]);
            }
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

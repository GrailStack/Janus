package org.xujin.janus.filter.filters;


import org.xujin.janus.core.filter.filters.AbstractSyncFilter;
import io.netty.handler.codec.http.HttpHeaders;

import java.util.*;

/**
 * @author: gan
 * @date: 2020/5/19
 */
public abstract class AbstractHeaderFilter extends AbstractSyncFilter {
    private static final String HEADER_KEY_VALUE_DELIMITER = ":";
    private static final String HEADERS_DELIMITER = ",";

    protected void addHeaders(Map<String, String> config, HttpHeaders httpHeaders) {
        AddHeaderConfig addHeaderConfig = new AddHeaderConfig(config);
        addHeaderConfig.getHeaders().forEach((key, value) -> {
            addHeaders(httpHeaders, key, value);
        });
    }

    protected void removeHeaders(Map<String, String> config, HttpHeaders httpHeaders) {
        RemoveHeaderConfig removeHeaderConfig = new RemoveHeaderConfig(config);
        removeHeaderConfig.getKeys().forEach(key -> {
            httpHeaders.remove(key);
        });
    }

    private void addHeaders(HttpHeaders httpHeaders, String name, Object value) {
        if (httpHeaders == null) {
            return;
        }
        if (httpHeaders.contains(name)) {
            httpHeaders.remove(name);
        }
        httpHeaders.add(name, value);
    }

    private class RemoveHeaderConfig {
        private List<String> keys = new ArrayList<>();

        public RemoveHeaderConfig(Map<String, String> config) {
            if (config == null) {
                return;
            }
            String autoGenKey = getAutoGenerateKey(config);
            if (autoGenKey != null) {
                Optional.ofNullable(config.get(autoGenKey)).ifPresent(e -> {
                    this.setKeys(Arrays.asList(e.split(HEADERS_DELIMITER)));
                });
            } else {
                //config with parameter name
                Optional.ofNullable(config.get("headers")).ifPresent(e -> {
                    this.setKeys(Arrays.asList(e.split(HEADERS_DELIMITER)));
                });
            }
        }

        public List<String> getKeys() {
            return keys;
        }

        public void setKeys(List<String> keys) {
            this.keys = keys;
        }
    }

    private class AddHeaderConfig {
        private Map<String, String> headers = new HashMap<>();

        public AddHeaderConfig(Map<String, String> config) {
            if (config == null) {
                return;
            }
            String autoGenKey = getAutoGenerateKey(config);
            if (autoGenKey != null) {
                Optional.ofNullable(config.get(autoGenKey)).ifPresent(e -> {
                    //auto_generate_arg_key_0=X-Request-Red:Red,X-Request-Black:Black
                    String[] headersStr = e.split(HEADERS_DELIMITER);
                    for (String header : headersStr) {
                        String[] keyValues = header.split(HEADER_KEY_VALUE_DELIMITER);
                        if (keyValues.length > 1) {
                            headers.put(keyValues[0], keyValues[1]);
                        }
                    }
                });
            } else {
                config.forEach((key, value) -> {
                    //X-Request-Red=Red,X-Request-Black=Black
                    headers.put(key, value);
                });
            }
        }

        public Map<String, String> getHeaders() {
            return headers;
        }

        public void setHeaders(Map<String, String> headers) {
            this.headers = headers;
        }
    }
}

package org.xujin.janus.core.context;

import java.util.HashMap;

/**
 * @author: gan
 * @date: 2020/4/15
 */
public class SessionContext extends HashMap<String, Object> implements Cloneable {

    private static final int INITIAL_SIZE = 8;

    public SessionContext() {
        /**
         * Use a lower than default initial capacity for the hashmap as we generally have more than the default
         * 8 entries.
         */
        super(INITIAL_SIZE);
    }
}

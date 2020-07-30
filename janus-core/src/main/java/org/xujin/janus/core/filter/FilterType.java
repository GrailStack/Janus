package org.xujin.janus.core.filter;

/**
 * execute order is pre_in >> in >> invoke >> out >> after_out
 *
 * @author: gan
 * @date: 2020/4/15
 */
public enum FilterType {
    /**
     * execute before in
     */
    PRE_IN("pre_in", 1),
    /**
     * in,process inbound request filter
     */
    INBOUND("in", 2),
    /**
     * dispatch to other service filter
     */
    INVOKE("invoke", 3),
    /**
     * out,process outbound response filter
     */
    OUTBOUND("out", 4),
    /**
     * execute after out
     */
    AFTER_OUT("after_out", 5);

    private final String shortName;
    private final int index;

    FilterType(String shortName, int index) {
        this.shortName = shortName;
        this.index = index;
    }

    public int getIndex() {
        return this.index;
    }

    @Override
    public String toString() {
        return shortName;
    }

}

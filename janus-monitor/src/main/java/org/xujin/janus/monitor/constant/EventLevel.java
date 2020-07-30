package org.xujin.janus.monitor.constant;

/**
 * @author: gan
 * @date: 2020/6/8
 */
public enum EventLevel {
    /**
     * 严重的错误
     */
    Critical(5),
    /**
     * 错误
     */
    Error(4),
    /**
     * 警告
     */
    Warning(3),
    /**
     * 告知
     */
    Informational(2),
//    /**
//     * 记录详细的日志
//     */
//    Verbose(1),
//    /**
//     * 记录日志
//     */
//    LogAlways(0),
    ;
    int level;

    private EventLevel(int level) {
        this.level = level;
    }

    public int getLevel() {
        return this.level;
    }
}

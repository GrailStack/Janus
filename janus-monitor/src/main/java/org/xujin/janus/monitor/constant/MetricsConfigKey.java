package org.xujin.janus.monitor.constant;

/**
 * Desc:
 *
 * @author yage.luan
 * @date 2020/5/19 16:24
 **/
public class MetricsConfigKey {

    public static final String EXPORT_ENABLED = "janus.metrics.export.enabled";
    public static final String EXPORT_STEP_SECONDS = "janus.metrics.export.step.seconds";

    public static final String INFLUX_DB = "janus.metrics.influx.db";
    public static final String INFLUX_URI = "janus.metrics.influx.uri";
    public static final String INFLUX_USERNAME = "janus.metrics.influx.username";
    public static final String INFLUX_PASSWORD = "janus.metrics.influx.password";

    public static final String JVM_BINDER_ENABLED = "janus.metrics.binder.jvm.enabled";
    public static final String UPTIME_BINDER_ENABLED = "janus.metrics.binder.uptime.enabled";
    public static final String PROCESSOR_BINDER_ENABLED = "janus.metrics.binder.processor.enabled";
    public static final String FILES_BINDER_ENABLED = "janus.metrics.binder.files.enabled";
    public static final String DISK_BINDER_ENABLED = "janus.metrics.binder.disk.enabled";
    public static final String DISK_BINDER_PATH = "janus.metrics.binder.disk.path";
    public static final String MICROMETER_BINDER_ENABLED = "janus.metrics.binder.micrometer.path";

}
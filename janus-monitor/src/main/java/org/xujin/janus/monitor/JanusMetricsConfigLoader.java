package org.xujin.janus.monitor;

import org.xujin.janus.config.ConfigRepo;
import org.xujin.janus.monitor.constant.MetricsConfigKey;
import org.xujin.janus.config.app.JanusInfluxConfig;
import org.xujin.janus.monitor.influx.InfluxConfigImpl;
import org.xujin.janus.monitor.util.DurationParser;
import org.xujin.janus.monitor.util.SysPropertyUtil;

/**
 * Desc:
 *
 * @author yage.luan
 * @date 2020/5/19 11:49
 **/
public class JanusMetricsConfigLoader {

    public InfluxConfigImpl loadInfluxConfig() {
        JanusInfluxConfig properties = ConfigRepo.getServerConfig().getJanusInfluxConfig();
        if (properties == null) {
            properties = new JanusInfluxConfig();
        }

        SysPropertyUtil.ifExistThenSet(MetricsConfigKey.INFLUX_DB, properties::setDb);
        SysPropertyUtil.ifExistThenSet(MetricsConfigKey.INFLUX_URI, properties::setUri);
        SysPropertyUtil.ifExistThenSet(MetricsConfigKey.INFLUX_USERNAME, properties::setUserName);
        SysPropertyUtil.ifExistThenSet(MetricsConfigKey.INFLUX_PASSWORD, properties::setPassword);

        SysPropertyUtil.ifExistThenSet(MetricsConfigKey.EXPORT_ENABLED, Boolean::valueOf, properties::setExportEnabled);
        SysPropertyUtil.ifExistThenSet(MetricsConfigKey.EXPORT_STEP_SECONDS, DurationParser::parseSeconds, properties::setExportStep);

        return new InfluxConfigImpl(properties);
    }

    public JanusMetricsConfig loadJanusMetricsConfig() {
        JanusMetricsConfig config = new JanusMetricsConfig();
        SysPropertyUtil.ifExistThenSet(MetricsConfigKey.JVM_BINDER_ENABLED, Boolean::valueOf, config::setJvmBinderEnabled);
        SysPropertyUtil.ifExistThenSet(MetricsConfigKey.UPTIME_BINDER_ENABLED, Boolean::valueOf, config::setUptimeBinderEnabled);
        SysPropertyUtil.ifExistThenSet(MetricsConfigKey.PROCESSOR_BINDER_ENABLED, Boolean::valueOf, config::setProcessorBinderEnabled);
        SysPropertyUtil.ifExistThenSet(MetricsConfigKey.FILES_BINDER_ENABLED, Boolean::valueOf, config::setFilesBinderEnabled);
        SysPropertyUtil.ifExistThenSet(MetricsConfigKey.DISK_BINDER_ENABLED, Boolean::valueOf, config::setDiskBinderEnabled);
        SysPropertyUtil.ifExistThenSet(MetricsConfigKey.DISK_BINDER_PATH, config::setDiskBinderPath);
        SysPropertyUtil.ifExistThenSet(MetricsConfigKey.MICROMETER_BINDER_ENABLED, Boolean::valueOf, config::setMicrometerBinderEnabled);
        return config;
    }

}

package org.xujin.janus.monitor;

import org.xujin.janus.config.ApplicationConfig;
import org.xujin.janus.damon.utils.IpUtil;
import org.xujin.janus.monitor.constant.MetricsConst;
import org.xujin.janus.monitor.influx.InfluxConfigImpl;
import org.xujin.janus.monitor.metrics.MicrometerBinder;
import io.micrometer.core.instrument.Clock;
import io.micrometer.core.instrument.Metrics;
import io.micrometer.core.instrument.Tag;
import io.micrometer.core.instrument.binder.MeterBinder;
import io.micrometer.core.instrument.binder.jvm.*;
import io.micrometer.core.instrument.binder.system.FileDescriptorMetrics;
import io.micrometer.core.instrument.binder.system.ProcessorMetrics;
import io.micrometer.core.instrument.binder.system.UptimeMetrics;
import io.micrometer.influx.InfluxMeterRegistry;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Desc:
 *
 * @author yage.luan
 * @date 2020/5/18 20:12
 **/
@Slf4j
public class JanusMetricsInitializer {

    private static InfluxConfigImpl influxConfig;

    private static JanusMetricsConfig metricsConfig;

    private static List<MeterBinder> meterBinders;

    public static void init() {
        JanusMetricsConfigLoader configLoader = new JanusMetricsConfigLoader();
        influxConfig = configLoader.loadInfluxConfig();

        InfluxMeterRegistry meterRegistry = new InfluxMeterRegistry(influxConfig, Clock.SYSTEM);
        Metrics.addRegistry(meterRegistry);
        meterRegistry.config().commonTags(getCommonTags());

        metricsConfig = configLoader.loadJanusMetricsConfig();
        initMeterBinder();
    }

    private static List<Tag> getCommonTags() {
        return Arrays.asList(
                Tag.of(MetricsConst.TAG_KEY_ENV, ApplicationConfig.getEnv()),
                Tag.of(MetricsConst.TAG_KEY_CLUSTER_NAME, ApplicationConfig.getCluster()),
                Tag.of(MetricsConst.TAG_KEY_HOST, IpUtil.getIp()+":"+ApplicationConfig.getApplicationPort()+ "")
        );
    }

    private static void initMeterBinder() {
        meterBinders = new ArrayList<>();
        if (metricsConfig.getJvmBinderEnabled()) {
            meterBinders.add(new JvmGcMetrics());
            meterBinders.add(new JvmMemoryMetrics());
            meterBinders.add(new JvmThreadMetrics());
            meterBinders.add(new ClassLoaderMetrics());
        }
        if (metricsConfig.getUptimeBinderEnabled()) {
            meterBinders.add(new UptimeMetrics());
        }
        if (metricsConfig.getProcessorBinderEnabled()) {
            meterBinders.add(new ProcessorMetrics());
        }
        if (metricsConfig.getFilesBinderEnabled()) {
            meterBinders.add(new FileDescriptorMetrics());
        }
        if (metricsConfig.getDiskBinderEnabled()) {
            meterBinders.add(new DiskSpaceMetrics(new File(metricsConfig.getDiskBinderPath())));
        }
        if (metricsConfig.getMicrometerBinderEnabled()) {
            meterBinders.add(new MicrometerBinder());
        }
        meterBinders.forEach(binder -> binder.bindTo(Metrics.globalRegistry));
    }

}

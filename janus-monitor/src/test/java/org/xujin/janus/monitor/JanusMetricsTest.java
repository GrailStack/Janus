package org.xujin.janus.monitor;

import org.xujin.janus.monitor.constant.MetricsConfigKey;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.Metrics;
import io.micrometer.core.instrument.Tags;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

/**
 * Desc:
 *
 * @author yage.luan
 * @date 2020/5/19 10:37
 **/
public class JanusMetricsTest {

    @BeforeClass
    public static void init() {
        System.setProperty(MetricsConfigKey.EXPORT_STEP_SECONDS, "5");
        System.setProperty("env", "dev");
        System.setProperty("clusterName", "order");
        JanusMetricsInitializer.init();
    }

    @Test
    public void test_counter() throws Exception {
        for (int i = 0; i < 10; i++) {
            Counter counter = Metrics.counter("test_counter");
            counter.increment(ThreadLocalRandom.current().nextInt(10));
            TimeUnit.SECONDS.sleep(3);
        }
    }

    @Test
    public void test_count() throws Exception {
        JanusMetrics.counter("test_count", Tags.of("url", "/getInfo"));
        JanusMetrics.counter("test_count", Tags.of("url", "/addUser"));
        JanusMetrics.counter("test_count", Tags.of("url", "/addUser"));
        TimeUnit.SECONDS.sleep(30);
    }


}

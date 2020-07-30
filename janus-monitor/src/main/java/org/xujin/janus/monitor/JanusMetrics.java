package org.xujin.janus.monitor;

import io.micrometer.core.instrument.Metrics;
import io.micrometer.core.instrument.Tags;
import io.micrometer.core.instrument.Timer;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

/**
 * Desc:
 *
 * @author yage.luan
 * @date 2020/5/18 19:56
 **/
@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class JanusMetrics {

    private static final Logger logger = LoggerFactory.getLogger(JanusMetrics.class);

    public static void counter(String name, Tags tags) {
        Metrics.counter(name, Tags.of(tags)).increment();
    }

    public static <T extends Number> void gauge(String name, Tags tags, T number) {
        Metrics.gauge(name, tags, number);
    }

    public static void timer(String name, Tags tags, long duration, TimeUnit timeUnit) {
        Timer timer = Timer.builder(name)
                .tags(tags)
                .publishPercentileHistogram()
                .publishPercentiles(0.5, 0.95, 0.99)
                .serviceLevelObjectives(Duration.ofMillis(275), Duration.ofMillis(300), Duration.ofMillis(500))
                .distributionStatisticExpiry(Duration.ofSeconds(10))
                .distributionStatisticBufferLength(3)
                .register(Metrics.globalRegistry);
        timer.record(duration, timeUnit);
    }
}

package org.xujin.janus.monitor.metrics;

import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Metrics;
import io.micrometer.core.instrument.binder.MeterBinder;
import io.micrometer.core.lang.NonNullApi;

@NonNullApi
public class MicrometerBinder implements MeterBinder {

    @Override
    public void bindTo(MeterRegistry registry) {
        Gauge.builder("micrometer.meters.size", Metrics.globalRegistry, r -> r.getMeters().size())
                .description("The current number registered meters").register(registry);
    }

}

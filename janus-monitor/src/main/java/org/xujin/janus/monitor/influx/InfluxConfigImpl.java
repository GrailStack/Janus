package org.xujin.janus.monitor.influx;

import org.xujin.janus.config.app.JanusInfluxConfig;
import io.micrometer.influx.InfluxConfig;
import lombok.Getter;
import org.apache.commons.lang3.Validate;

import java.time.Duration;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Desc:
 *
 * @author yage.luan
 * @date 2020/5/19 14:46
 **/
public class InfluxConfigImpl implements InfluxConfig {

    @Getter
    private volatile JanusInfluxConfig properties;

    public InfluxConfigImpl(JanusInfluxConfig properties) {
        Validate.notNull(properties, "InfluxConfigImpl required");
        this.properties = properties;
    }

    @Override
    public String prefix() {
        return null;
    }

    @Override
    public String get(String k) {
        return null;
    }

    @Override
    public Duration step() {
        return get(JanusInfluxConfig::getExportStep, InfluxConfig.super::step);
    }

    @Override
    public boolean enabled() {
        return get(JanusInfluxConfig::isExportEnabled, InfluxConfig.super::enabled);
    }

    @Override
    public Duration connectTimeout() {
        return get(JanusInfluxConfig::getConnectTimeout, InfluxConfig.super::connectTimeout);
    }

    @Override
    public Duration readTimeout() {
        return get(JanusInfluxConfig::getReadTimeout, InfluxConfig.super::readTimeout);
    }

    @Override
    public int numThreads() {
        return get(JanusInfluxConfig::getNumThreads, InfluxConfig.super::numThreads);
    }

    @Override
    public int batchSize() {
        return get(JanusInfluxConfig::getBatchSize, InfluxConfig.super::batchSize);
    }

    @Override
    public String db() {
        return get(JanusInfluxConfig::getDb, InfluxConfig.super::db);
    }

//    @Override
//    public InfluxConsistency consistency() {
//        return get(JanusInfluxConfig::getConsistency, InfluxConfig.super::consistency);
//    }

    @Override
    public String userName() {
        return get(JanusInfluxConfig::getUserName, InfluxConfig.super::userName);
    }

    @Override
    public String password() {
        return get(JanusInfluxConfig::getPassword, InfluxConfig.super::password);
    }

    @Override
    public String retentionPolicy() {
        return get(JanusInfluxConfig::getRetentionPolicy,
                InfluxConfig.super::retentionPolicy);
    }

    @Override
    public Integer retentionReplicationFactor() {
        return get(JanusInfluxConfig::getRetentionReplicationFactor,
                InfluxConfig.super::retentionReplicationFactor);
    }

    @Override
    public String retentionDuration() {
        return get(JanusInfluxConfig::getRetentionDuration,
                InfluxConfig.super::retentionDuration);
    }

    @Override
    public String retentionShardDuration() {
        return get(JanusInfluxConfig::getRetentionShardDuration,
                InfluxConfig.super::retentionShardDuration);
    }

    @Override
    public String uri() {
        return get(JanusInfluxConfig::getUri, InfluxConfig.super::uri);
    }

    @Override
    public boolean compressed() {
        return get(JanusInfluxConfig::isCompressed, InfluxConfig.super::compressed);
    }

    @Override
    public boolean autoCreateDb() {
        return get(JanusInfluxConfig::isAutoCreateDb, InfluxConfig.super::autoCreateDb);
    }

    private <V> V get(Function<JanusInfluxConfig, V> getter, Supplier<V> fallback) {
        V value = getter.apply(this.properties);
        return (value != null) ? value : fallback.get();
    }

}

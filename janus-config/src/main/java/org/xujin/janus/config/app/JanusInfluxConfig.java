package org.xujin.janus.config.app;

import java.time.Duration;

/**
 * Desc:
 *
 * @author yage.luan
 * @date 2020/5/18 14:51
 **/
public class JanusInfluxConfig {

    /**
     * Step size (i.e. reporting frequency) to use.
     */
    private Duration exportStep = Duration.ofMinutes(1);

    /**
     * Whether exporting of metrics to this backend is exportEnabled.
     */
    private boolean exportEnabled = true;

    /**
     * Connection timeout for requests to this backend.
     */
    private Duration connectTimeout = Duration.ofSeconds(5);

    /**
     * Read timeout for requests to this backend.
     */
    private Duration readTimeout = Duration.ofSeconds(10);

    /**
     * Number of threads to use with the metrics publishing scheduler.
     */
    private Integer numThreads = 2;

    /**
     * Number of measurements per request to use for this backend. If more measurements
     * are found, then multiple requests will be made.
     */
    private Integer batchSize = 10000;

    /**
     * The db to send metrics to.
     */
    private String db = "janus";

    /**
     * Write consistency for each point.
     */
//    private InfluxConsistency consistency = InfluxConsistency.ONE;

    /**
     * Login user of the Influx server.
     */
    private String userName;

    /**
     * Login password of the Influx server.
     */
    private String password;

    /**
     * Retention policy to use (Influx writes to the DEFAULT retention policy if one is
     * not specified).
     */
    private String retentionPolicy;

    /**
     * Time period for which Influx should retain data in the current database. For
     * instance 7d, check the influx documentation for more details on the duration
     * format.
     */
    private String retentionDuration;

    /**
     * How many copies of the data are stored in the cluster. Must be 1 for a single node
     * instance.
     */
    private Integer retentionReplicationFactor;

    /**
     * Time range covered by a shard group. For instance 2w, check the influx
     * documentation for more details on the duration format.
     */
    private String retentionShardDuration;

    /**
     * URI of the Influx server.
     */
    private String uri = "http://localhost:8086";

    /**
     * Whether to enable GZIP compression of metrics batches published to Influx.
     */
    private boolean compressed = true;

    /**
     * Whether to create the Influx database if it does not exist before attempting to
     * publish metrics to it.
     */
    private boolean autoCreateDb = true;


    public Duration getExportStep() {
        return exportStep;
    }

    public void setExportStep(Duration exportStep) {
        this.exportStep = exportStep;
    }

    public boolean isExportEnabled() {
        return exportEnabled;
    }

    public void setExportEnabled(boolean exportEnabled) {
        this.exportEnabled = exportEnabled;
    }

    public Duration getConnectTimeout() {
        return connectTimeout;
    }

    public void setConnectTimeout(Duration connectTimeout) {
        this.connectTimeout = connectTimeout;
    }

    public Duration getReadTimeout() {
        return readTimeout;
    }

    public void setReadTimeout(Duration readTimeout) {
        this.readTimeout = readTimeout;
    }

    public Integer getNumThreads() {
        return numThreads;
    }

    public void setNumThreads(Integer numThreads) {
        this.numThreads = numThreads;
    }

    public Integer getBatchSize() {
        return batchSize;
    }

    public void setBatchSize(Integer batchSize) {
        this.batchSize = batchSize;
    }

    public String getDb() {
        return db;
    }

    public void setDb(String db) {
        this.db = db;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRetentionPolicy() {
        return retentionPolicy;
    }

    public void setRetentionPolicy(String retentionPolicy) {
        this.retentionPolicy = retentionPolicy;
    }

    public String getRetentionDuration() {
        return retentionDuration;
    }

    public void setRetentionDuration(String retentionDuration) {
        this.retentionDuration = retentionDuration;
    }

    public Integer getRetentionReplicationFactor() {
        return retentionReplicationFactor;
    }

    public void setRetentionReplicationFactor(Integer retentionReplicationFactor) {
        this.retentionReplicationFactor = retentionReplicationFactor;
    }

    public String getRetentionShardDuration() {
        return retentionShardDuration;
    }

    public void setRetentionShardDuration(String retentionShardDuration) {
        this.retentionShardDuration = retentionShardDuration;
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public boolean isCompressed() {
        return compressed;
    }

    public void setCompressed(boolean compressed) {
        this.compressed = compressed;
    }

    public boolean isAutoCreateDb() {
        return autoCreateDb;
    }

    public void setAutoCreateDb(boolean autoCreateDb) {
        this.autoCreateDb = autoCreateDb;
    }
}

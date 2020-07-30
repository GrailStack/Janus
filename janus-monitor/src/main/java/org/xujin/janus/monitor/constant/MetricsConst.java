package org.xujin.janus.monitor.constant;

/**
 * Desc:
 *
 * @author yage.luan
 * @date 2020/5/19 10:13
 **/
public class MetricsConst {

    public static final String INFLUX_DB = "janus";

    /**
     * metric names
     */

    /**
     * request finished 统计 tps latency
     */
    public static String METRIC_FINISH_REQUEST = "request.finished";
    /**
     * request received 统计 qps
     */
    public static String METRIC_RECEIVE_REQUEST = "request.received";
    /**
     * request error 统计sla
     */
    public static String METRIC_ERROR_REQUEST = "request.error";
    /**
     * connection
     */
    public static String METRIC_CONNECTIONS = "connections";
    public static String METRIC_CONCURRENT_CONNECTIONS = "concurrent.connections";

    /**
     * Route metric
     */
    public static String METRIC_ROUTE_COUNT="route.count";

    /**
     * common tags key
     */
    public static String TAG_KEY_HOST="host";
    public static String TAG_KEY_ENV = "env";
    public static String TAG_KEY_CLUSTER_NAME = "cluster.name";

    /**
     * custom tags key
     */
    public static String TAG_KEY_URL = "url";
    public static String TAG_KEY_REMOTE_IP = "remote.ip";


}

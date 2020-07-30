package org.xujin.janus.core.constant;

/**
 * constant key name use in SessionContext
 *
 * @author: gan
 * @date: 2020/4/17
 */
public class SessionContextKey {
    /**
     * use for filter execute
     */
    public static final String FILTER_ROUTE = "filter_route";
    public static final String FILTER_INDEX = "filter_index";
    public static final String FILTER_START_TIME = "filter_start_time";
    /**
     * use for predicate test
     */
    public static final String PREDICATE_ROUTE = "predicate_route";


    /**
     * judge invoke filter execute
     */
    public static final String MOCK_RESPONSE = "invoke_filter_name";

    /**
     * circuit breaker
     */
    public static final String CIRCUIT_BREAKER = "circuit_breaker";
    public static final String CIRCUIT_BREAKER_START_TIME = "circuit_breaker_start_time";

    /**
     * retry
     */
    public static final String RETRY_CONTEXT = "retry_context";
    public static final String RETRY = "retry";

}

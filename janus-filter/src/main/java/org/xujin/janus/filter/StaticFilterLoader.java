package org.xujin.janus.filter;

import org.xujin.janus.core.filter.FilterRepo;
import org.xujin.janus.core.filter.filters.HttpFilter;
import org.xujin.janus.core.filter.filters.HttpLbByHostsFilter;
import org.xujin.janus.core.filter.filters.LBHttpFilter;
import org.xujin.janus.core.filter.filters.WriteToClientFilter;
import org.xujin.janus.core.resilience.breaker.CircuitBreakerFilter;
import org.xujin.janus.core.resilience.retry.RetryFilter;
import org.xujin.janus.filter.filters.*;

/**
 * @author: gan
 * @date: 2020/4/29
 */
public class StaticFilterLoader {

    public static final RetryFilter RETRY_FILTER = new RetryFilter();
    public static final CircuitBreakerFilter BREAKER_FILTER = new CircuitBreakerFilter();
    public static final AuthTokenFilter AUTH_TOKEN_FILTER = new AuthTokenFilter();
    public static final StripPrefixFilter STRIP_PREFIX_FILTER = new StripPrefixFilter();
    public static final PrefixPathFilter PREFIX_PATH_FILTER = new PrefixPathFilter();
    public static final PathMappingFilter PATH_MAPPING_FILTER = new PathMappingFilter();
    public static final AddRequestHeaderFilter ADD_REQUEST_HEADER_FILTER = new AddRequestHeaderFilter();
    public static final RemoveRequestHeaderFilter REMOVE_REQUEST_HEADER_FILTER = new RemoveRequestHeaderFilter();
    public static final MockResponseFilter MOCK_RESPONSE_FILTER = new MockResponseFilter();
    public static final HttpFilter HTTP_INVOKE_FILTER = new HttpFilter();
    public static final LBHttpFilter LOAD_BALANCE_INVOKE_FILTER = new LBHttpFilter();
    public static final HttpLbByHostsFilter LB_HOSTS_INVOKE_FILTER = new HttpLbByHostsFilter();

    public static final AddResponseHeaderFilter ADD_RESPONSE_HEADER_FILTER = new AddResponseHeaderFilter();
    public static final RemoveResponseHeaderFilter REMOVE_RESPONSE_HEADER_FILTER = new RemoveResponseHeaderFilter();
    public static final WriteToClientFilter WRITE_TO_CLIENT_FILTER = new WriteToClientFilter();

    public static void load() {
        //pre-in
        FilterRepo.add(BREAKER_FILTER);
        FilterRepo.add(RETRY_FILTER);
        //inbound
        FilterRepo.add(AUTH_TOKEN_FILTER);
        FilterRepo.add(STRIP_PREFIX_FILTER);
        FilterRepo.add(PREFIX_PATH_FILTER);
        FilterRepo.add(PATH_MAPPING_FILTER);
        FilterRepo.add(ADD_REQUEST_HEADER_FILTER);
        FilterRepo.add(REMOVE_REQUEST_HEADER_FILTER);
        FilterRepo.add(MOCK_RESPONSE_FILTER);
        //invoke
        FilterRepo.add(HTTP_INVOKE_FILTER);
        FilterRepo.add(LOAD_BALANCE_INVOKE_FILTER);
        FilterRepo.add(LB_HOSTS_INVOKE_FILTER);

        //outbound
        FilterRepo.add(ADD_RESPONSE_HEADER_FILTER);
        FilterRepo.add(REMOVE_RESPONSE_HEADER_FILTER);
        //after-out
        FilterRepo.add(WRITE_TO_CLIENT_FILTER);

        //注册系统filter
        FilterRepo.addSystemFilters(HTTP_INVOKE_FILTER);
        FilterRepo.addSystemFilters(LOAD_BALANCE_INVOKE_FILTER);
        FilterRepo.addSystemFilters(LB_HOSTS_INVOKE_FILTER);
        FilterRepo.addSystemFilters(WRITE_TO_CLIENT_FILTER);

    }
}

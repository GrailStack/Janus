package org.xujin.janus.core.resilience.retry;

import org.xujin.janus.core.context.FilterContext;
import org.xujin.janus.core.filter.Filter;
import org.xujin.janus.core.filter.FilterRepo;
import org.xujin.janus.core.route.Route;
import org.xujin.janus.core.util.NameUtils;
import io.github.resilience4j.retry.Retry;
import io.github.resilience4j.retry.RetryConfig;

import java.time.Duration;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.function.Predicate;

/**
 * @author: gan
 * @date: 2020/5/14
 */
public class RetryRepo {
    /**
     * key: route id
     */
    private static ConcurrentMap<String, Retry> allRetry = new ConcurrentHashMap<>();

    public static void add(String name, Retry retry) {
        allRetry.putIfAbsent(name, retry);
    }

    public static void removeIfExist(String retryName) {
        if (allRetry.containsKey(retryName)) {
            allRetry.remove(retryName);
        }
    }

    public static void addForRoute(Route route) {
        String retryName = route.getId();
        Retry retry = allRetry.get(retryName);
        if (retry != null) {
            return;
        }
        String filterName = NameUtils.normalizeFilterName(RetryFilter.class);
        Filter retryFilter = FilterRepo.get(filterName);


        if (retryFilter == null || retryFilter.getConfig(route.getId()) == null ||
                retryFilter.getConfig(route.getId()).size() <= 0) {
            retry = RetryFactory.create(retryName);
        } else {
            retry = RetryFactory.create(retryName, fromFilterArg(retryFilter.getConfig(route.getId())));
        }
        add(retryName, retry);

    }

    public static Retry get(String retryName) {
        return allRetry.get(retryName);
    }

    public static ConcurrentMap<String, Retry> getAllRetry() {
        return allRetry;
    }

    private static RetryConfig fromFilterArg(Map<String, String> filterArg) {
        RetryConfig.Builder builder = RetryConfig.<FilterContext>custom();
        Optional.ofNullable(filterArg.get("maxAttempts"))
                .ifPresent(e -> builder.maxAttempts(Integer.parseInt(e)));
        Optional.ofNullable(filterArg.get("waitDuration"))
                .ifPresent(e -> builder.waitDuration(Duration.ofMillis(Long.parseLong(e))));

        Predicate<FilterContext> resultPredicate = filterContext -> filterContext.getCtx() != null &&
                filterContext.getCtx().getOriHttpResponse() != null;
        String methods = filterArg.get("methods");
        Predicate<FilterContext> methodPredicate = getMethodPredicate(methods);
        if (methodPredicate != null) {
            resultPredicate = resultPredicate.and(methodPredicate);
        }
        String statusCodes = filterArg.get("statusCodes");
        Predicate<FilterContext> statusCodePredicate = getStatusCodePredicate(statusCodes);
        if (statusCodePredicate != null) {
            resultPredicate = resultPredicate.and(statusCodePredicate);
        }
        builder.retryOnResult(resultPredicate);
        return builder.build();

    }

    private static Predicate<FilterContext> getMethodPredicate(Object methods) {
        if (methods == null) {
            return null;
        }
        String[] methodNames = methods.toString().split(",");
        Predicate<FilterContext> predicateFirst = filterContext -> filterContext.getCtx().getOriFullHttpRequest()
                .method().name().equalsIgnoreCase(methodNames[0]);
        if (methodNames.length > 1) {
            for (int i = 1; i < methodNames.length; i++) {
                String methodName = methodNames[i];
                Predicate<FilterContext> filterContextPredicate = filterContext -> filterContext.getCtx()
                        .getOriFullHttpRequest().method().name().equalsIgnoreCase(methodName);
                predicateFirst = predicateFirst.or(filterContextPredicate);
            }
        }
        return predicateFirst;
    }

    private static Predicate<FilterContext> getStatusCodePredicate(Object statusCodes) {
        if (statusCodes == null) {
            return null;
        }
        String[] statusCodesArray = statusCodes.toString().split(",");
        Predicate<FilterContext> predicateFirst = filterContext -> filterContext.getCtx().getOriHttpResponse()
                .status().codeAsText().toString().equalsIgnoreCase(statusCodesArray[0]);
        if (statusCodesArray.length > 1) {
            for (int i = 1; i < statusCodesArray.length; i++) {
                String statusCode = statusCodesArray[i];
                Predicate<FilterContext> filterContextPredicate = filterContext -> filterContext.getCtx()
                        .getOriHttpResponse().status().codeAsText().toString().equalsIgnoreCase(statusCode);
                predicateFirst = predicateFirst.or(filterContextPredicate);
            }
        }
        return predicateFirst;
    }
}

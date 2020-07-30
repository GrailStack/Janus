package org.xujin.janus.core.filter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * @author: gan
 * @date: 2020/4/22
 */
public class FilterRepo {
    private static final Logger logger = LoggerFactory.getLogger(FilterRepo.class);
    /**
     * 所有的Filter
     */
    private static ConcurrentMap<String, Filter> allFilters = new ConcurrentHashMap<>();

    /**
     * 系统级Filter 每个路由必须执行的
     */
    private static List<Filter> systemFilters = new CopyOnWriteArrayList<>();


    public static void add(Filter filter) {
        String key = filter.name();
        allFilters.putIfAbsent(key, filter);
    }

    public static void addOrUpdate(Filter filter) {
        String key = filter.name();
        if (allFilters.get(key) == null) {
            allFilters.put(key, filter);
        } else {
            allFilters.replace(key, filter);
        }
    }

    public static void addSystemFilters(Filter filter) {
        systemFilters.add(filter);
    }

    public static List<Filter> getSystemFilters() {
        return systemFilters;
    }

    public static Map<String, Filter> getAllFilters() {
        return allFilters;
    }

    public static Filter get(String filterName) {
        if (allFilters == null || allFilters.size() <= 0) {
            logger.warn("allFilters is empty");
            return null;
        }
        Filter filter = allFilters.get(filterName);
        if (filter == null) {
            logger.error("Filter " + filterName +
                    " not exist,please check filter name in config file and check whether to register in the FilterRepo");
            throw new RuntimeException("Filter " + filterName +
                    " not exist,please check filter name in config file and check whether to register in the FilterRepo");
        }
        return filter;

    }


}

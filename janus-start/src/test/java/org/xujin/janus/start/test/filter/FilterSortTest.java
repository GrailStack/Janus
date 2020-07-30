package org.xujin.janus.start.test.filter;

import org.xujin.janus.core.filter.Filter;
import org.xujin.janus.core.filter.FilterSort;
import org.xujin.janus.core.filter.filters.HttpFilter;
import org.xujin.janus.core.route.RouteRepo;
import org.xujin.janus.filter.filters.PrefixPathFilter;
import org.xujin.janus.startup.JanusBootStrap;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

/**
 * @author: gan
 * @date: 2020/5/27
 */
public class FilterSortTest {
    @Before
    public void init() {
        JanusBootStrap.initGateway();
    }

    @After
    public void destroy() {
        JanusBootStrap.destroy();
    }
    @Test
    public void testSort(){
        List<Filter> filters=RouteRepo.get("singleFlowAdmin").getFilters();
        filters.add(new PrefixPathFilter());
        List<Filter>  sortFilter= FilterSort.sort(filters);

        Assert.assertTrue(sortFilter.get(0).order()<=sortFilter.get(1).order());
        Assert.assertTrue(sortFilter.get(0).type().getIndex()<=sortFilter.get(1).type().getIndex());
    }
    @Test
    public void TestSort1(){
        PrefixPathFilter prefixPathFilter=new PrefixPathFilter();
        HttpFilter httpFilter =new HttpFilter();
        Assert.assertTrue(prefixPathFilter.compareTo(httpFilter)<0);
    }
}

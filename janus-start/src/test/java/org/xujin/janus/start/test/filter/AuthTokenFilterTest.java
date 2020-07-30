package org.xujin.janus.start.test.filter;

import org.xujin.janus.filter.filters.AuthTokenFilter;
import org.xujin.janus.startup.JanusBootStrap;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * @author: gan
 * @date: 2020/6/10
 */
public class AuthTokenFilterTest {
    @Before
    public void init() {
        JanusBootStrap.initGateway();
    }

    @After
    public void destroy() {
        JanusBootStrap.destroy();
    }
    @Test
    public void test() throws InterruptedException {
        AuthTokenFilter authTokenFilter=new AuthTokenFilter();
        authTokenFilter.doFilter(null);
        Thread.sleep(200000);
    }
}

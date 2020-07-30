package org.xujin.janus.start.test.dynamic;

import org.xujin.janus.core.dynamic.DynamicFileManger;
import org.xujin.janus.core.filter.FilterRepo;
import org.junit.Test;

/**
 * @author: gan
 * @date: 2020/4/29
 */
public class DynamicFileMangerTest {
    @Test
    public void testDynamicFilterLoad() throws InterruptedException {
        DynamicFileManger.startPoller();
        Thread.sleep(3000L);
        FilterRepo.getAllFilters().forEach((key, value) -> {
            System.out.println("key:" + key + ";value:" + value);
        });
    }

    @Test
    public void testDynamicFilterPut() throws InterruptedException {
        DynamicFileManger.startPoller();
        while (true) {
            FilterRepo.getAllFilters().forEach((key, value) -> {
                System.out.println("key:" + key + ";value:" + value);
            });
            Thread.sleep(3000L);
        }
    }

    @Test
    public void testDynamicPredicateLoad() throws InterruptedException {
        DynamicFileManger.startPoller();
        Thread.sleep(3000L);
        FilterRepo.getAllFilters().forEach((key, value) -> {
            System.out.println("key:" + key + ";value:" + value);
        });
    }

}

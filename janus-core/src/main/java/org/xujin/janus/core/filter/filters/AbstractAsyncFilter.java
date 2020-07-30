package org.xujin.janus.core.filter.filters;

import org.xujin.janus.core.context.FilterContext;
import org.xujin.janus.core.filter.FilterChain;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 和同步Filter的不同：执行完Filter后不会自动执行下一个Filter,需要在有异步处理结果显示的来调用complete或者error
 *
 * @author: gan
 * @date: 2020/4/27
 */
public abstract class AbstractAsyncFilter extends AbstractFilter {
    private static final Logger logger = LoggerFactory.getLogger(AbstractAsyncFilter.class);

    /**
     * @param context
     */
    @Override
    public void filter(FilterContext context) {
        if (!isEnable()) {
            logger.info("filter:" + name() + " not execute cause disable,context:" + context.toString());
            FilterChain.next(context);
            return;
        }
        logger.info("filter:" + name() + " execute,context:" + context.toString());
        doFilter(context);
    }

    public void complete(FilterContext context) {
        FilterChain.next(context);
    }

    public void error(FilterContext context, Throwable throwable) {
        logger.error("exec async invoke error", throwable);
        FilterChain.next(context);
    }

    public abstract void doFilter(FilterContext context);
}

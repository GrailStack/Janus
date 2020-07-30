package org.xujin.janus.core.filter.filters;


import org.xujin.janus.core.context.FilterContext;
import org.xujin.janus.core.filter.FilterChain;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author: gan
 * @date: 2020/4/15
 */
public abstract class AbstractSyncFilter extends AbstractFilter {
    private static final Logger logger = LoggerFactory.getLogger(AbstractSyncFilter.class);

    @Override
    public void filter(FilterContext context) {
        if (!isEnable()) {
            logger.info("filter:" + name() + " not execute cause disable,context:" + context.toString());
            FilterChain.next(context);
            return;
        }
        logger.info("filter:" + name() + " execute,context:" + context.toString());
        doFilter(context);
        FilterChain.next(context);

    }

    public abstract void doFilter(FilterContext context);

}

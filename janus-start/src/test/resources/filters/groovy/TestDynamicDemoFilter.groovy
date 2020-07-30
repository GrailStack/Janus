package filters.groovy


import org.xujin.janus.core.context.FilterContext
import org.xujin.janus.core.filter.filters.AbstractSyncFilter
import org.xujin.janus.core.filter.FilterType

/**
 * @author: gan* @date: 2020/4/27
 */
class TestDynamicDemoFilter extends AbstractSyncFilter {
    TestDynamicDemoFilter(Map filterArgs) {
        super(filterArgs)
    }

    @Override
    int order() {
        return 0
    }

    @Override
    FilterType type() {
        return FilterType.INBOUND
    }

    @Override
    void doFilter(FilterContext context) {
        println("dynamic groovy filter run....");
    }
}

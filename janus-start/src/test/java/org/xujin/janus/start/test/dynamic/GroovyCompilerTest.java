package org.xujin.janus.start.test.dynamic;

import org.xujin.janus.core.dynamic.GroovyCompiler;
import org.xujin.janus.core.filter.filters.AbstractFilter;
import org.junit.Test;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

/**
 * @author: gan
 * @date: 2020/4/27
 */
public class GroovyCompilerTest {
    @Test
    public void testCompile() throws Exception {
        java.net.URL url = Thread.currentThread().getContextClassLoader().getResource("filters/groovy/DynamicDemoFilter.groovy");
        File file = new File(url.getPath());
        GroovyCompiler compiler = new GroovyCompiler();
        Class<AbstractFilter> filterClass = (Class<AbstractFilter>) compiler.compile(file);
        Map<String,Object> map=new HashMap<>();
        AbstractFilter abstractFilter = filterClass.getDeclaredConstructor(Map.class).newInstance(map);

        abstractFilter.filter(null);
    }
}

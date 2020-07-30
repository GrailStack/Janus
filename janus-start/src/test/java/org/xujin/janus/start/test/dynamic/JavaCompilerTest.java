package org.xujin.janus.start.test.dynamic;

import org.xujin.janus.core.dynamic.JdkCompiler;
import org.xujin.janus.core.filter.filters.AbstractFilter;
import org.junit.Test;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

/**
 * @author: gan
 * @date: 2020/4/27
 */
public class JavaCompilerTest {
    @Test
    public void testCompile() throws Exception {
        java.net.URL url = Thread.currentThread().getContextClassLoader().getResource("filters/java/PrefixPathDemoFilter.java");
        File file = new File(url.getPath());
        JdkCompiler jdkCompiler = new JdkCompiler();
        Class<AbstractFilter> filterClass = (Class<AbstractFilter>) jdkCompiler.compile(file);
        Map<String, Object> map = new HashMap<>();
        AbstractFilter abstractFilter = filterClass.getDeclaredConstructor(Map.class).newInstance(map);

        abstractFilter.filter(null);
    }
}

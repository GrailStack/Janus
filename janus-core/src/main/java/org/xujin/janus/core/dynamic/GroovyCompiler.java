package org.xujin.janus.core.dynamic;

import groovy.lang.GroovyClassLoader;

import java.io.File;

/**
 * @author: gan
 * @date: 2020/4/27
 */
public class GroovyCompiler implements DynamicCodeCompiler {

    /**
     * @return a new GroovyClassLoader
     */
    GroovyClassLoader getGroovyClassLoader() {
        return new GroovyClassLoader();
    }

    /**
     * compile groovy source code
     *
     * @param file
     * @return
     * @throws Exception
     */
    @Override
    public Class<?> compile(File file) throws Exception {
        GroovyClassLoader loader = getGroovyClassLoader();
        Class<?> groovyClass = loader.parseClass(file);
        return groovyClass;
    }
}

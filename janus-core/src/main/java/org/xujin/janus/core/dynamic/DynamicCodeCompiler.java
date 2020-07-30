package org.xujin.janus.core.dynamic;

import java.io.File;

/**
 * @author: gan
 * @date: 2020/4/27
 */
public interface DynamicCodeCompiler {
    /**
     * dynamic compile java/groovy file,get a class
     *
     * @param file
     * @return
     * @throws Exception
     */
    Class<?> compile(File file) throws Exception;
}

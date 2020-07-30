package org.xujin.janus.config.app;

import java.io.File;
import java.io.FilenameFilter;

/**
 * @author: gan
 * @date: 2020/4/28
 */
public class DynamicClassConfig {
    private String filtersPath;
    private String predicatesPath;

    private int pollingIntervalSeconds;
    private int compileFileThreads;
    private int compileFileThreadTimeOut;

    public String getFiltersPath() {
        return filtersPath;
    }

    public void setFiltersPath(String filtersPath) {
        this.filtersPath = filtersPath;
    }

    public String getPredicatesPath() {
        return predicatesPath;
    }

    public void setPredicatesPath(String predicatesPath) {
        this.predicatesPath = predicatesPath;
    }

    public int getPollingIntervalSeconds() {
        return pollingIntervalSeconds;
    }

    public void setPollingIntervalSeconds(int pollingIntervalSeconds) {
        this.pollingIntervalSeconds = pollingIntervalSeconds;
    }

    public int getCompileFileThreads() {
        return compileFileThreads;
    }

    public int getCompileFileThreadTimeOut() {
        return compileFileThreadTimeOut;
    }

    public void setCompileFileThreadTimeOut(int compileFileThreadTimeOut) {
        this.compileFileThreadTimeOut = compileFileThreadTimeOut;
    }

    public void setCompileFileThreads(int compileFileThreads) {
        this.compileFileThreads = compileFileThreads;
    }

    public FilenameFilter getFilenameFilter() {
        return new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                return name.endsWith(getJavaExtendName()) || name.endsWith(getGroovyExtendName());
            }
        };
    }

    public String getJavaExtendName() {
        return ".java";
    }

    public String getGroovyExtendName() {
        return ".groovy";
    }

}

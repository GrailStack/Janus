package org.xujin.janus.core.dynamic;


import org.xujin.janus.config.ConfigRepo;
import org.xujin.janus.config.app.DynamicClassConfig;
import org.xujin.janus.config.util.CheckUtils;
import org.xujin.janus.core.filter.Filter;
import org.xujin.janus.core.filter.FilterRepo;
import org.xujin.janus.core.predicates.PredicateRepo;
import org.xujin.janus.core.util.ThreadFactoryHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Predicate;

/**
 * This class manages the directory polling for changes and new Groovy/java filters/predicates.
 * Polling interval and directories are specified in the initialization of the class, and a poller will check
 * for changes and additions.
 *
 * @author: gan
 * @date: 2020/4/28
 */
public class DynamicFileManger {
    private static final Logger logger = LoggerFactory.getLogger(DynamicFileManger.class);
    private static AtomicBoolean bRunning = new AtomicBoolean();
    private static DynamicClassConfig dynamicClassConfig;
    private static final ConcurrentMap<String, Long> fileLastModified = new ConcurrentHashMap<>();
    private static ExecutorService pollerExecutor;
    private static ExecutorService compileFileExecutor;
    private static final String JAVA_EXTEND = ".java";
    private static final String GROOVY_EXTEND = ".groovy";

    private DynamicFileManger() {
    }

    private static void init() {
        bRunning.set(true);
        dynamicClassConfig = ConfigRepo.getServerConfig().getDynamicClass();
        checkDynamicClassConfig(dynamicClassConfig);
        pollerExecutor = new ThreadPoolExecutor(1, 1,
                0L, TimeUnit.MILLISECONDS,
                new LinkedBlockingQueue<Runnable>(),
                ThreadFactoryHelper.create("DynamicFilePollerThread", false));
        compileFileExecutor = new ThreadPoolExecutor(dynamicClassConfig.getCompileFileThreads(),
                dynamicClassConfig.getCompileFileThreads(),
                0L, TimeUnit.MILLISECONDS,
                new LinkedBlockingQueue<Runnable>(),
                ThreadFactoryHelper.create("DynamicFileProcessThread", false));
    }

    private static void checkDynamicClassConfig(DynamicClassConfig dynamicClassConfig) {
        CheckUtils.checkNotNull(dynamicClassConfig, "please config dynamic class file ");
        CheckUtils.checkNonEmpty(dynamicClassConfig.getFiltersPath(),
                "filtersPath and predicatesPath in dynamicClass config cannot be null ");
        CheckUtils.checkParam(dynamicClassConfig.getCompileFileThreads() > 0,
                "processFileThreads  in dynamicClass config cannot less than one ");
        CheckUtils.checkParam(dynamicClassConfig.getPollingIntervalSeconds() > 0,
                "pollingIntervalSeconds  in dynamicClass config cannot less than one ");
        CheckUtils.checkParam(dynamicClassConfig.getCompileFileThreadTimeOut() > 0,
                "processFileThreadTimeOut  in dynamicClass config cannot less than one ");
    }

    public static void stopPoller() {
        bRunning.set(false);
    }

    public static void startPoller() {
        init();
        doPoll();
        pollerExecutor.submit(() -> {
            while (bRunning.get()) {
                try {
                    TimeUnit.MILLISECONDS.sleep(dynamicClassConfig.getPollingIntervalSeconds() * 1000);
                    doPoll();
                } catch (InterruptedException e) {
                    logger.error("poll dynamic file interrupted", e);
                }
            }
        });
    }

    public static void doPoll() {
        logger.info("start poll file.......");
        List<Callable<Boolean>> tasks = new ArrayList<>();
        try {
            List<File> aFiles = getFiles();
            for (File file : aFiles) {
                tasks.add(() -> {
                    String fileName = file.getAbsolutePath();
                    if (fileLastModified.get(fileName) != null && fileLastModified.get(fileName) == file.lastModified()) {
                        logger.info(fileName + "  not modified.......");
                        //not modified
                        return false;
                    }
                    logger.info("begin process new file,name:" + fileName);
                    if (file.getName().endsWith(GROOVY_EXTEND)) {
                        compileGroovyFile(file);
                    } else if (file.getName().endsWith(JAVA_EXTEND)) {
                        compileJavaFile(file);
                    } else {
                        logger.error(fileName + ", extend name not supported");
                        return false;
                    }
                    fileLastModified.put(fileName, file.lastModified());
                    return true;
                });
            }
            compileFileExecutor.invokeAll(tasks, dynamicClassConfig.getCompileFileThreadTimeOut(), TimeUnit.SECONDS);
        } catch (Exception e) {
            String msg = "Error updating groovy/java filters from disk!";
            logger.error(msg, e);
        }
    }

    /**
     * compile source code and register to registry
     */
    private static void compileGroovyFile(File file) throws Exception {
        GroovyCompiler groovyCompiler = new GroovyCompiler();
        Class<?> compiledClass;
        try {
            compiledClass = groovyCompiler.compile(file);
        } catch (Exception ex) {
            logger.error("compile groovy file error,cause:" + ex.getMessage(), ex);
            return;
        }
        register(compiledClass);

    }

    private static void compileJavaFile(File file) {
        JdkCompiler jdkCompiler = new JdkCompiler();
        Class<?> compiledClass;
        try {
            compiledClass = jdkCompiler.compile(file);
        } catch (Exception ex) {
            logger.error("compile java file error,cause:" + ex.getMessage(), ex);
            return;
        }
        register(compiledClass);
    }

    private static void register(Class<?> compiledClass) {
        if (Filter.class.isAssignableFrom(compiledClass)) {
            try {
                Filter filter = (Filter) compiledClass.newInstance();
                FilterRepo.addOrUpdate(filter);
            } catch (Exception ex) {
                logger.error("new filter instance error", ex);
            }
        }
        if (Predicate.class.isAssignableFrom(compiledClass)) {
            try {
                Predicate predicate = (Predicate) compiledClass.newInstance();
                PredicateRepo.addOrUpdate(predicate);
            } catch (Exception ex) {
                logger.error("new predicate instance error", ex);
            }
        }
    }

    /**
     * Returns a List<File> of all Files from all polled directories
     *
     * @return
     */
    private static List<File> getFiles() {
        List<File> list = new ArrayList<File>();
        String[] directories = new String[2];
        directories[0] = dynamicClassConfig.getFiltersPath();
        directories[1] = dynamicClassConfig.getPredicatesPath();
        for (String sDirectory : directories) {
            if (sDirectory == null) {
                logger.info("no filter/predicate path found in dynamicClass config");
                continue;
            }
            File directory = getDirectory(sDirectory);
            if (directory == null) {
                return list;
            }
            File[] aFiles = directory.listFiles(dynamicClassConfig.getFilenameFilter());
            if (aFiles != null) {
                list.addAll(Arrays.asList(aFiles));
            } else {
                logger.info("no file found in " + sDirectory);
            }
        }
        return list;
    }

    /**
     * Returns the directory File for a path. A Runtime Exception is thrown if the directory is in valid.
     *
     * @param sPath
     * @return a File representing the directory path
     */
    private static File getDirectory(String sPath) {
        File directory = new File(sPath);
        if (directory.isDirectory()) {
            return directory;
        }
        URL resource = DynamicFileManger.class.getClassLoader().getResource(sPath);
        if (resource != null) {
            try {
                directory = new File(resource.toURI());
            } catch (Exception e) {
                throw new RuntimeException("Error accessing directory in classloader. path=" + sPath, e);
            }
        }
        if (!directory.isDirectory()) {
            logger.warn(directory.getAbsolutePath() + " is not a exist directory!!!!");
            return null;
        }

        return directory;
    }

}

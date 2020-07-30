package org.xujin.janus.config.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Properties;

/**
 * @author: gan
 * @date: 2020/5/21
 */
public class EnvUtils {
    private static final Logger logger = LoggerFactory.getLogger(EnvUtils.class);
    private static final String SERVER_PROPERTIES_LINUX = "/opt/settings/server.properties";
    private static final String SERVER_PROPERTIES_WINDOWS = "C:/opt/settings/server.properties";
    private static final String DEFAULT_ENV = "dev";
    private static final String DEFAULT_CLUSTER = "default";
    private static String m_env;
    private static Properties m_serverProperties = new Properties();

    public static boolean isOSWindows() {
        String osName = System.getProperty("os.name");
        if (osName == null || osName.isEmpty()) {
            return false;
        }
        return osName.startsWith("Windows");
    }

    public static String getEnv() {
        return getEnvironmentConfig("env", DEFAULT_ENV);
    }

    public static String getCluster() {
        return getEnvironmentConfig("cluster", DEFAULT_CLUSTER);
    }

    /**
     * 默认设置为false 不本地加载配置启动
     * @return
     */
    public static String getLocal() {
        return getEnvironmentConfig("local", "false");
    }

    public static String getUserDir() {
        return System.getProperty("user.dir");
    }

    /**
     * @return dev fat uat  pro
     */
    private static String getEnvironmentConfig(String key, String defaultValue) {
        // 1. Try to get environment from JVM system property
        m_env = System.getProperty(key);
        if (m_env != null && !m_env.isEmpty()) {
            m_env = m_env.trim();
            logger.info("{} is set to [{}] by JVM system property '{}'.", key, m_env, key);
            return m_env;
        }

        // 2. Try to get environment from OS environment variable
        m_env = System.getenv(key.toUpperCase());
        if (m_env != null && !m_env.isEmpty()) {
            m_env = m_env.trim();
            logger.info("{} is set to [{}] by OS  variable '{}'.", key, m_env, key.toUpperCase());
            return m_env;
        }

        // 3. Try to get environment from file "server.properties"
        m_env = getEnvFromLocalProperties(key);
        if (m_env != null && !m_env.isEmpty()) {
            m_env = m_env.trim();
            logger.info("{} is set to [{}] by property '{}' in server.properties.", key, m_env, key);
            return m_env;
        }

        // 4. Set environment to default.
        m_env = defaultValue;
        logger.info("{} is set to default {}. Because it is not available in either (1) JVM system property '{}', (2) OS env variable '{}' nor (3) property '{}' from the properties InputStream.", key, defaultValue, key, key.toUpperCase(), key);
        return m_env;
    }

    private static String getEnvFromLocalProperties(String key) {

        String path = isOSWindows() ? SERVER_PROPERTIES_WINDOWS : SERVER_PROPERTIES_LINUX;
        m_serverProperties = ConfigFileUtils.readProperties(path);
        return m_serverProperties.getProperty(key);
    }
}

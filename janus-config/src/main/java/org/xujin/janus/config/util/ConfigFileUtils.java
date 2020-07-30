package org.xujin.janus.config.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yaml.snakeyaml.Yaml;

import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Properties;

/**
 * @author: gan
 * @date: 2020/5/21
 */
public class ConfigFileUtils {
    private static final Logger logger = LoggerFactory.getLogger(ConfigFileUtils.class);

    public static <T> T readYaml(String yamlFile, Class<T> tClass) {

        try {
            java.io.File file = new java.io.File(yamlFile);
            FileInputStream fis;
            if (file.exists() && file.canRead()) {
                fis = new FileInputStream(file);
            } else {
                java.net.URL url = Thread.currentThread().getContextClassLoader().getResource(yamlFile);
                if (url == null) {
                    throw new RuntimeException("cannot found yaml file,file name: " + yamlFile);
                }
                fis = new FileInputStream(url.getFile());
            }

            return new Yaml().loadAs(fis, tClass);
        } catch (Exception e) {
            throw new RuntimeException("load yaml exception,cause:"+e.getMessage(), e);
        }


    }

    public static Properties readProperties(String propertiesFile) {
        Properties properties = new Properties();
        try {
            java.io.File file = new java.io.File(propertiesFile);
            FileInputStream fis;
            if (file.exists() && file.canRead()) {
                fis = new FileInputStream(file);
            } else {
                java.net.URL url = Thread.currentThread().getContextClassLoader().getResource(propertiesFile);
                if (url == null) {
                    throw new RuntimeException("cannot found properties file,file name: " + propertiesFile);
                }
                fis = new FileInputStream(url.getFile());
            }
            if (fis != null) {
                try {
                    properties.load(new InputStreamReader(fis, StandardCharsets.UTF_8));
                } finally {
                    fis.close();
                }
            }

        } catch (Throwable ex) {
            logger.error("getEnvFromLocalProperties failed.", ex);
        }
        return properties;
    }
}

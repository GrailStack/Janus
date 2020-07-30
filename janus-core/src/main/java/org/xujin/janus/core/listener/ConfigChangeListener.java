package org.xujin.janus.core.listener;

import com.google.gson.Gson;
import org.xujin.janus.config.ConfigRepo;
import org.xujin.janus.config.observer.ConfigChangeObserver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;


/**
 * @author: gan
 * @date: 2020/5/26
 */
public class ConfigChangeListener {
    private static final Logger logger = LoggerFactory.getLogger(ConfigChangeListener.class);
    public static final String GLOBAL_FILTER_KEY = "globalFilters";
    public static final String VALUE_SEPARATE = ",";

    public static void listen() {
        ConfigChangeObserver.addListener(configChangeCmd -> {
            logger.info("receive config change ,content:" + new Gson().toJson(configChangeCmd));
            Map<String, String> changeItem = configChangeCmd.getChangedItem();
            if (changeItem == null) {
                return;
            }
            changeGlobalFilter(changeItem);
            /**
             * other config change process
             * changeXXXX(changeItem)
             */

        });
    }

    /**
     * 全局Filter配置变化处理
     *
     * @param changeItem
     */
    private static void changeGlobalFilter(Map<String, String> changeItem) {
        if (!changeItem.containsKey(GLOBAL_FILTER_KEY)) {
            return;
        }
        String changedValue = changeItem.get(GLOBAL_FILTER_KEY);
        if (changedValue == null) {
            return;
        }
        String[] globalFilters = changedValue.split(VALUE_SEPARATE);
        ConfigRepo.getServerConfig().setGlobalFilters(globalFilters);
    }
}

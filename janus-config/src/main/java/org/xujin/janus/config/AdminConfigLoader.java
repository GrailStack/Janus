package org.xujin.janus.config;


import com.google.gson.Gson;
import org.xujin.janus.client.co.AllConfigCO;
import org.xujin.janus.config.admin.client.AdminRequests;
import org.xujin.janus.config.util.CheckUtils;

/**
 * @author: gan
 * @date: 2020/5/20
 */

public class AdminConfigLoader {
    public static void load() {
        try {
            AllConfigCO allConfigCO = AdminRequests.instance().pullAllConfig();
            CheckUtils.checkNotNull(allConfigCO,"pull config from admin response is null");
            ServerConfig serverConfig = new Gson().fromJson(allConfigCO.getConfigJson(), ServerConfig.class);
            ConfigRepo.init(serverConfig);

        } catch (Exception ex) {
            throw new RuntimeException("pull config from admin error,message:"+ex.getMessage(), ex);
        }
    }
}

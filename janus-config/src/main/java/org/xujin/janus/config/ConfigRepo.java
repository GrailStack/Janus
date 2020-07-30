package org.xujin.janus.config;

import org.xujin.janus.config.util.CheckUtils;

/**
 * @author: gan
 * @date: 2020/5/20
 */
public class ConfigRepo {
    private static ServerConfig serverConfig;

    public static void init(ServerConfig serverConfigAdd) {
        CheckUtils.checkNotNull(serverConfigAdd, "server config cannot be null");
        serverConfigAdd.checkMember();
        serverConfig = serverConfigAdd;
    }

    public static ServerConfig getServerConfig() {
        CheckUtils.checkNotNull(serverConfig, "server config not init");
        return serverConfig;
    }
}

package org.xujin.janus.config.admin.processer;

import org.xujin.janus.client.cmo.ConfigChangeCmd;
import org.xujin.janus.config.observer.ConfigChangeObserver;
import org.xujin.janus.damon.exchange.JanusCmdMsg;

/**
 * Janus Server提供给Admin的配置下发接口
 * @author xujin
 */
public class ConfigChangeProcessor extends AbstractProcessor {
    @Override
    public JanusCmdMsg doExecute(Object payload) {
        if (payload == null) {
            throw new RuntimeException("payload cannot be null");
        }
        ConfigChangeCmd configChangeCmd = (ConfigChangeCmd) payload;
        ConfigChangeObserver.notifyListeners(configChangeCmd);
        return successResponse();
    }
}

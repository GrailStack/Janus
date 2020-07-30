package org.xujin.janus.daemon.admin.processor;

import org.xujin.janus.client.cmo.ChangeOnlineCmd;
import org.xujin.janus.config.admin.processer.AbstractProcessor;
import org.xujin.janus.damon.exchange.JanusCmdMsg;

/**
 * 在线摘掉网关Server的处理器
 *
 * @author xujin
 */
public class OnlineProcessor extends AbstractProcessor {

    public volatile static boolean online = false;

    @Override
    public JanusCmdMsg doExecute(Object payload) {
        if (payload == null) {
            return errorResponse("payload cannot be null");
        }
        ChangeOnlineCmd cmd = (ChangeOnlineCmd) payload;
        if (cmd.isOnline()) {
            online = true;
        } else {
            online = false;
        }
        return successResponse();

    }


}

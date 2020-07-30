package org.xujin.janus.config.admin.client;

import org.xujin.janus.client.cmo.CallBackCmd;
import org.xujin.janus.client.cmo.QueryAllConfigCmd;
import org.xujin.janus.client.co.AllConfigCO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * @author: gan
 * @date: 2020/5/21
 */
public class AdminRequests extends AbstractRequest {
    private static Logger logger = LoggerFactory.getLogger(AdminRequests.class);

    private static class InstanceHolder {
        private static final AdminRequests INSTANCE = new AdminRequests();
    }

    private AdminRequests() {

    }

    public static AdminRequests instance() {
        return InstanceHolder.INSTANCE;
    }

    /**
     * 从服务端拉取全部配置
     */
    public AllConfigCO pullAllConfig() throws Exception {
        try {
            QueryAllConfigCmd queryAllConfigCmd = new QueryAllConfigCmd();
            return (AllConfigCO) syncSend(QueryAllConfigCmd.method, queryAllConfigCmd);
        } catch (Exception e) {
            logger.error("janus server pull all config from admin fail:{}", e.getMessage());
            return null;
        }

    }

    public void sendCallBack(String requestID, String requestMethod, int processResult, String errorMessage) {
        CallBackCmd callBackCmd = new CallBackCmd();
        callBackCmd.setRequestID(requestID);
        callBackCmd.setRequestMethod(requestMethod);
        callBackCmd.setProcessResult(processResult);
        callBackCmd.setErrorMessage(errorMessage);
        try {
            asyncSend(CallBackCmd.method, null, callBackCmd);
        } catch (Exception e) {
            logger.error("send call back to admin fail:{}", e.getMessage());
        }
    }
}

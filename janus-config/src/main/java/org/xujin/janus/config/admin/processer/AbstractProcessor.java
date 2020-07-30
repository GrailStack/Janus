package org.xujin.janus.config.admin.processer;

import org.xujin.janus.config.ApplicationConfig;
import org.xujin.janus.config.admin.client.AdminRequests;
import org.xujin.janus.config.util.CheckUtils;
import org.xujin.janus.damon.exchange.JanusCmdMsg;
import org.xujin.janus.damon.processer.ICmdMsgProcessor;
import io.netty.handler.codec.http.HttpResponseStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author: gan
 * @date: 2020/5/25
 */
public abstract class AbstractProcessor implements ICmdMsgProcessor {
    private static Logger logger = LoggerFactory.getLogger(AbstractProcessor.class);

    @Override
    public JanusCmdMsg execute(JanusCmdMsg msg) {
        try {
            CheckUtils.checkNotNull(msg, "janusMsg cannot be null");
            CheckUtils.checkNotNull(msg.getCluster(), "janusMsg cluster cannot be null");
            String errorMsg = String.format("current project cluster is:%s,receive cluster is:%s,not matched"
                    , ApplicationConfig.getCluster(), msg.getCluster());
            CheckUtils.checkParam(msg.getCluster().equalsIgnoreCase(ApplicationConfig.getCluster()), errorMsg);
            JanusCmdMsg resultMsg = doExecute(msg.getPayload());
            //异步接口，给admin发送处理结果
            if (msg.isNeedCallBack()) {
                AdminRequests.instance().sendCallBack(msg.getRequestID(), msg.getMethod(), 0, null);
            }
            return resultMsg;
        } catch (Exception ex) {
            logger.error("processor doExecute throw exception", ex);
            if (msg.isNeedCallBack()) {
                AdminRequests.instance().sendCallBack(msg.getRequestID(), msg.getMethod(), 1, ex.getMessage());
            }
            return errorResponse(ex.getMessage());
        }
    }

    public abstract JanusCmdMsg doExecute(Object payload);

    public JanusCmdMsg errorResponse(String errorMessage) {
        return JanusCmdMsg.builder()
                .code(HttpResponseStatus.INTERNAL_SERVER_ERROR.code())
                .message(errorMessage)
                .cluster(ApplicationConfig.getCluster())
                .version(ApplicationConfig.getApplicationVersion())
                .buildFailResponse();
    }

    public JanusCmdMsg successResponse() {
        return JanusCmdMsg.builder()
                .cluster(ApplicationConfig.getCluster())
                .version(ApplicationConfig.getApplicationVersion())
                .buildSuccessResponse();
    }
}

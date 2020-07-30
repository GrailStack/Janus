package org.xujin.janus.daemon.admin.client;

import org.xujin.janus.client.cmo.AlarmCmd;
import org.xujin.janus.client.cmo.HeartBeatCmd;
import org.xujin.janus.config.ApplicationConfig;
import org.xujin.janus.config.admin.client.AbstractRequest;
import org.xujin.janus.damon.utils.IpUtil;
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

    public void sendHeartBeatMsg() {
        try {
            HeartBeatCmd heartBeatCmd = new HeartBeatCmd();
            heartBeatCmd.setHost(getServerIpPort());
            asyncSend(HeartBeatCmd.method,null, heartBeatCmd);
        } catch (Exception e) {
            logger.error("janus server send heartBeat msg fail:{}", e.getMessage());
        }
    }

    public void sendAlarm(String alarmInfo) {
        try {
            AlarmCmd alarmCmd = new AlarmCmd();
            alarmCmd.setHost(getServerIpPort());
            alarmCmd.setAlarmInfo(alarmInfo);
            asyncSend(AlarmCmd.method,null, alarmCmd);
        } catch (Exception e) {
            logger.error("janus server send alarm msg fail:{}", e.getMessage());
        }
    }

    public String getServerIpPort() {
        String ip = IpUtil.getIp();
        int applicationPort = ApplicationConfig.getApplicationPort();
        return ip + ":" + applicationPort;
    }

}

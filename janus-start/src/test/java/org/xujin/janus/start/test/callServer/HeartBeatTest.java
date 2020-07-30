package org.xujin.janus.start.test.callServer;


import org.xujin.janus.daemon.admin.client.AdminRequests;
import org.xujin.janus.startup.JanusBootStrap;
import org.junit.Before;
import org.junit.Test;

/**
 * @author: gan
 * @date: 2020/5/22
 */
public class HeartBeatTest {
    @Before
    public void init(){
        JanusBootStrap.initGateway();
    }
    @Test
    public void testSendHeartBeat() {
        AdminRequests.instance().sendHeartBeatMsg();
    }

    @Test
    public void testSendAlarm() {
        AdminRequests.instance().sendAlarm("告警信息");
    }
}

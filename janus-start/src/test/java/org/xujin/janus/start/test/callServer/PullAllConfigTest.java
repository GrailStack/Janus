package org.xujin.janus.start.test.callServer;


import com.google.gson.Gson;
import org.xujin.janus.client.co.AllConfigCO;
import org.xujin.janus.config.ServerConfig;
import org.xujin.janus.config.admin.client.AdminRequests;
import org.xujin.janus.startup.JanusBootStrap;
import org.junit.Before;
import org.junit.Test;

/**
 * @author: gan
 * @date: 2020/5/22
 */
public class PullAllConfigTest {
    @Before
    public void init() {
        JanusBootStrap.initGateway();
    }

    @Test
    public void testPullAllConfig() throws Exception {
        AllConfigCO janusCmdMsg = AdminRequests.instance().pullAllConfig();
        System.out.println("response:");
        System.out.println(new Gson().toJson(janusCmdMsg));
        ServerConfig serverConfig=new Gson().fromJson(janusCmdMsg.getConfigJson(),ServerConfig.class);
    }
    @Test
    public void testMemoryLeak() throws Exception {
        for(int i=0;i<100000;i++){
            AllConfigCO janusCmdMsg = AdminRequests.instance().pullAllConfig();
        }
    }
}

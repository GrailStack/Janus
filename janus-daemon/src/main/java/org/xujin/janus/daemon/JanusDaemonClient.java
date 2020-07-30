package org.xujin.janus.daemon;

import org.xujin.janus.daemon.admin.client.AdminRequests;
import org.xujin.janus.daemon.handler.JanusWriteIdleHandler;
import org.xujin.janus.damon.client.ClientIdleHandlerPool;

/**
 * DaemonClient的设置
 * @author xujin
 */
public class JanusDaemonClient {

    public static  void setDaemonClient(){

        ClientIdleHandlerPool.getSingleton().setWriteIdleHandler(new JanusWriteIdleHandler());
        //设置写超时时间
        ClientIdleHandlerPool.getSingleton().setWriteIdleTime(30*1000);
    }

    public static void pingJanusAdmin(){
        AdminRequests.instance().sendHeartBeatMsg();
    }

}

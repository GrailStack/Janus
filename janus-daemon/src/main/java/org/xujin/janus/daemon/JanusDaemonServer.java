package org.xujin.janus.daemon;

import com.google.gson.Gson;
import org.xujin.janus.client.cmo.ChangeOnlineCmd;
import org.xujin.janus.client.cmo.ConfigChangeCmd;
import org.xujin.janus.client.cmo.RouteChangeCmd;
import org.xujin.janus.client.cmo.SendFileCmd;
import org.xujin.janus.config.admin.processer.ConfigChangeProcessor;
import org.xujin.janus.config.admin.processer.RouteChangeProcessor;
import org.xujin.janus.config.admin.processer.SendFileProcessor;
import org.xujin.janus.daemon.admin.processor.OnlineProcessor;
import org.xujin.janus.damon.JanusCmdServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * @author xujin
 */
public class JanusDaemonServer {
    private static Logger logger = LoggerFactory.getLogger(JanusDaemonServer.class);

    public static void start(int port) {
        logger.info("start janus damon server ");
        JanusCmdServer janusCmdServer = new JanusCmdServer();
        Map janusServerProcessorMap = new HashMap(4);
        logger.info("init processor map");
        janusServerProcessorMap.put(SendFileCmd.method, new SendFileProcessor());
        janusServerProcessorMap.put(ChangeOnlineCmd.method, new OnlineProcessor());
        janusServerProcessorMap.put(ConfigChangeCmd.method, new ConfigChangeProcessor());
        janusServerProcessorMap.put(RouteChangeCmd.method, new RouteChangeProcessor());
        logger.info("damon server port:{},processorMap:{}", port, new Gson().toJson(janusServerProcessorMap));
        janusCmdServer.init(port, janusServerProcessorMap);
        janusCmdServer.start();
        logger.info("janus  damon server end!");
    }


}

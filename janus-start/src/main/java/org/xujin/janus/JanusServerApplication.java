package org.xujin.janus;

import org.xujin.janus.config.ApplicationConfig;
import org.xujin.janus.core.netty.NettyServer;
import org.xujin.janus.daemon.JanusDaemonClient;
import org.xujin.janus.daemon.JanusDaemonServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xujin.janus.startup.JanusBootStrap;

import java.net.MalformedURLException;

/**
 * 网关Server的启动入口
 *
 * @author xujin
 */
public class JanusServerApplication {

    private static Logger logger = LoggerFactory.getLogger(JanusServerApplication.class);

    public static void main(String[] args) throws MalformedURLException {

        logger.info("Gateway Server Application Start...");

        // 初始化网关Filter和配置
        logger.info("init Gateway Server ...");
        JanusBootStrap.initGateway();

        NettyServer nettyServer = new NettyServer();
        // 启动HTTP容器
        int httpPort = ApplicationConfig.getApplicationPort();
        logger.info("start netty  Server...,port:{}", httpPort);
        nettyServer.start(httpPort);

        //启动网关的Damon Server
        int daemonPort = httpPort + 1;
        logger.info("start daemon Server...,port:{}", daemonPort);
        JanusDaemonServer.start(daemonPort);

        //设置DaemonClient配置
        logger.info("start set daemonClient config...");
        JanusDaemonClient.setDaemonClient();
        logger.info("start First Janus Server Ping Admin...");
        JanusDaemonClient.pingJanusAdmin();
    }

    public static void fail(String message) {
        logger.error("application start up error: " + message);
        System.exit(-1);
    }

}

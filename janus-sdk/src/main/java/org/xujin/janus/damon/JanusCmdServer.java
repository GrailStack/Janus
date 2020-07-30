package org.xujin.janus.damon;

import org.xujin.janus.damon.processer.ICmdMsgProcessor;
import org.xujin.janus.damon.idle.IIdleHandler;
import org.xujin.janus.damon.server.NettyServer;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;

@Slf4j
public class JanusCmdServer {
    private int port;
    private Server server;

    private int readIdleTime = 60000;
    private IIdleHandler readIdleHandler;

    private int writeIdleTime = 0;
    private IIdleHandler writeIdleHandler;

    private int allIdleTime = 0;
    private IIdleHandler allIdleHandler;

    private Map<String, ICmdMsgProcessor> processerMap = new HashMap<>();

    public void init(int port, Map processerMap) {
        this.port = port;
        this.processerMap = processerMap;
    }

    public void setReadIdle(int readIdleTime, IIdleHandler readIdleHandler){
        this.readIdleTime = readIdleTime;
        this.readIdleHandler = readIdleHandler;
    }

    public void setWriteIdle(int writeIdleTime, IIdleHandler writeIdleHandler){
        this.writeIdleTime = writeIdleTime;
        this.writeIdleHandler = writeIdleHandler;
    }

    public void setAllIdle(int allIdleTime, IIdleHandler allIdleHandler){
        this.allIdleTime = allIdleTime;
        this.allIdleHandler = allIdleHandler;
    }

    public void start() {
        server = new NettyServer();
        try {
            server.start(port, processerMap, readIdleTime, readIdleHandler, writeIdleTime, writeIdleHandler, allIdleTime, allIdleHandler);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            System.exit(-1);
        }

    }

}

package org.xujin.janus.damon;

import org.xujin.janus.damon.idle.IIdleHandler;

import java.util.Map;

public interface Server {

	public void start(int port, Map processerMap, int readIdleTime, IIdleHandler readIdleHandler, int writeIdleTime, IIdleHandler writeIdleHandler, int allIdleTime, IIdleHandler allIdleHandler) throws Exception;

	public void stop() throws Exception;

}

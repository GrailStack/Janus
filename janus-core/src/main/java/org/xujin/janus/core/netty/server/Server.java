package org.xujin.janus.core.netty.server;

import lombok.extern.slf4j.Slf4j;

/**
 * server
 *
 * @author tbkk 2019-11-24 20:59:49
 */

@Slf4j
public abstract class Server {


	private BaseCallback startedCallback;
	private BaseCallback stoppedCallback;

	public void setStartedCallback(BaseCallback startedCallback) {
		this.startedCallback = startedCallback;
	}

	public void setStoppedCallback(BaseCallback stoppedCallback) {
		this.stoppedCallback = stoppedCallback;
	}


	/**
	 * start server
	 *
	 * @throws Exception
	 */
	public abstract void start(int port) throws Exception;

	/**
	 * callback when started
	 */
	public void startCallBack() {
		if (startedCallback != null) {
			try {
				startedCallback.run();
			} catch (Exception e) {
				log.error("server startedCallback error.", e);
			}
		}
	}

	/**
	 * stop server
	 *
	 * @throws Exception
	 */
	public abstract void stop() throws Exception;

	/**
	 * callback when stoped
	 */
	public void stopCallBack() {
		if (stoppedCallback != null) {
			try {
				stoppedCallback.run();
			} catch (Exception e) {
				log.error("janus server stopped callback error.", e);
			}
		}
	}

}

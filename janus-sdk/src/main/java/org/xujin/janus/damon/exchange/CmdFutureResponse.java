package org.xujin.janus.damon.exchange;


import org.xujin.janus.damon.exception.JanusCmdException;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;


public class CmdFutureResponse implements Future<NettyMsg> {

	private NettyMsg request;
	private NettyMsg response;


	private boolean done = false;
	private Object lock = new Object();


	public CmdFutureResponse(NettyMsg request) {
		this.request = request;
	}


	// ---------------------- for invoke back ----------------------

	public void setResponseAndNotify(NettyMsg response) {
		this.response = response;
		synchronized (lock) {
			done = true;
			lock.notifyAll();
		}
	}


	// ---------------------- for invoke ----------------------

	@Override
	public boolean cancel(boolean mayInterruptIfRunning) {
		// TODO
		return false;
	}

	@Override
	public boolean isCancelled() {
		// TODO
		return false;
	}

	@Override
	public boolean isDone() {
		return done;
	}

	@Override
	public NettyMsg get() throws InterruptedException, ExecutionException {
		try {
			return get(-1, TimeUnit.MILLISECONDS);
		} catch (TimeoutException e) {
			throw new JanusCmdException(e);
		}
	}

	@Override
	public NettyMsg get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
		if (!done) {
			synchronized (lock) {
				try {
					if (timeout < 0) {
						lock.wait();
					} else {
						long timeoutMillis = (TimeUnit.MILLISECONDS==unit)?timeout:TimeUnit.MILLISECONDS.convert(timeout , unit);
						lock.wait(timeoutMillis);
					}
				} catch (InterruptedException e) {
					throw e;
				}
			}
		}

		if (!done) {
			throw new JanusCmdException("janus-cmd, request timeout at:"+ System.currentTimeMillis() +", request:" + request.toString());
		}
		return response;
	}


}

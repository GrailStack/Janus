package org.xujin.janus.damon;


import org.xujin.janus.damon.exchange.JanusCmdMsg;

public interface Client {

	public void sendMsg(String address, JanusCmdMsg janusCmdMsg) throws Exception;

}

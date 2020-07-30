package org.xujin.janus.damon.processer;


import org.xujin.janus.damon.exchange.JanusCmdMsg;

public interface ICmdMsgProcessor {
    JanusCmdMsg execute(JanusCmdMsg msg);
}

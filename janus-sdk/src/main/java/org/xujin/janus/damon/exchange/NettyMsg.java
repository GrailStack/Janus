package org.xujin.janus.damon.exchange;

import lombok.Data;

import java.io.Serializable;

@Data
public class NettyMsg implements Serializable {
    private static final long serialVersionUID = 1L;
    private String tranceId = "";
    private Boolean sync = true;//是否同步发送
    private JanusCmdMsg cmdMsg;
}

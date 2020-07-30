package org.xujin.janus.config.netty;

import java.io.Serializable;


public class NettyClientConfig implements Serializable {
    private boolean keepalive ;
    private boolean reUseAddr ;
    private boolean tcpNoDelay ;
    private int backLog ;
    private  int  sndBuf ;
    private int revBuf ;
    private int heart ;
    private int workThread;


    public boolean isKeepalive() {
        return keepalive;
    }

    public void setKeepalive(boolean keepalive) {
        this.keepalive = keepalive;
    }

    public boolean isReUseAddr() {
        return reUseAddr;
    }

    public void setReUseAddr(boolean reUseAddr) {
        this.reUseAddr = reUseAddr;
    }

    public boolean isTcpNoDelay() {
        return tcpNoDelay;
    }

    public void setTcpNoDelay(boolean tcpNoDelay) {
        this.tcpNoDelay = tcpNoDelay;
    }

    public int getBackLog() {
        return backLog;
    }

    public void setBackLog(int backLog) {
        this.backLog = backLog;
    }

    public int getSndBuf() {
        return sndBuf;
    }

    public void setSndBuf(int sndBuf) {
        this.sndBuf = sndBuf;
    }

    public int getRevBuf() {
        return revBuf;
    }

    public void setRevBuf(int revBuf) {
        this.revBuf = revBuf;
    }

    public int getHeart() {
        return heart;
    }

    public void setHeart(int heart) {
        this.heart = heart;
    }

    public int getWorkThread() {
        return workThread;
    }

    public void setWorkThread(int workThread) {
        this.workThread = workThread;
    }
}

package org.xujin.janus.config.netty;

import java.io.Serializable;


public class NettyServerConfig implements Serializable {

    private boolean keepalive;
    private boolean reUseAddr;
    private boolean tcpNoDelay;
    private int backLog = 1024;
    private int sndBuf = 10485760;
    private int revBuf = 10485760;
    private int heart = 180;
    private int bossThread = 2;
    private int workThread = 4;

    private int maxInitialLineLength = 8192;
    private int maxHeaderSize = 8192 * 2;
    private int maxChunkSize = 8192 * 2;
    private int maxRequestBufferSizeInBytes = 0;
    private int maxHttpLength = 65535;


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

    public int getBossThread() {
        return bossThread;
    }

    public void setBossThread(int bossThread) {
        this.bossThread = bossThread;
    }

    public int getWorkThread() {
        return workThread;
    }

    public void setWorkThread(int workThread) {
        this.workThread = workThread;
    }

    public int getMaxInitialLineLength() {
        return maxInitialLineLength;
    }

    public void setMaxInitialLineLength(int maxInitialLineLength) {
        this.maxInitialLineLength = maxInitialLineLength;
    }

    public int getMaxHeaderSize() {
        return maxHeaderSize;
    }

    public void setMaxHeaderSize(int maxHeaderSize) {
        this.maxHeaderSize = maxHeaderSize;
    }

    public int getMaxChunkSize() {
        return maxChunkSize;
    }

    public void setMaxChunkSize(int maxChunkSize) {
        this.maxChunkSize = maxChunkSize;
    }

    public int getMaxRequestBufferSizeInBytes() {
        return maxRequestBufferSizeInBytes;
    }

    public void setMaxRequestBufferSizeInBytes(int maxRequestBufferSizeInBytes) {
        this.maxRequestBufferSizeInBytes = maxRequestBufferSizeInBytes;
    }

    public int getMaxHttpLength() {
        return maxHttpLength;
    }

    public void setMaxHttpLength(int maxHttpLength) {
        this.maxHttpLength = maxHttpLength;
    }
}

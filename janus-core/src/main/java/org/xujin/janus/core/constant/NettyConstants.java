package org.xujin.janus.core.constant;

public class NettyConstants {

    public static final int MAX_REQUEST_SIZE = 500 * 1024 * 1024;
    public static final int MAX_RESPONSE_SIZE = 10 * 1024 * 1024;
    public static final int MAX_INITIAL_LINE_LENGTH = 4096;
    public static final int MAX_HEADER_SIZE = 16 * 1024;
    public static final int MAX_CHUNK_SIZE = 16 * 1024;
    //用于网关通过Channel转发请求调用源服务之后,异步返回结果从同一个channel中返回
    public static final String CHANNEL_CTX_KEY = "_JANUS_CTX_";

}

package org.xujin.janus.damon.exchange;

import lombok.Getter;
import lombok.ToString;

import java.io.Serializable;

/**
 *
 */
@Getter
@ToString
public class JanusCmdMsg implements Serializable {

    private static final long serialVersionUID = 1L;
    /**
     * 集群编码
     */
    private String cluster;
    /**
     * Janus Server的版本
     */

    private String version;

    /**
     * 异步的时候需要传
     */
    private String requestID;

    private String method;

    private boolean needCallBack;

    private int code;

    private String message;

    private Object payload;


    private JanusCmdMsg(String cluster, String version, String method, String requestID, boolean needCallBack, Object payload) {
        this.cluster = cluster;
        this.version = version;
        this.method = method;
        this.payload = payload;
        this.requestID = requestID;
        this.needCallBack = needCallBack;
    }

    private JanusCmdMsg(String cluster, String version, Object payload, int code, String message) {
        this.cluster = cluster;
        this.version = version;
        this.code = code;
        this.message = message;
        this.payload = payload;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String cluster;
        private String method;
        private String version;
        private String requestID;
        private Object payload;
        private boolean needCallBack;
        private int code;
        private String message;

        public Builder cluster(String cluster) {
            this.cluster = cluster;
            return this;
        }

        public Builder method(String method) {
            this.method = method;
            return this;
        }

        public Builder version(String version) {
            this.version = version;
            return this;
        }

        public Builder code(int code) {
            this.code = code;
            return this;
        }

        public Builder message(String message) {
            this.message = message;
            return this;
        }

        public Builder requestID(String requestID) {
            this.requestID = requestID;
            return this;
        }

        public Builder needCallBack(boolean needCallBack) {
            this.needCallBack = needCallBack;
            return this;
        }

        public Builder payload(Object payload) {
            this.payload = payload;
            return this;
        }

        private void baseCheck() {
            if (this.cluster == null || this.cluster.isEmpty()) {
                throw new IllegalArgumentException("cluster cannot be empty");
            }
            if (this.version == null || this.version.isEmpty()) {
                throw new IllegalArgumentException("version cannot be empty");
            }
        }

        public JanusCmdMsg buildRequest() {
            baseCheck();
            if (this.method == null) {
                throw new IllegalArgumentException("method cannot be null");
            }

            JanusCmdMsg janusCmdMsg = new JanusCmdMsg(this.cluster, this.version, this.method, this.requestID, this.needCallBack, this.payload);
            return janusCmdMsg;
        }

        public JanusCmdMsg buildSuccessResponse() {
            baseCheck();
            JanusCmdMsg janusCmdMsg = new JanusCmdMsg(this.cluster, this.version, this.payload, 0, "");
            return janusCmdMsg;
        }

        public JanusCmdMsg buildFailResponse() {
            baseCheck();
            if (this.code == 0) {
                throw new IllegalArgumentException("must set code for error response");
            }
            JanusCmdMsg janusCmdMsg = new JanusCmdMsg(this.cluster, this.version, this.payload, this.code, this.message);
            return janusCmdMsg;
        }
    }

}

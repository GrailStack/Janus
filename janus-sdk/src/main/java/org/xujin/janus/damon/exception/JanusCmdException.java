package org.xujin.janus.damon.exception;


public class JanusCmdException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public JanusCmdException(String msg) {
        super(msg);
    }

    public JanusCmdException(String msg, Throwable cause) {
        super(msg, cause);
    }

    public JanusCmdException(Throwable cause) {
        super(cause);
    }

}
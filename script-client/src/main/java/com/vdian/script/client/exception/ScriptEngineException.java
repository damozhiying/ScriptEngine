package com.vdian.script.client.exception;

/**
 * @author jifang
 * @since 2016/10/26 下午5:02.
 */
public class ScriptEngineException extends RuntimeException {

    public ScriptEngineException(String message) {
        super(message);
    }

    public ScriptEngineException(Throwable cause) {
        super(cause);
    }
}

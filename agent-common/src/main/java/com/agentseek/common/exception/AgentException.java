package com.agentseek.common.exception;

/**
 * Agent 业务异常
 */
public class AgentException extends RuntimeException {

    private final int code;

    public AgentException(String message) {
        super(message);
        this.code = 500;
    }

    public AgentException(int code, String message) {
        super(message);
        this.code = code;
    }

    public int getCode() {
        return code;
    }
}

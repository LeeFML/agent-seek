package com.agentseek.common.constant;

/**
 * Agent 状态枚举
 */
public enum AgentStatus {

    CREATED("已创建"),
    INITIALIZING("初始化中"),
    RUNNING("运行中"),
    PAUSED("已暂停"),
    STOPPING("停止中"),
    STOPPED("已停止"),
    ERROR("异常"),
    DESTROYED("已销毁");

    private final String desc;

    AgentStatus(String desc) {
        this.desc = desc;
    }

    public String getDesc() {
        return desc;
    }
}

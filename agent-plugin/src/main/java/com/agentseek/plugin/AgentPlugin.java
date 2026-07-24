package com.agentseek.plugin;

import com.agentseek.core.model.Agent;

/**
 * Agent 插件接口
 */
public interface AgentPlugin {

    /**
     * 插件名称
     */
    String getName();

    /**
     * 插件类型
     */
    String getType();

    /**
     * 执行插件逻辑
     */
    Object execute(Agent agent, Object input);

    /**
     * 是否支持该类型的 Agent
     */
    default boolean supports(String agentType) {
        return true;
    }
}

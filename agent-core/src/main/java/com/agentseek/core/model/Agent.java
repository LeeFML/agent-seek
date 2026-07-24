package com.agentseek.core.model;

import com.agentseek.common.constant.AgentStatus;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * Agent 实体定义
 */
@Data
public class Agent {

    /** Agent 唯一标识 */
    private String agentId;

    /** Agent 名称 */
    private String name;

    /** Agent 类型 */
    private String type;

    /** 当前状态 */
    private AgentStatus status;

    /** Agent 配置参数 */
    private Map<String, Object> config;

    /** 所属工作流 ID */
    private String workflowId;

    /** 创建时间 */
    private LocalDateTime createTime;

    /** 最后更新时间 */
    private LocalDateTime updateTime;

    /** 描述 */
    private String description;
}

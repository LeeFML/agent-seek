package com.agentseek.core.entity;

import com.agentseek.common.entity.BaseEntity;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Agent 配置表
 * 数据库持久化的 Agent 定义
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("agent_config")
public class AgentConfig extends BaseEntity {

    /** Agent 唯一标识 */
    private String agentId;

    /** Agent 名称 */
    private String name;

    /** Agent 类型 */
    private String type;

    /** 描述 */
    private String description;

    /** 关联的模型配置 ID */
    private Long modelConfigId;

    /** 系统提示词（System Prompt） */
    private String systemPrompt;

    /** 工作流 ID */
    private String workflowId;

    /** 是否启用 */
    private Boolean isEnabled;

    /** 扩展配置 JSON */
    private String extraConfig;
}

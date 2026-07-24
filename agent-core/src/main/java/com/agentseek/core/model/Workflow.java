package com.agentseek.core.model;

import lombok.Data;

import java.util.List;

/**
 * 工作流定义
 */
@Data
public class Workflow {

    /** 工作流 ID */
    private String workflowId;

    /** 工作流名称 */
    private String name;

    /** 工作流描述 */
    private String description;

    /** 工作流节点（按执行顺序） */
    private List<WorkflowNode> nodes;

    /** 是否启用 */
    private boolean enabled;

    /** 触发方式：SCHEDULE / EVENT / MANUAL */
    private String triggerType;

    /** Cron 表达式（定时触发时） */
    private String cronExpression;
}

package com.agentseek.core.model;

import lombok.Data;

import java.util.Map;

/**
 * 工作流节点
 */
@Data
public class WorkflowNode {

    /** 节点 ID */
    private String nodeId;

    /** 节点名称 */
    private String name;

    /** 节点类型：AGENT / CONDITION / LOOP / DELAY */
    private String type;

    /** 执行的 Agent ID */
    private String agentId;

    /** 节点配置 */
    private Map<String, Object> config;

    /** 下一个节点 ID */
    private String nextNodeId;

    /** 条件分支：条件表达式 -> 目标节点 ID */
    private Map<String, String> conditionBranches;
}

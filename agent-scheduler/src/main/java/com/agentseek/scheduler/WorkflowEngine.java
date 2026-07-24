package com.agentseek.scheduler;

import com.agentseek.core.engine.AgentLifecycleManager;
import com.agentseek.core.model.Workflow;
import com.agentseek.core.model.WorkflowNode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * 工作流执行引擎
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class WorkflowEngine {

    private final AgentLifecycleManager agentManager;

    /**
     * 执行工作流
     */
    public void execute(Workflow workflow) {
        log.info("开始执行工作流: {}", workflow.getWorkflowId());
        Map<String, Object> context = new HashMap<>();

        if (workflow.getNodes() == null || workflow.getNodes().isEmpty()) {
            log.warn("工作流 {} 没有定义任何节点", workflow.getWorkflowId());
            return;
        }

        WorkflowNode current = workflow.getNodes().get(0);
        while (current != null) {
            log.info("执行节点: {} ({})"
, current.getName(), current.getType());

            switch (current.getType()) {
                case "AGENT" -> executeAgentNode(current, context);
                case "DELAY" -> executeDelayNode(current);
                case "CONDITION" -> current = evaluateCondition(current, context);
                default -> log.warn("未知节点类型: {}", current.getType());
            }

            if (current != null && !"CONDITION".equals(current.getType())) {
                current = findNextNode(workflow, current.getNextNodeId());
            }
        }

        log.info("工作流 {} 执行完成", workflow.getWorkflowId());
    }

    private void executeAgentNode(WorkflowNode node, Map<String, Object> context) {
        String agentId = node.getAgentId();
        if (agentId != null) {
            try {
                agentManager.start(agentId);
                // 这里可以扩展为真正的 Agent 执行逻辑
                log.info("Agent {} 执行完毕，上下文: {}", agentId, context);
            } catch (Exception e) {
                log.error("Agent {} 执行失败: {}", agentId, e.getMessage());
            }
        }
    }

    private void executeDelayNode(WorkflowNode node) {
        Object delayObj = node.getConfig().get("delayMs");
        long delayMs = delayObj instanceof Number ? ((Number) delayObj).longValue() : 1000;
        try {
            Thread.sleep(delayMs);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    private WorkflowNode evaluateCondition(WorkflowNode node, Map<String, Object> context) {
        // 简化实现：根据上下文中的变量值决定分支
        if (node.getConditionBranches() != null) {
            for (Map.Entry<String, String> entry : node.getConditionBranches().entrySet()) {
                log.info("条件分支: {} -> {}", entry.getKey(), entry.getValue());
            }
        }
        return null;
    }

    private WorkflowNode findNextNode(Workflow workflow, String nextNodeId) {
        if (nextNodeId == null) return null;
        return workflow.getNodes().stream()
                .filter(n -> n.getNodeId().equals(nextNodeId))
                .findFirst()
                .orElse(null);
    }
}

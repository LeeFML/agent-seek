package com.agentseek.plugin;

import com.agentseek.core.model.Agent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * 代码审查插件示例：自动检查代码规范
 */
@Slf4j
@Component
public class CodeReviewPlugin implements AgentPlugin {

    @Override
    public String getName() {
        return "code-review";
    }

    @Override
    public String getType() {
        return "DEVELOPER_TOOL";
    }

    @Override
    public Object execute(Agent agent, Object input) {
        String code = input != null ? input.toString() : "";
        log.info("[CodeReview] Agent {} 正在审查代码...", agent.getAgentId());

        // 模拟代码审查逻辑
        int issues = 0;
        if (code.contains("System.out.println")) {
            issues++;
            log.warn("[CodeReview] 发现硬编码日志输出");
        }
        if (code.contains("TODO")) {
            issues++;
            log.warn("[CodeReview] 发现未完成的 TODO");
        }

        return "审查完成，发现 " + issues + " 个问题";
    }
}

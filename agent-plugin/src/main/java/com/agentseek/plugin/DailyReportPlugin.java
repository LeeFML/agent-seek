package com.agentseek.plugin;

import com.agentseek.core.model.Agent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * 日报生成插件示例：自动汇总工作内容
 */
@Slf4j
@Component
public class DailyReportPlugin implements AgentPlugin {

    @Override
    public String getName() {
        return "daily-report";
    }

    @Override
    public String getType() {
        return "PRODUCTIVITY";
    }

    @Override
    public Object execute(Agent agent, Object input) {
        log.info("[DailyReport] Agent {} 正在生成日报...", agent.getAgentId());
        // 模拟从 Git 提交、任务系统等拉取数据
        String report = """
            # 工作日报
            - 提交代码: 5 次
            - 完成 PR: 2 个
            - Code Review: 3 次
            - 修复 Bug: 1 个
            """;
        return report;
    }
}

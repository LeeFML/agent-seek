package com.agentseek.scheduler.job;

import com.agentseek.scheduler.WorkflowEngine;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * 定时扫描任务：检查需要触发的工作流
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class WorkflowScanJob {

    private final WorkflowEngine workflowEngine;

    @Scheduled(fixedRate = 60000)
    public void scanAndTrigger() {
        log.debug("扫描定时工作流...");
        // 实际实现中从数据库/缓存读取待执行的工作流
    }
}

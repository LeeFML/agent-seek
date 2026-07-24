package com.agentseek.api.controller;

import com.agentseek.common.result.Result;
import com.agentseek.core.engine.AgentLifecycleManager;
import com.agentseek.core.model.Agent;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;

/**
 * Agent 管理 REST API
 */
@RestController
@RequestMapping("/api/v1/agents")
@RequiredArgsConstructor
public class AgentController {

    private final AgentLifecycleManager agentManager;

    /**
     * 注册 Agent
     */
    @PostMapping
    public Result<String> register(@RequestBody Agent agent) {
        agentManager.register(agent);
        return Result.ok(agent.getAgentId());
    }

    /**
     * 获取 Agent 详情
     */
    @GetMapping("/{agentId}")
    public Result<Agent> getAgent(@PathVariable String agentId) {
        return Result.ok(agentManager.getAgent(agentId));
    }

    /**
     * 列出所有 Agent
     */
    @GetMapping
    public Result<Collection<Agent>> listAgents() {
        return Result.ok(agentManager.listAll().values());
    }

    /**
     * 启动 Agent
     */
    @PostMapping("/{agentId}/start")
    public Result<Void> start(@PathVariable String agentId) {
        agentManager.start(agentId);
        return Result.ok();
    }

    /**
     * 暂停 Agent
     */
    @PostMapping("/{agentId}/pause")
    public Result<Void> pause(@PathVariable String agentId) {
        agentManager.pause(agentId);
        return Result.ok();
    }

    /**
     * 停止 Agent
     */
    @PostMapping("/{agentId}/stop")
    public Result<Void> stop(@PathVariable String agentId) {
        agentManager.stop(agentId);
        return Result.ok();
    }

    /**
     * 销毁 Agent
     */
    @DeleteMapping("/{agentId}")
    public Result<Void> destroy(@PathVariable String agentId) {
        agentManager.destroy(agentId);
        return Result.ok();
    }
}

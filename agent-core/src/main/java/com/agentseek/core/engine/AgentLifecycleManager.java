package com.agentseek.core.engine;

import com.agentseek.common.constant.AgentStatus;
import com.agentseek.common.exception.AgentException;
import com.agentseek.core.model.Agent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Agent 生命周期管理器
 */
@Slf4j
@Component
public class AgentLifecycleManager {

    private final Map<String, Agent> agentRegistry = new ConcurrentHashMap<>();

    /**
     * 注册 Agent
     */
    public void register(Agent agent) {
        agent.setStatus(AgentStatus.CREATED);
        agent.setCreateTime(LocalDateTime.now());
        agentRegistry.put(agent.getAgentId(), agent);
        log.info("Agent registered: {}", agent.getAgentId());
    }

    /**
     * 启动 Agent
     */
    public void start(String agentId) {
        Agent agent = getAgent(agentId);
        if (agent.getStatus() == AgentStatus.RUNNING) {
            throw new AgentException("Agent already running: " + agentId);
        }
        agent.setStatus(AgentStatus.RUNNING);
        agent.setUpdateTime(LocalDateTime.now());
        log.info("Agent started: {}", agentId);
    }

    /**
     * 暂停 Agent
     */
    public void pause(String agentId) {
        Agent agent = getAgent(agentId);
        agent.setStatus(AgentStatus.PAUSED);
        agent.setUpdateTime(LocalDateTime.now());
        log.info("Agent paused: {}", agentId);
    }

    /**
     * 停止 Agent
     */
    public void stop(String agentId) {
        Agent agent = getAgent(agentId);
        agent.setStatus(AgentStatus.STOPPED);
        agent.setUpdateTime(LocalDateTime.now());
        log.info("Agent stopped: {}", agentId);
    }

    /**
     * 销毁 Agent
     */
    public void destroy(String agentId) {
        Agent agent = agentRegistry.remove(agentId);
        if (agent != null) {
            agent.setStatus(AgentStatus.DESTROYED);
            log.info("Agent destroyed: {}", agentId);
        }
    }

    /**
     * 获取 Agent
     */
    public Agent getAgent(String agentId) {
        Agent agent = agentRegistry.get(agentId);
        if (agent == null) {
            throw new AgentException("Agent not found: " + agentId);
        }
        return agent;
    }

    /**
     * 列出所有 Agent
     */
    public Map<String, Agent> listAll() {
        return new ConcurrentHashMap<>(agentRegistry);
    }
}

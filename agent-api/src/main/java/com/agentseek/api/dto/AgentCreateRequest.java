package com.agentseek.api.dto;

import lombok.Data;

import java.util.Map;

/**
 * Agent 创建请求 DTO
 */
@Data
public class AgentCreateRequest {

    private String name;
    private String type;
    private String description;
    private Map<String, Object> config;
}

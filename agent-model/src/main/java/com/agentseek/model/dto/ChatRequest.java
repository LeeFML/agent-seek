package com.agentseek.model.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

/**
 * 聊天请求
 */
@Data
@Builder
public class ChatRequest {

    private String model;
    private List<Message> messages;
    private Double temperature;
    private Integer maxTokens;
    private Boolean stream;

    @Data
    @Builder
    public static class Message {
        private String role;    // system / user / assistant
        private String content;
    }
}

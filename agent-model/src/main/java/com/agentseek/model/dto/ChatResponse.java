package com.agentseek.model.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

/**
 * 聊天响应
 */
@Data
@Builder
public class ChatResponse {

    private String id;
    private String model;
    private String content;
    private Integer promptTokens;
    private Integer completionTokens;
    private Integer totalTokens;
    private List<Choice> choices;

    @Data
    @Builder
    public static class Choice {
        private Integer index;
        private ChatRequest.Message message;
        private String finishReason;
    }
}

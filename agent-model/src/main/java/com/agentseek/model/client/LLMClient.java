package com.agentseek.model.client;

import com.agentseek.model.dto.ChatRequest;
import com.agentseek.model.dto.ChatResponse;

/**
 * LLM 大模型调用统一接口
 * 所有模型客户端（DeepSeek / Qwen / Kimi）都实现此接口
 */
public interface LLMClient {

    /**
     * 发起聊天请求
     */
    ChatResponse chat(ChatRequest request);

    /**
     * 流式聊天（预留）
     */
    default void chatStream(ChatRequest request, StreamCallback callback) {
        throw new UnsupportedOperationException("Stream not supported yet");
    }

    /**
     * 客户端支持的提供商类型
     */
    String getProvider();

    /**
     * 是否可用
     */
    boolean isAvailable();

    /**
     * 流式回调接口
     */
    interface StreamCallback {
        void onMessage(String chunk);
        void onComplete();
        void onError(Throwable e);
    }
}

package com.agentseek.model.service;

import com.agentseek.model.client.LLMClient;
import com.agentseek.model.config.ModelConfig;
import com.agentseek.model.dto.ChatRequest;
import com.agentseek.model.dto.ChatResponse;
import com.agentseek.model.factory.ModelClientFactory;
import com.agentseek.model.mapper.ModelConfigMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 模型调用服务
 * 上层业务通过此服务调用大模型，无需关心底层是哪个提供商
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ModelChatService {

    private final ModelConfigMapper modelConfigMapper;
    private final ModelClientFactory clientFactory;

    /**
     * 使用默认模型进行对话
     */
    public ChatResponse chat(List<ChatRequest.Message> messages) {
        ModelConfig defaultConfig = modelConfigMapper.selectDefault();
        if (defaultConfig == null) {
            log.error("No default model config found in database");
            return ChatResponse.builder()
                    .content("错误：数据库中未配置默认模型，请先通过管理后台配置 model_config")
                    .build();
        }
        return chatWithConfig(defaultConfig, messages);
    }

    /**
     * 使用指定提供商进行对话
     */
    public ChatResponse chatWithProvider(String provider, List<ChatRequest.Message> messages) {
        ModelConfig config = modelConfigMapper.selectByProvider(provider);
        if (config == null) {
            log.error("Model config not found for provider: {}", provider);
            return ChatResponse.builder()
                    .content("错误：未找到提供商 " + provider + " 的配置")
                    .build();
        }
        return chatWithConfig(config, messages);
    }

    /**
     * 使用指定配置进行对话
     */
    public ChatResponse chatWithConfig(ModelConfig config, List<ChatRequest.Message> messages) {
        LLMClient client = clientFactory.getClient(config);
        if (!client.isAvailable()) {
            return ChatResponse.builder()
                    .content("错误：模型客户端不可用，请检查 API Key 配置")
                    .build();
        }

        ChatRequest request = ChatRequest.builder()
                .model(config.getModelName())
                .messages(messages)
                .temperature(config.getTemperature())
                .maxTokens(config.getMaxTokens())
                .build();

        return client.chat(request);
    }

    /**
     * 列出所有启用的模型配置
     */
    public List<ModelConfig> listEnabledConfigs() {
        return modelConfigMapper.selectList(
                new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<ModelConfig>()
                        .eq(ModelConfig::getIsEnabled, true)
                        .eq(ModelConfig::getDeleted, 0)
                        .orderByAsc(ModelConfig::getSortOrder)
        );
    }
}

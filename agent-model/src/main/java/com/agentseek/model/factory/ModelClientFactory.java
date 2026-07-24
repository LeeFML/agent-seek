package com.agentseek.model.factory;

import com.agentseek.model.client.DeepSeekClient;
import com.agentseek.model.client.KimiClient;
import com.agentseek.model.client.LLMClient;
import com.agentseek.model.client.QwenClient;
import com.agentseek.model.config.ModelConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * LLM 客户端工厂
 * 根据数据库中的 model_config 配置动态创建对应客户端
 */
@Slf4j
@Component
public class ModelClientFactory {

    private final Map<String, LLMClient> clientCache = new ConcurrentHashMap<>();

    /**
     * 根据配置创建或获取客户端（带缓存）
     */
    public LLMClient getClient(ModelConfig config) {
        String cacheKey = config.getProvider() + ":" + config.getConfigName();
        return clientCache.computeIfAbsent(cacheKey, k -> createClient(config));
    }

    /**
     * 创建新客户端实例（不缓存）
     */
    public LLMClient createClient(ModelConfig config) {
        String provider = config.getProvider();
        String baseUrl = config.getBaseUrl();
        String apiKey = config.getApiKey();
        String modelName = config.getModelName();

        log.info("Creating LLM client for provider: {}, model: {}", provider, modelName);

        return switch (provider.toLowerCase()) {
            case "deepseek" -> new DeepSeekClient(baseUrl, apiKey, modelName);
            case "qwen", "dashscope" -> new QwenClient(baseUrl, apiKey, modelName);
            case "kimi", "moonshot" -> new KimiClient(baseUrl, apiKey, modelName);
            default -> {
                log.warn("Unknown provider: {}, fallback to OpenAI compatible format", provider);
                yield new DeepSeekClient(baseUrl, apiKey, modelName); // OpenAI 兼容格式
            }
        };
    }

    /**
     * 清除缓存
     */
    public void clearCache() {
        clientCache.clear();
        log.info("LLM client cache cleared");
    }
}

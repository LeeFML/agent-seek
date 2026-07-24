package com.agentseek.model.config;

import com.agentseek.common.entity.BaseEntity;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 大模型配置表
 * 支持在数据库中动态配置多个模型提供商
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("model_config")
public class ModelConfig extends BaseEntity {

    /** 配置名称，如：deepseek-chat */
    private String configName;

    /** 提供商：deepseek / qwen / kimi / openai */
    private String provider;

    /** 模型名称，如：deepseek-chat / qwen-turbo / moonshot-v1-8k */
    private String modelName;

    /** API 基础地址 */
    private String baseUrl;

    /** API Key（建议加密存储） */
    private String apiKey;

    /** 默认温度参数 */
    private Double temperature;

    /** 默认最大 Token 数 */
    private Integer maxTokens;

    /** 是否为默认模型 */
    private Boolean isDefault;

    /** 是否启用 */
    private Boolean isEnabled;

    /** 排序权重 */
    private Integer sortOrder;

    /** 扩展配置 JSON */
    private String extraConfig;
}

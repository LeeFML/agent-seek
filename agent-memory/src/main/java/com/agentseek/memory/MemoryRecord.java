package com.agentseek.memory;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 记忆记录
 * 存储用户的对话上下文、Agent 执行结果等
 */
@Data
@Builder
public class MemoryRecord {

    /** 记忆 ID */
    private String memoryId;

    /** 所属用户 ID */
    private String userId;

    /** 所属 Agent ID */
    private String agentId;

    /** 记忆类型：chat / action / thought / summary */
    private String memoryType;

    /** 原始内容文本 */
    private String content;

    /** 向量嵌入（用于相似度搜索） */
    private float[] embedding;

    /** 元数据 JSON */
    private String metadata;

    /** 创建时间 */
    private LocalDateTime createTime;

    /** 会话 ID */
    private String sessionId;

    /** 记忆重要性评分（0-10） */
    private Integer importance;
}

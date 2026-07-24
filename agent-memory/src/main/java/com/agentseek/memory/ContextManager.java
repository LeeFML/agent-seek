package com.agentseek.memory;

import com.agentseek.memory.store.VectorStore;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 上下文管理器
 * 负责维护用户的长期记忆，支持相似度检索相关历史上下文
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class ContextManager {

    private final VectorStore vectorStore;

    /**
     * 保存对话上下文
     */
    public void saveContext(String userId, String agentId, String content, float[] embedding) {
        MemoryRecord record = MemoryRecord.builder()
                .userId(userId)
                .agentId(agentId)
                .memoryType("chat")
                .content(content)
                .embedding(embedding)
                .importance(5)
                .build();
        vectorStore.save(record);
    }

    /**
     * 检索相关上下文
     * 根据当前查询向量，找出用户历史上最相关的记忆
     */
    public List<String> retrieveRelevantContext(String userId, float[] queryVector, int topK) {
        List<MemoryRecord> records = vectorStore.searchByUser(userId, queryVector, topK);
        return records.stream()
                .map(MemoryRecord::getContent)
                .collect(Collectors.toList());
    }

    /**
     * 构建带上下文的提示词
     * 将检索到的历史上下文注入到当前提示中
     */
    public String buildContextualPrompt(String userId, float[] queryVector, String currentPrompt, int contextSize) {
        List<String> contexts = retrieveRelevantContext(userId, queryVector, contextSize);
        if (contexts.isEmpty()) {
            return currentPrompt;
        }

        StringBuilder sb = new StringBuilder();
        sb.append("【历史上下文】\n");
        for (int i = 0; i < contexts.size(); i++) {
            sb.append(i + 1).append(". ").append(contexts.get(i)).append("\n");
        }
        sb.append("\n【当前问题】\n");
        sb.append(currentPrompt);
        return sb.toString();
    }

    /**
     * 清空用户记忆
     */
    public void clearUserMemory(String userId) {
        vectorStore.deleteByUser(userId);
    }
}

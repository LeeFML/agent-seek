package com.agentseek.memory.store;

import com.agentseek.memory.MemoryRecord;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * 内存向量存储实现
 * 适用于开发和测试环境，生产环境建议替换为 Milvus / pgvector
 */
@Slf4j
@Component
public class InMemoryVectorStore implements VectorStore {

    private final Map<String, MemoryRecord> store = new ConcurrentHashMap<>();

    @Override
    public void save(MemoryRecord record) {
        if (record.getMemoryId() == null) {
            record.setMemoryId(UUID.randomUUID().toString());
        }
        if (record.getCreateTime() == null) {
            record.setCreateTime(java.time.LocalDateTime.now());
        }
        store.put(record.getMemoryId(), record);
        log.debug("Memory saved: {}, user: {}", record.getMemoryId(), record.getUserId());
    }

    @Override
    public List<MemoryRecord> search(float[] queryVector, int topK) {
        return store.values().stream()
                .filter(r -> r.getEmbedding() != null)
                .sorted((a, b) -> Float.compare(
                        cosineSimilarity(b.getEmbedding(), queryVector),
                        cosineSimilarity(a.getEmbedding(), queryVector)))
                .limit(topK)
                .collect(Collectors.toList());
    }

    @Override
    public List<MemoryRecord> searchByUser(String userId, float[] queryVector, int topK) {
        return store.values().stream()
                .filter(r -> userId.equals(r.getUserId()))
                .filter(r -> r.getEmbedding() != null)
                .sorted((a, b) -> Float.compare(
                        cosineSimilarity(b.getEmbedding(), queryVector),
                        cosineSimilarity(a.getEmbedding(), queryVector)))
                .limit(topK)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteByUser(String userId) {
        List<String> toDelete = store.values().stream()
                .filter(r -> userId.equals(r.getUserId()))
                .map(MemoryRecord::getMemoryId)
                .toList();
        toDelete.forEach(store::remove);
        log.info("Deleted {} memories for user: {}", toDelete.size(), userId);
    }
}

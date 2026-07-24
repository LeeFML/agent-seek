package com.agentseek.memory.store;

import com.agentseek.memory.MemoryRecord;

import java.util.List;

/**
 * 向量存储接口
 * 支持内存实现（开发测试）和 Milvus / pgvector 等生产实现
 */
public interface VectorStore {

    /**
     * 保存一条记忆记录
     */
    void save(MemoryRecord record);

    /**
     * 根据向量相似度搜索相关记忆
     * @param queryVector 查询向量
     * @param topK 返回最相似的 K 条
     */
    List<MemoryRecord> search(float[] queryVector, int topK);

    /**
     * 根据用户 ID 搜索相关记忆
     */
    List<MemoryRecord> searchByUser(String userId, float[] queryVector, int topK);

    /**
     * 删除用户的所有记忆
     */
    void deleteByUser(String userId);

    /**
     * 计算向量相似度（余弦相似度）
     */
    default float cosineSimilarity(float[] a, float[] b) {
        if (a.length != b.length) return 0;
        float dot = 0, normA = 0, normB = 0;
        for (int i = 0; i < a.length; i++) {
            dot += a[i] * b[i];
            normA += a[i] * a[i];
            normB += b[i] * b[i];
        }
        return (float) (dot / (Math.sqrt(normA) * Math.sqrt(normB)));
    }
}

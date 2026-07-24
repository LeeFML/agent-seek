-- Agent Seek 数据库初始化脚本（H2 兼容）

-- 模型配置表：支持 DeepSeek / Qwen / Kimi 等多模型动态配置
CREATE TABLE IF NOT EXISTS model_config (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    config_name VARCHAR(64) NOT NULL COMMENT '配置名称',
    provider VARCHAR(32) NOT NULL COMMENT '提供商：deepseek/qwen/kimi/openai',
    model_name VARCHAR(64) NOT NULL COMMENT '模型名称',
    base_url VARCHAR(255) NOT NULL COMMENT 'API 基础地址',
    api_key VARCHAR(512) NOT NULL COMMENT 'API Key',
    temperature DOUBLE DEFAULT 0.7 COMMENT '默认温度参数',
    max_tokens INT DEFAULT 2048 COMMENT '默认最大 Token 数',
    is_default TINYINT DEFAULT 0 COMMENT '是否默认模型',
    is_enabled TINYINT DEFAULT 1 COMMENT '是否启用',
    sort_order INT DEFAULT 0 COMMENT '排序权重',
    extra_config VARCHAR(2000) COMMENT '扩展配置 JSON',
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    deleted TINYINT DEFAULT 0
);

-- Agent 配置表：数据库持久化的 Agent 定义
CREATE TABLE IF NOT EXISTS agent_config (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    agent_id VARCHAR(64) NOT NULL UNIQUE COMMENT 'Agent 唯一标识',
    name VARCHAR(128) NOT NULL COMMENT 'Agent 名称',
    type VARCHAR(32) DEFAULT 'custom' COMMENT 'Agent 类型',
    description VARCHAR(500) COMMENT '描述',
    model_config_id BIGINT COMMENT '关联的模型配置 ID',
    system_prompt TEXT COMMENT '系统提示词',
    workflow_id VARCHAR(64) COMMENT '关联工作流 ID',
    is_enabled TINYINT DEFAULT 1 COMMENT '是否启用',
    extra_config VARCHAR(2000) COMMENT '扩展配置 JSON',
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    deleted TINYINT DEFAULT 0
);

-- 插件配置表：数据库持久化的插件注册
CREATE TABLE IF NOT EXISTS plugin_config (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    plugin_name VARCHAR(64) NOT NULL UNIQUE COMMENT '插件名称',
    plugin_type VARCHAR(32) NOT NULL COMMENT '插件类型',
    class_name VARCHAR(255) COMMENT '全限定类名',
    description VARCHAR(500) COMMENT '插件描述',
    version VARCHAR(32) DEFAULT '1.0.0' COMMENT '版本号',
    is_enabled TINYINT DEFAULT 1 COMMENT '是否启用',
    config_json VARCHAR(2000) COMMENT '配置参数 JSON',
    sort_order INT DEFAULT 0 COMMENT '排序',
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    deleted TINYINT DEFAULT 0
);

-- 用户上下文记忆表（文本存储，向量通过内存/向量数据库存储）
CREATE TABLE IF NOT EXISTS user_context_memory (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    memory_id VARCHAR(64) NOT NULL UNIQUE COMMENT '记忆 ID',
    user_id VARCHAR(64) NOT NULL COMMENT '用户 ID',
    agent_id VARCHAR(64) COMMENT 'Agent ID',
    memory_type VARCHAR(32) DEFAULT 'chat' COMMENT '记忆类型',
    content TEXT NOT NULL COMMENT '原始内容',
    embedding_vector VARCHAR(4000) COMMENT '向量序列化（逗号分隔浮点数）',
    metadata VARCHAR(2000) COMMENT '元数据 JSON',
    session_id VARCHAR(64) COMMENT '会话 ID',
    importance INT DEFAULT 5 COMMENT '重要性评分 0-10',
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    deleted TINYINT DEFAULT 0
);

CREATE INDEX idx_user_context_user ON user_context_memory(user_id);
CREATE INDEX idx_user_context_session ON user_context_memory(session_id);
CREATE INDEX idx_agent_config_agent_id ON agent_config(agent_id);

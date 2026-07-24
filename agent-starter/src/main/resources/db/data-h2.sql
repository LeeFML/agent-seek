-- Agent Seek 示例数据

-- 模型配置示例（请替换为真实的 API Key）
INSERT INTO model_config (config_name, provider, model_name, base_url, api_key, temperature, max_tokens, is_default, is_enabled, sort_order)
VALUES
('deepseek-chat', 'deepseek', 'deepseek-chat', 'https://api.deepseek.com', 'sk-your-deepseek-key', 0.7, 4096, 1, 1, 1),
('qwen-turbo', 'qwen', 'qwen-turbo', 'https://dashscope.aliyuncs.com/compatible-mode/v1', 'sk-your-qwen-key', 0.7, 2048, 0, 1, 2),
('kimi-8k', 'kimi', 'moonshot-v1-8k', 'https://api.moonshot.cn/v1', 'sk-your-kimi-key', 0.7, 8192, 0, 1, 3);

-- Agent 配置示例
INSERT INTO agent_config (agent_id, name, type, description, model_config_id, system_prompt, is_enabled)
VALUES
('code-reviewer', '代码审查助手', 'developer_tool', '自动审查代码规范并给出建议', 1,
 '你是一名资深 Java 开发工程师，擅长代码审查。请检查代码中的潜在问题，包括：性能、安全性、可读性、最佳实践。', 1),
('daily-reporter', '日报生成助手', 'productivity', '自动汇总工作内容生成日报', 1,
 '你是一名高效的工作助手。请根据提供的工作记录，整理成结构清晰、语言简洁的日报。', 1),
('tech-writer', '技术文档助手', 'developer_tool', '协助撰写技术文档和注释', 1,
 '你是一名技术文档专家。请将技术内容整理成清晰、专业的文档。', 1);

-- 插件配置示例
INSERT INTO plugin_config (plugin_name, plugin_type, class_name, description, version, is_enabled, sort_order)
VALUES
('code-review', 'DEVELOPER_TOOL', 'com.agentseek.plugin.CodeReviewPlugin', '自动代码审查插件', '1.0.0', 1, 1),
('daily-report', 'PRODUCTIVITY', 'com.agentseek.plugin.DailyReportPlugin', '日报自动生成插件', '1.0.0', 1, 2);

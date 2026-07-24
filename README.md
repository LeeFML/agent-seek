# Agent Seek

> 一个面向 Java 后端开发的 Agent 智能体管理系统，支持多模型动态切换、数据库配置化、向量上下文记忆。

---

## 项目简介

**Agent Seek** 是一个基于 Spring Boot 3 + Java 17 构建的 Agent 管理后端框架。它提供完整的**智能体编排引擎**，支持在数据库中动态配置多个大模型（DeepSeek / Qwen / Kimi 等），并通过向量存储实现用户长期记忆。

### 核心能力

| 能力 | 说明 |
|------|------|
| **多模型动态切换** | 数据库配置多个 LLM API，运行时按需切换，不硬编码 |
| **Agent 数据库配置化** | Agent 定义、插件注册全部持久化到数据库，支持热更新 |
| **向量上下文记忆** | 用户对话上下文存入向量存储，支持相似度检索，实现长期记忆 |
| **工作流编排** | 可视化节点编排，串行/条件/延迟/循环多种节点类型 |
| **插件扩展** | 内置 + 动态加载，支持自定义 Agent 能力 |

---

## 架构设计

```
┌─────────────────────────────────────────────────────────────────────────┐
│                              Agent Seek                                  │
├──────────┬──────────┬──────────┬──────────┬──────────┬──────────────────┤
│ REST API │ 模型调用 │ 工作流   │ 任务调度 │ 插件扩展 │  向量记忆         │
│(agent-api│(agent-  │ 引擎     │(Quartz/ │(agent-  │ (agent-memory)   │
│)         │ model)   │(agent-  │ Spring) │ plugin) │                  │
│          │          │scheduler)│         │         │                  │
├──────────┴──────────┴──────────┴──────────┴──────────┴──────────────────┤
│                    Agent 生命周期管理器 + 上下文管理器                      │
├─────────────────────────────────────────────────────────────────────────┤
│         MyBatis-Plus │  H2 / MySQL  │  内存向量 / Milvus（预留）          │
├─────────────────────────────────────────────────────────────────────────┤
│         Spring Boot 3  │  Java 17  │  Maven 多模块                       │
└─────────────────────────────────────────────────────────────────────────┘
```

---

## 多模型管理：数据库动态配置

### 为什么需要数据库配置模型？

传统做法是把 API Key 和模型地址硬编码在代码或配置文件中：
- 换个模型要改代码重新部署
- 团队成员各自维护自己的 API Key
- 无法 A/B 测试不同模型效果

Agent Seek 把所有模型信息放在 `model_config` 表中：

```
+----+---------------+----------+------------------+----------------------------+----------------------+
| id | config_name   | provider | model_name       | base_url                   | api_key              |
+----+---------------+----------+------------------+----------------------------+----------------------+
| 1  | deepseek-chat | deepseek | deepseek-chat    | https://api.deepseek.com   | sk-xxx...            |
| 2  | qwen-turbo    | qwen     | qwen-turbo       | https://dashscope...       | sk-xxx...            |
| 3  | kimi-8k       | kimi     | moonshot-v1-8k   | https://api.moonshot.cn... | sk-xxx...            |
+----+---------------+----------+------------------+----------------------------+----------------------+
```

### 使用方式

```java
// 1. 使用默认模型对话
ChatResponse response = modelChatService.chat(List.of(
    ChatRequest.Message.builder().role("user").content("你好").build()
));

// 2. 指定提供商对话
ChatResponse response = modelChatService.chatWithProvider("deepseek", messages);

// 3. 指定具体配置对话
ModelConfig config = modelConfigMapper.selectById(1L);
ChatResponse response = modelChatService.chatWithConfig(config, messages);
```

### 支持的模型

| 提供商 | 实现类 | 格式 |
|--------|--------|------|
| DeepSeek | `DeepSeekClient` | OpenAI 兼容 |
| Qwen (通义千问) | `QwenClient` | OpenAI 兼容 |
| Kimi (Moonshot) | `KimiClient` | OpenAI 兼容 |
| 其他 OpenAI 兼容 | 自动回退到 `DeepSeekClient` | OpenAI 兼容 |

---

## 数据库配置化：Agent & 插件

### Agent 配置表 (`agent_config`)

不再在代码中定义 Agent，而是通过数据库管理：

```sql
INSERT INTO agent_config (agent_id, name, type, model_config_id, system_prompt, is_enabled)
VALUES ('code-reviewer', '代码审查助手', 'developer_tool', 1,
        '你是一名资深 Java 开发工程师...', 1);
```

每个 Agent 可以：
- 绑定不同的模型配置
- 拥有独立的系统提示词（System Prompt）
- 关联工作流
- 动态启用/禁用

### 插件配置表 (`plugin_config`)

插件注册也从数据库读取：

```sql
INSERT INTO plugin_config (plugin_name, plugin_type, class_name, is_enabled, sort_order)
VALUES ('code-review', 'DEVELOPER_TOOL', 'com.agentseek.plugin.CodeReviewPlugin', 1, 1);
```

---

## 向量上下文记忆

### 问题：为什么需要向量存储？

普通数据库存储是按时间顺序记录的，检索时只能按关键词或时间范围查找。但用户提问往往是语义化的：

> "上次说的那个方案后来怎么样了？"

关键词匹配会失败，而**向量相似度搜索**能找到语义相关的历史记录。

### 实现原理

```
用户提问 → 向量化 → 向量相似度搜索 → 找出最相关的 K 条历史记忆
                                              ↓
                                    构建带上下文的提示词 → 发给 LLM
```

### 使用方式

```java
// 保存对话上下文（需先将文本转为向量 embedding）
contextManager.saveContext("user_123", "agent_1", "用户说：帮我写个排序算法", embedding);

// 检索相关上下文
List<String> contexts = contextManager.retrieveRelevantContext("user_123", queryEmbedding, 5);

// 构建带上下文的提示词
String prompt = contextManager.buildContextualPrompt("user_123", queryEmbedding, "帮我写代码", 5);
```

### 向量存储实现

| 实现 | 适用场景 |
|------|---------|
| `InMemoryVectorStore` | 开发测试，单机内存存储 |
| `MilvusVectorStore`（预留） | 生产环境，分布式向量数据库 |
| `PgVectorStore`（预留） | 生产环境，PostgreSQL 扩展 |

---

## 工作流编排：把重复工作交给 Agent

### 核心理念

将"重复性工作"抽象为**工作流（Workflow）**，由一系列**节点（Node）**组成：

- **Agent 节点** — 调用某个具体的智能体执行任务
- **条件节点** — 根据执行结果决定走哪条分支
- **延迟节点** — 等待一段时间再执行下一步
- **循环节点** — 对一批数据重复执行某个 Agent

### 典型场景

#### 1. 自动日报生成

```
定时触发(18:00) → Git拉取提交 → 整理任务 → AI生成日报 → 飞书推送
```

配置示例：
```yaml
workflow:
  name: "每日自动日报"
  trigger: SCHEDULE
  cron: "0 0 18 * * MON-FRI"
  nodes:
    - name: "拉取代码记录"    type: AGENT   agentId: "git-analyzer"
    - name: "生成日报内容"    type: AGENT   agentId: "daily-reporter"
    - name: "推送消息"        type: AGENT   agentId: "feishu-notifier"
```

#### 2. 代码提交前自动审查

```
代码Push → 静态检查 → AI代码审查 → 条件判断 → 通过则允许合并 / 失败则阻断+通知
```

---

## 技术栈

| 层级 | 技术 |
|------|------|
| 语言 | Java 17 |
| 框架 | Spring Boot 3.2.x |
| ORM | MyBatis-Plus 3.5.6 |
| 数据库 | H2（默认）/ MySQL（生产） |
| 向量存储 | 内存实现（默认）/ Milvus（预留） |
| 任务调度 | Spring Scheduler |
| 构建 | Maven 3.9+ |

## 项目结构

```
agent-seek/
├── agent-common/          # 公共模块：工具类、常量、异常、BaseEntity
├── agent-core/            # 核心引擎：Agent 生命周期、上下文、事件总线、数据库实体
│   └── entity/            #   AgentConfig / PluginConfig 数据库实体
│   └── mapper/            #   MyBatis-Plus Mapper
├── agent-model/           # 大模型调用模块：多模型动态切换
│   └── client/            #   LLMClient 接口 + DeepSeek/Qwen/Kimi 实现
│   └── config/            #   ModelConfig 数据库实体
│   └── dto/               #   ChatRequest / ChatResponse
│   └── factory/           #   ModelClientFactory 工厂
│   └── mapper/            #   ModelConfigMapper
│   └── service/           #   ModelChatService 统一调用入口
├── agent-memory/          # 上下文记忆模块：向量存储
│   └── store/             #   VectorStore 接口 + InMemoryVectorStore 实现
│   └── ContextManager.java #  上下文管理器
├── agent-api/             # 管理后台 REST API
├── agent-scheduler/       # 任务调度 + 工作流引擎
├── agent-plugin/          # 插件扩展模块
└── agent-starter/         # 启动入口 + 配置文件 + 数据库初始化 SQL
    └── resources/db/      #   schema-h2.sql / data-h2.sql
```

## 快速开始

```bash
# 克隆项目
git clone git@github.com:LeeFML/agent-seek.git
cd agent-seek

# 构建
mvn clean package -DskipTests

# 启动（默认使用 H2 内存数据库）
java -jar agent-starter/target/agent-starter-0.1.0-SNAPSHOT.jar
```

启动后访问：
- 健康检查：`GET http://localhost:8080/api/v1/system/health`
- H2 控制台：`http://localhost:8080/h2-console`（JDBC URL: `jdbc:h2:mem:agentseek`）

### 切换到 MySQL

1. 创建数据库：`CREATE DATABASE agent_seek;`
2. 修改 `application.yml`，注释 H2 配置，取消 MySQL 配置注释
3. 将 `schema-h2.sql` 转换为 MySQL 语法后执行
4. 重新启动

### 配置大模型 API

启动后数据会自动初始化（见 `data-h2.sql`），但 API Key 是占位符。请替换为真实 Key：

```sql
UPDATE model_config SET api_key = 'sk-your-real-key' WHERE id = 1;
```

## 插件开发

```java
@Component
public class MyPlugin implements AgentPlugin {

    @Override
    public String getName() {
        return "my-custom-plugin";
    }

    @Override
    public Object execute(Agent agent, Object input) {
        // 你的业务逻辑
        return result;
    }
}
```

同时在 `plugin_config` 表中注册即可。

## 贡献指南

欢迎提交 Issue 和 Pull Request。

## License

MIT

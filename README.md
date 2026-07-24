# Agent Seek

> 一个面向 Java 后端开发的 Agent 智能体管理系统，解决个人与团队在日常工作中使用 AI 的痛点。

---

## 项目简介

**Agent Seek** 是一个基于 Spring Boot 3 + Java 17 构建的 Agent 管理后端框架。它不只是调用大模型 API，而是提供一个完整的**智能体编排引擎**，让开发者和团队能够把日常工作中的重复性任务**流程化、自动化**，交给 Agent 智能体去执行。

### 我们解决的核心问题

在日常开发工作中，使用 AI 往往面临这些困境：

| 问题 | 表现 | Agent Seek 的解法 |
|------|------|-------------------|
| **每次从零开始** | 想 AI 帮我生成日报，每次都要重新写 prompt | 创建一次 Agent，配置好模板和参数，以后一键触发 |
| **AI 输出不统一** | 团队里每个人调 AI 的方式不同，产出质量参差不齐 | 通过工作流定义标准化流程，所有人复用同一套 Agent |
| **重复劳动浪费** | 每天花 30 分钟写日报、整理 commit、检查代码规范 | 编排定时工作流，Agent 自动完成并推送结果 |
| **多步骤任务难串联** | "先分析需求 → 生成代码 → 跑测试 → 出报告" 需要手动一步步来 | 工作流引擎串行/并行执行多个 Agent，自动流转 |
| **上下文丢失** | 对话中前面的信息后面忘了，需要重复说明 | Agent 上下文管理器自动维护状态，跨节点传递数据 |

---

## 工作流编排：把重复工作交给 Agent

### 核心理念

Agent Seek 将"重复性工作"抽象为**工作流（Workflow）**，每个工作流由一系列**节点（Node）**组成。节点可以是：

- **Agent 节点** — 调用某个具体的智能体执行任务
- **条件节点** — 根据执行结果决定走哪条分支
- **延迟节点** — 等待一段时间再执行下一步
- **循环节点** — 对一批数据重复执行某个 Agent

### 典型工作流示例

#### 1. 个人日常：自动化日报生成

```
┌─────────────┐    ┌──────────────┐    ┌─────────────┐    ┌──────────┐
│ 定时触发     │ -> │ 拉取 Git 提交 │ -> │ 整理任务进度 │ -> │ 生成日报  │
│ (每天18:00)  │    │   (Git Agent) │    │(Task Agent) │    │(Report   │
└─────────────┘    └──────────────┘    └─────────────┘    │  Agent)  │
                                                          └────┬─────┘
                                                               │
                                                               v
                                                          ┌──────────┐
                                                          │ 飞书推送  │
                                                          │(Notify   │
                                                          │ Agent)   │
                                                          └──────────┘
```

**配置示例：**
```yaml
workflow:
  name: "每日自动日报"
  trigger: SCHEDULE
  cron: "0 0 18 * * MON-FRI"
  nodes:
    - name: "拉取代码记录"
      type: AGENT
      agentId: "git-analyzer"
    - name: "生成日报内容"
      type: AGENT
      agentId: "daily-report"
    - name: "推送消息"
      type: AGENT
      agentId: "feishu-notifier"
```

**效果：** 每天下班前自动收到飞书消息，内容是 AI 整理好的当天工作日报，你只需花 10 秒扫一眼确认即可。

---

#### 2. 团队协作：代码提交前自动审查

```
┌─────────────┐    ┌──────────────┐    ┌─────────────┐    ┌──────────┐
│ 代码 Push    │ -> │ 静态检查      │ -> │ AI 代码审查  │ -> │ 条件判断  │
│ 事件触发     │    │(Checkstyle)   │    │(CodeReview  │    │ 是否通过  │
└─────────────┘    └──────────────┘    │  Agent)     │    └────┬─────┘
                                       └─────────────┘         │
                                                               │
                                            ┌──────否────> ┌──────────┐
                                            │              │ 阻断合并  │
                                            │              │ + 通知作者│
                                            │              └──────────┘
                                            │ 是
                                            v
                                       ┌──────────┐
                                       │ 允许合并  │
                                       └──────────┘
```

**效果：** 团队成员每次 push 代码，Agent 自动跑检查 + AI 审查，发现问题直接在 PR 里评论，通过才允许合并，团队代码质量一致性大幅提升。

---

#### 3. 复杂流程：需求 → 代码 → 测试 → 部署

```
需求输入
  │
  v
┌─────────────┐    ┌──────────────┐    ┌─────────────┐    ┌──────────┐
│ 需求分析     │ -> │ 生成代码骨架   │ -> │ 补充单元测试  │ -> │ 运行测试  │
│ Agent       │    │ (CodeGen     │    │ (TestGen    │    │ (Test    │
│             │    │  Agent)      │    │  Agent)     │    │  Runner) │
└─────────────┘    └──────────────┘    └─────────────┘    └────┬─────┘
                                                               │
                                            失败 <────┬────────┘
                                                      │ 成功
                                                      v
                                               ┌──────────┐
                                               │ 生成变更  │
                                               │ 摘要 +    │
                                               │ 部署推荐  │
                                               └──────────┘
```

---

## 技术架构

```
┌─────────────────────────────────────────────────────────────────┐
│                         Agent Seek                              │
├─────────────┬─────────────┬─────────────┬───────────────────────┤
│  REST API   │  工作流引擎  │  任务调度   │     插件扩展          │
│  (agent-api)│(agent-sched)│  (Quartz)   │   (agent-plugin)      │
├─────────────┴─────────────┴─────────────┴───────────────────────┤
│                    Agent 生命周期管理器                           │
│         注册 → 启动 → 运行 → 暂停 → 停止 → 销毁                 │
├─────────────────────────────────────────────────────────────────┤
│              事件总线 / 上下文管理 / 状态机                        │
├─────────────────────────────────────────────────────────────────┤
│         Spring Boot 3  │  Java 17  │  Maven 多模块              │
└─────────────────────────────────────────────────────────────────┘
```

## 技术栈

| 层级 | 技术 |
|------|------|
| 语言 | Java 17 |
| 框架 | Spring Boot 3.2.x |
| 构建 | Maven 3.9+ |
| 任务调度 | Spring Scheduler (可扩展 Quartz) |
| 数据库 | MySQL / PostgreSQL（预留） |
| 缓存 | Redis（预留） |
| 容器化 | Docker（预留） |

## 项目结构

```
agent-seek/
├── agent-common/          # 公共模块：工具类、常量、异常、统一响应
├── agent-core/            # 核心引擎：Agent 生命周期、上下文、事件总线
├── agent-api/             # 管理后台 API：RESTful 接口、Web 控制器
├── agent-scheduler/       # 任务调度模块：工作流引擎、定时扫描、节点编排
├── agent-plugin/          # 插件扩展模块：内置插件、动态加载器
│   └── CodeReviewPlugin   # 示例：自动代码审查
│   └── DailyReportPlugin  # 示例：日报自动生成
└── agent-starter/         # 启动入口：Spring Boot 主应用 + 配置文件
```

## 快速开始

```bash
# 克隆项目
git clone git@github.com:LeeFML/agent-seek.git
cd agent-seek

# 构建
mvn clean package -DskipTests

# 启动
java -jar agent-starter/target/agent-starter-0.1.0-SNAPSHOT.jar

# 或直接在 IDE 中运行 AgentSeekApplication 主类
```

服务启动后访问：
- 健康检查：`GET http://localhost:8080/api/v1/system/health`
- Agent 列表：`GET http://localhost:8080/api/v1/agents`

## API 概览

| 方法 | 路径 | 说明 |
|------|------|------|
| POST | `/api/v1/agents` | 注册 Agent |
| GET | `/api/v1/agents/{id}` | 查询 Agent |
| GET | `/api/v1/agents` | 列出所有 Agent |
| POST | `/api/v1/agents/{id}/start` | 启动 Agent |
| POST | `/api/v1/agents/{id}/pause` | 暂停 Agent |
| POST | `/api/v1/agents/{id}/stop` | 停止 Agent |
| DELETE | `/api/v1/agents/{id}` | 销毁 Agent |
| POST | `/api/v1/workflows` | 创建工作流 |
| GET | `/api/v1/workflows` | 列出工作流 |

## 插件开发

```java
@Component
public class MyPlugin implements AgentPlugin {

    @Override
    public String getName() {
        return "my-custom-plugin";
    }

    @Override
    public String getType() {
        return "CUSTOM";
    }

    @Override
    public Object execute(Agent agent, Object input) {
        // 你的业务逻辑
        return result;
    }
}
```

将插件打成 jar 放入 `plugins/` 目录，系统会自动加载。

## 贡献指南

欢迎提交 Issue 和 Pull Request。

## License

MIT

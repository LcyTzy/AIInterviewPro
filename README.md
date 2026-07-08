# AIInterviewPro · AI 智能面试平台

> 基于大语言模型（DeepSeek）打造的智能模拟面试系统，覆盖「微信小程序用户端 + Vue3 管理后台」双端，提供简历智能解析、AI 对话式面试、多维度评估报告的完整闭环。

<p align="center">
  <img src="https://img.shields.io/badge/Java-17-orange.svg" alt="Java"/>
  <img src="https://img.shields.io/badge/Spring%20Boot-4.1-brightgreen.svg" alt="Spring Boot"/>
  <img src="https://img.shields.io/badge/Vue-3.x-42b883.svg" alt="Vue3"/>
  <img src="https://img.shields.io/badge/PostgreSQL-16-blue.svg" alt="PostgreSQL"/>
  <img src="https://img.shields.io/badge/DeepSeek-LLM-9cf.svg" alt="DeepSeek"/>
  <img src="https://img.shields.io/badge/License-MIT-lightgrey.svg" alt="License"/>
</p>

---

## 项目简介

AIInterviewPro 是一套从零构建的 **AI 驱动智能面试平台**。求职者通过微信小程序上传简历、选择应聘岗位后，即可与 AI 面试官进行真实的对话式技术面试；面试结束后系统自动生成多维度评估报告，帮助求职者查漏补缺、提升面试表现。管理端提供数据看板、面试记录、用户 / 简历 / 岗位 / 知识库管理等完整运营能力。

## 核心功能

### 用户端（微信小程序）
- **微信一键登录**：基于 `code2session` 实现小程序授权登录与新用户自动注册，支持手机号验证码登录作为备选
- **简历智能解析**：上传 PDF / Word 简历，通过 Apache Tika 提取文本，调用 DeepSeek 提炼姓名、学历、技能、项目经历等结构化信息
- **AI 对话式面试**：AI 面试官结合「岗位 + 简历 + 知识库」动态出题与追问，基于 WebFlux 流式返回，交互体验接近真人对话
- **多维度评估报告**：面试结束后自动生成逻辑能力、技术深度、岗位匹配度、沟通表达等维度评分与改进建议
- **面试记录与个人中心**：历史面试记录追溯、评估报告回看、个人数据统计

### 管理端（Vue3 + Element Plus）
- **数据看板**：ECharts 可视化展示每日面试趋势、热门岗位排行、核心运营指标
- **面试记录管理**：面试会话分页检索、对话详情与评估报告查看
- **用户管理**：用户列表、启用 / 禁用
- **简历管理**：简历分页检索、阿里云 OSS 文件预览与删除
- **岗位管理**：岗位增删改查，维护岗位描述、任职要求、面试参考题库
- **知识库管理**：按技术分类维护面试知识文档，辅助提升 AI 出题质量

## 技术栈

| 分层 | 技术选型 |
|------|----------|
| **后端框架** | Spring Boot、Spring Web、Spring WebFlux（AI 流式响应） |
| **持久层** | Spring Data JPA + PostgreSQL、HikariCP 连接池 |
| **安全认证** | Spring Security + JWT（jjwt） |
| **AI 能力** | DeepSeek 大语言模型（`deepseek-chat`） |
| **简历解析** | Apache Tika（PDF / Word 文本提取） |
| **文件存储** | 阿里云 OSS |
| **实时通信** | WebSocket / SSE 流式输出 |
| **管理后台** | Vue3 + Vite + Element Plus + Pinia + Axios + ECharts |
| **用户端** | 微信小程序原生 |
| **部署运维** | Nginx 反向代理、Docker 容器化、dev/prod 多环境配置 |

## 系统架构

```
                       ┌─────────────────────────┐
         微信小程序 ───▶│                         │
         (用户端)       │   Spring Boot 后端       │
                       │                         │
         Vue3 后台  ───▶│  Controller             │───▶ PostgreSQL
         (管理端)       │     ↓ Service           │
                       │       ↓ Repository       │───▶ 阿里云 OSS
                       │                         │
                       │   DeepSeek 大模型集成     │───▶ DeepSeek API
                       └─────────────────────────┘
```

- **三层分层架构**：`Controller → Service → Repository`，职责清晰
- **统一响应体 + 全局异常处理**：`GlobalExceptionHandler` 统一收敛异常
- **双端 JWT 鉴权**：`JwtAuthenticationFilter` + `SecurityContextHolder` 传递登录态
- **流式 AI 交互**：基于 Reactor `Flux` 实现面试问答的实时流式输出
- **AI 降级容错**：DeepSeek 调用失败时自动降级为默认题库，保障面试流程不中断

## 项目结构

```
AIInterviewPro/
├── src/main/java/com/zhenyu/
│   ├── controller/
│   │   ├── admin/          # 管理端接口（用户、简历、岗位、知识库、面试记录、统计）
│   │   └── user/           # 用户端接口（登录、简历、面试、报告、个人中心）
│   ├── service/            # 业务逻辑（DeepSeek、面试、简历解析、报告、OSS）
│   ├── repository/         # JPA 数据访问层
│   ├── entity/             # 实体类
│   ├── config/             # 安全、JWT、线程池、数据初始化等配置
│   └── exception/          # 全局异常处理
├── src/main/resources/
│   └── application.yaml    # 应用配置
├── admin/                  # Vue3 管理后台
│   └── src/
│       ├── views/          # 页面（看板、面试记录、用户、简历、岗位、知识库）
│       ├── api/            # 接口封装
│       └── router/         # 路由
└── user/                   # 微信小程序用户端
    └── pages/              # 页面（登录、首页、简历、面试、记录、报告、个人中心）
```

## 快速开始

### 环境要求
- JDK 17+
- PostgreSQL 14+
- Node.js 18+
- 微信开发者工具

### 1. 后端启动

```bash
# 配置数据库、DeepSeek、OSS 等环境变量（见下方配置说明）
# 编译并启动
mvn spring-boot:run
```

首次启动会自动执行 `DataInitializer` 初始化种子数据（管理员账号、示例用户、简历、岗位、知识库、面试记录）。

> 默认管理员账号：`admin` / `admin123`

### 2. 管理后台启动

```bash
cd admin
npm install
npm run dev
# 访问 http://localhost:5173
```

### 3. 微信小程序端

使用微信开发者工具导入 `user/` 目录，填入自己的 AppID 后编译预览。

### 配置说明

关键配置通过环境变量注入（见 [application.yaml](src/main/resources/application.yaml)），生产环境请勿硬编码密钥：

| 环境变量 | 说明 |
|----------|------|
| `DB_HOST` / `DB_PORT` / `DB_NAME` / `DB_USER` / `DB_PASSWORD` | PostgreSQL 连接配置 |
| `DEEPSEEK_API_KEY` | DeepSeek API 密钥 |
| `alioss.*` | 阿里云 OSS 配置（endpoint / accessKey / bucket） |
| `jwt.secret` | JWT 签名密钥 |

## 主要接口

| 模块 | 方法 | 路径 | 说明 |
|------|------|------|------|
| 用户登录 | POST | `/user/auth/login` | 微信一键登录 |
| 简历上传 | POST | `/user/resume/upload` | 上传并解析简历 |
| 开始面试 | POST | `/user/interview/start` | 创建面试会话 |
| 面试问答 | POST | `/user/interview/answer` | 提交回答，获取下一问（流式） |
| 评估报告 | GET | `/user/report/{sessionId}` | 获取面试评估报告 |
| 管理登录 | POST | `/admin/login` | 管理员登录 |
| 数据统计 | GET | `/admin/stats/*` | 看板统计数据 |
| 岗位管理 | GET/POST/PUT/DELETE | `/admin/job-position/*` | 岗位增删改查 |
| 知识库 | GET/POST/PUT/DELETE | `/admin/knowledge/*` | 知识文档管理 |

## License

本项目基于 [MIT License](LICENSE) 开源。

---

> 如果这个项目对你有帮助，欢迎 Star ⭐ 支持一下！

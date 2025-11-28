# 项目概览

本项目 `bit-mscloud` 是一套围绕用户认证、登录风控、搜索分析与 AI 能力构建的 Spring Cloud 微服务体系，后端采用多模块 Maven 工程组织形式。各业务模块通过 Nacos 做服务注册发现，经由 Spring Cloud Gateway 对外暴露统一入口，内部基于 OpenFeign、消息事件与 Redis 协同工作，满足高并发、可扩展、可观测的 SaaS 场景需求。

## 业务目标
- 支撑多终端用户注册与多形态登录（密码、验证码）。
- 对登录事件做设备/IP 风控，自动记录并触发安全提醒。
- 提供人员档案的多级缓存读写示例，演进出缓存旁路、互斥锁等策略。
- 维护 Elasticsearch 索引与文档的运维能力，支持双客户端（HighLevelClient 与新版 RestClient）。
- 提供 AI 聊天与分布式 ID 生成等可复用服务。

## 技术栈
- **框架**：Spring Boot 3.3.x、Spring Cloud 2023.0.2、Spring Cloud Alibaba、MyBatis-Plus、DynamicDatasource、Spring Cloud Gateway、OpenFeign。
- **中间件**：MySQL 8.0、Redis、Elasticsearch 7.16.2、RabbitMQ（预留）、Nacos、阿里云短信、SMTP 邮件。
- **公共能力**：`bit-common-*` 模块提供统一返回体 `ApiResponse`、安全注解 `@CheckLogin`、`ClientMetaInfo`、`IdGenerator` 等。
- **语言与构建**：JDK 21、Maven、Lombok、Hutool、Guava 等。

## 核心流程
1. **注册**：`ms-user` 暴露 `/user/register`，由 `RegisterStrategyDispatcher` 按邮箱/手机验证码策略写入 `user_info` 表。
2. **登录**：`ms-auth` 的 `/auth/token/login` 通过策略模式支持用户名密码、邮箱/手机号密码、验证码登录；成功后发放 JWT 样式 token，并把用户快照缓存到 Redis。
3. **登录风控**：认证服务发布 `UserLoginEvent`，监听器查询 `ms-user` 提供的 `/user/login/history` 接口，判断异地或新设备并通过 `MessageService` 邮件告警。
4. **缓存读写**：`bit-cache` 模块展示人员信息在无缓存、Redis、ConcurrentHashMap、本地缓存、旁路更新、互斥锁等多种模式下的表现。
5. **搜索服务**：`ms-elasticsearch` 通过策略接口封装酒店、房源等索引管理，暴露索引 CRUD、文档 CRUD、批量写入与查询、高亮、分页等 API。
6. **AI 服务**：`ms-ai` 使用阿里云通义千问接口实现会话式 `/ai/chat/qwen`，并暴露 `/test/generator/id` 供各服务验证 Redis 驱动的高可用 ID 生成器。

## 系统特性
- **模块化**：公共 API 与实现分离（如 `bit-auth-api` 与 `bit-auth-service`），利于 SDK 化复用。
- **策略化扩展**：注册、登录、验证码、ES 文档等均以策略模式实现，方便新增渠道或索引。
- **事件驱动**：登录事件通过 Spring `ApplicationEventPublisher` + `@Async` 解耦审计逻辑。
- **多级缓存演示**：系统性示例各类缓存穿透、击穿、旁路策略，便于教学与复用。
- **安全内控**：自定义 `X-Internal-Token` 通过 `ForwardInternalTokenInterceptor` 透传，确保内部 Feign 调用带上链路凭证。

## 目录速览
- `bit-auth`：认证、验证码、登录事件。
- `bit-user`：用户主数据、登录历史。
- `bit-elasticsearch`：索引/文档管理与查询。
- `bit-ai`：AI 聊天与 ID 生成。
- `bit-cache`：缓存策略示例。
- `bit-gateway`：统一网关与路由。
- `bit-common-*`：公共组件库。
- `doc/`：系统设计文档、Snowflake 方案以及本文档族。

后续文档将从服务职责、接口契约、数据库、配置、部署、测试、运维安全等角度进一步展开，确保新成员与协同团队能快速理解并复现整套后端能力。


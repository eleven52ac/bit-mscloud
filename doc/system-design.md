# 系统设计文档（后端微服务）

更新时间：2025-11-20

## 总体架构
- 技术栈：Spring Boot、Spring Cloud、Nacos、Spring Cloud Gateway、MyBatis-Plus、Redis、Elasticsearch、OpenFeign
- 架构风格：多模块微服务，统一网关入口，服务发现与负载均衡，统一返回体
- 关键能力：用户与认证、登录审计与告警、缓存与一致性策略、Elasticsearch 索引与查询、AI 聊天

## 模块与端口
- `ms-user` 用户服务，端口 `19902`（配置：`bit-user/src/main/resources/application.yml:1`）
- `ms-auth` 认证服务，端口 `19903`（配置：`bit-auth/src/main/resources/application.yml:1`）
- `ms-elasticsearch` ES 服务，端口 `19912`（配置含 SSL：`bit-elasticsearch/src/main/resources/application.yml:1`）
- `ms-ai` AI 服务，端口 `19901`（配置：`bit-ai/src/main/resources/application.yml:1`）
- `ms-gateway` 网关，端口 `19010`（`bit-gateway/src/main/resources/application.yml:1`）
- 其它模块：`bit-cache`（缓存策略）、`bit-common`（公共库）、`bit-api`（Feign API）、`bit-file`、`bit-job`、`bit-seckill`、`bit-other`

## 服务发现与网关
- 服务发现：Nacos（各服务 `spring.cloud.nacos.discovery` 配置）
- 网关静态路由（`bit-gateway/src/main/resources/application.yml:51`）：
  - `lb://ms-user` ← `/api/user/**`
  - `lb://ms-elasticsearch` ← `/api/es/**`（`StripPrefix=1`）
  - `lb://ms-auth` ← `/api/auth/**`（`StripPrefix=1`）

## 数据源与存储
- MySQL 动态数据源：
  - `ms-user` 连接库 `bit-mscloud`（`bit-user/src/main/resources/application.yml:16`）
  - `ms-elasticsearch` 同时配置 `hotel` 与 `prhouseserver`（`bit-elasticsearch/src/main/resources/application.yml:24`）
  - `ms-ai` 同时配置 `hotel` 与 `prhouseserver`（`bit-ai/src/main/resources/application.yml:27`）
- 动态数据源标注：`@DS("mysql")`、`@DS("mysql2")`（示例：`bit-cache/src/main/java/com/bit/cache/service/PersonsInfoService.java:15`、`PersonInfoService.java:12`）
- 分页插件：`MybatisPlusInterceptor`（`bit-common/bit-common-database/src/main/java/com/bit/common/database/config/MybatisPlusConfig.java:17`）
- Redis：`ms-auth` 配置 Redis 连接（`bit-auth/src/main/resources/application.yml:33`）
- Elasticsearch：旧版 `RestHighLevelClient` 与新版 `RestClient` 并存（`bit-elasticsearch/.../IndexController.java:21`、`37`）

## 统一返回体与状态码
- 返回体：`ApiResponse<T>`（`bit-common/bit-common-core/src/main/java/com/bit/common/core/dto/response/ApiResponse.java:19`）
- 状态码常量：`ApiStatus`（`bit-common/bit-common-core/src/main/java/com/bit/common/core/dto/response/ApiStatus.java:12`）

## 核心领域模型
- 用户信息：`com.bit.auth.entity.UserInfo`（`bit-auth/src/main/java/com/bit/auth/entity/UserInfo.java:109`）与公共模型 `com.bit.common.web.model.UserInfo`（`bit-common/bit-common-web/src/main/java/com/bit/common/web/model/UserInfo.java:109`）结构一致
- 登录历史：
  - Feign DTO：`com.bit.user.api.model.UserLoginHistoryEntity`（`bit-api/bit-api-user/src/main/java/com/bit/user/api/model/UserLoginHistoryEntity.java:14`）
  - 服务端实体与映射：`bit-user/src/main/resources/mapper/UserLoginHistoryMapper.xml:1`
- 人员信息与脱敏响应：`bit-cache` 模块（`PersonsInfoMapper.xml:1`、服务实现脱敏逻辑 `PersonsInfoServiceImpl.java:117`）

## 接口清单

### 用户服务（ms-user）
- 基础用户：
  - `POST /user/create`（`bit-user/src/main/java/com/bit/user/controller/UserInfoController.java:23`）
  - `GET /user/email?email=...`（`UserInfoController.java:29`）
  - `GET /user/username?username=...`（`UserInfoController.java:34`）
- 登录审计：
  - `GET /user/login/history/userId?userId=...`（最近 5 条，`bit-user/src/main/java/com/bit/user/controller/UserLoginHistoryController.java:34`，实现：`UserLoginHistoryServiceImpl.java:20`）
  - `POST /user/login/history/save`（`UserLoginHistoryController.java:46`）

### 认证服务（ms-auth）
- 验证码发送：`POST /singular/sms/verification`（短信/邮箱策略，`bit-auth/src/main/java/com/bit/auth/controller/LoginController.java:50`）
- 登录注册：`POST /singular/user/login`（`LoginController.java:78`）
- 线程用户信息：`GET /singular/user/threadInfo`（`@CheckLogin`，`LoginController.java:100`）
- 登录事件监听与告警：`UserLoginEventListener`（查询最近登录、判定异地/新登录地并提醒，`bit-auth/src/main/java/com/bit/auth/event/UserLoginEventListener.java:20`）

### Elasticsearch 服务（ms-elasticsearch）
- 索引：
  - `GET /index/exist/old`（旧客户端，`bit-elasticsearch/src/main/java/mselasticsearch/controller/IndexController.java:136`）
  - `GET /index/exist/new`（新客户端 HEAD 查询，`IndexController.java:154`）
- 文档与检索：
  - `POST /doc/query/old`（按策略查询，`bit-elasticsearch/src/main/java/mselasticsearch/controller/DocumentController.java:98`）
  - `POST /doc/query/new`（按策略查询，`DocumentController.java:116`）
  - `GET /doc/query/old`（全文检索，`bit-elasticsearch/src/main/java/mselasticsearch/controller/QueryDocController.java:42`）
- 结果处理与高亮：
  - `GET /dispose/pagination`（分页与排序，`bit-elasticsearch/src/main/java/mselasticsearch/controller/DisposeResultController.java:51`）
  - `GET /dispose/highlighter/old`（高亮字段渲染，`DisposeResultController.java:68`）

### 缓存服务（bit-cache）
- 人员列表分页：`POST /cache/person/list`（`bit-cache/src/main/java/com/bit/cache/controller/CacheController.java:45`）
- 缓存策略：
  - 无缓存：`GET /cache/person/nocache/id`（`CacheController.java:59`）
  - 只读事务无缓存：`GET /cache/person/readonly/id`（`CacheController.java:76`）
  - Redis 缓存：`GET /cache/person/cache/id`（`CacheController.java:91`）
  - 本地缓存：`GET /cache/person/local/cache/id`（`CacheController.java:106`）
  - 旁路缓存更新：`POST /cache/person/cache/aside`（`CacheController.java:121`）
  - 击穿互斥锁：`GET /cache/person/cache/breakdown`（`CacheController.java:134`）

### AI 服务（ms-ai）
- 通义千问聊天：`POST /ai/chat/qwen`（会话 `sessionId` 可选；实现：`bit-ai/src/main/java/com/bit/ai/controller/AiChatController.java:25`，服务实现：`AiChatServiceImpl.java:36`）
- ID 生成测试：`GET /test/generator/id`（分布式 ID，`bit-ai/src/main/java/com/bit/ai/controller/IdGeneratorController.java:22`）

## 安全设计
- 登录检查注解：`@CheckLogin`（`bit-common/bit-common-security/src/main/java/com/bit/common/security/annotation/CheckLogin.java:12`）
- 用户上下文：`UserContext`（线程级存取用户，`bit-common/bit-common-web/src/main/java/com/bit/common/web/context/UserContext.java:8`）
- JWT 工具：签发、校验、刷新（需安全管理秘钥），`JwtUtils`（`bit-common/bit-common-utils/src/main/java/com/bit/common/utils/jwt/JwtUtils.java:19`）
- 加密通信切面（预留，当前注释状态）：`EncryptCheckAspect` 与注解 `@EncryptCheck`

## 网关调用示例
- 用户服务：`GET http://<gateway-host>:19010/api/user/user/username?username=jack`
- 认证服务：`POST http://<gateway-host>:19010/api/auth/singular/user/login`
- ES 服务：`GET http://<gateway-host>:19010/api/es/index/exist/old`

## 部署与运行
- Java 版本：`21`（父 POM，`bit-mscloud/pom.xml:39`）
- Spring Boot 与 Cloud 版本统一由父 POM BOM 管理（`bit-mscloud/pom.xml:70`）
- 各服务入口：
  - 用户：`BitUserApplication`（`bit-user/src/main/java/com/bit/user/BitUserApplication.java:17`）
  - AI：`BitAiApplication`（`bit-ai/src/main/java/com/bit/ai/BitAiApplication.java:12`）
  - ES：`MsElasticsearchApplication`（`bit-elasticsearch/src/main/java/mselasticsearch/MsElasticsearchApplication.java:13`）

## 安全与合规建议
- 数据库密码、第三方 API Key、JWT 秘钥务必使用环境变量或安全配置中心管理，避免明文暴露
- 网关层建议统一鉴权与限流策略，对敏感接口使用 `@CheckLogin` 或网关 Filter
- 生产环境下适度降低日志级别，避免输出敏感信息

## 后续扩展建议
- 接入 Swagger/OpenAPI 为各服务生成自描述接口文档，统一在 `doc` 目录导出 API 说明
- 将 AI 会话上下文从内存迁移至 Redis/DB；完善会话生命周期管理
- 为缓存与 ES 增加监控与告警，结合熔断与降级策略
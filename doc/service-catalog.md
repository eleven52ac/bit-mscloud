# 服务目录与职责

| 模块 | 端口/服务名 | 核心职责 | 关键依赖 |
| --- | --- | --- | --- |
| `bit-gateway` (`ms-gateway`) | 19010 | 统一入口、路径转发、后续可扩展鉴权/限流。静态路由 `/api/{service}/**` -> `lb://ms-*`。 | Spring Cloud Gateway、Nacos |
| `bit-auth-service` (`ms-auth`) | 19903 | 登录、验证码、内部 token 透传、登录风控、消息通知。 | Redis、`bit-user` Feign、邮件/短信、Nacos |
| `bit-auth-api` | - | 对外暴露的认证 SDK（登录、验证码、枚举）。供其他服务引用。 | - |
| `bit-user-service` (`ms-user`) | 19902 | 用户注册、主数据查询、登录历史 CRUD。 | MySQL（`bit-mscloud`）、MyBatis-Plus、Nacos |
| `bit-user-api` | - | 提供 `UserInfoFeignClient`、`UserLoginHistoryFeignClient`、注册枚举等公共契约。 | - |
| `bit-elasticsearch` (`ms-elasticsearch`) | 19912 | 维护酒店/房源索引、文档 CRUD、批量导入、高亮分页等查询。 | Elasticsearch 双客户端、策略模式 |
| `bit-ai` (`ms-ai`) | 19901 | 通义千问聊天、分布式 ID 生成测试。 | 阿里云灵积 API、Redis（ID 生成）、Nacos |
| `bit-cache` | 19920（可配置） | 人员档案缓存策略演示（无缓存、Redis、本地缓存、旁路、互斥锁）。 | MySQL、Redis、MyBatis-Plus |
| `bit-common-*` | - | 公共库：统一返回体、安全注解、日志、Redis、数据库配置工具、第三方集成、ID 生成器等。 | Spring Boot Starter 形式 |
| `bit-api` | - | 各微服务的 OpenFeign API 套件，供服务间依赖。 | Spring Cloud OpenFeign |
| `bit-job` | 待定 | 预留定时/异步任务模块。 | Quartz/XXL（预留） |
| `bit-file`、`bit-seckill`、`bit-other` | 待定 | 业务扩展模块，目前代码量较少或示例性功能。 | - |

## 模块要点

### 网关 `bit-gateway`
- 静态三条路由把 `/api/user/**`、`/api/es/**`、`/api/auth/**` 分别映射到用户、ES、认证服务，剥离一级前缀。
- 预留 SSL/mTLS 配置注释，可在生产中启用。
- 与 Nacos 集群交互时指定 `secure: false`，便于区分 HTTP 与 HTTPS 服务实例。

### 认证服务 `bit-auth-service`
- 控制器：`TokenController` 提供 `/auth/token/login` 与 `/auth/token/captcha`。
- **策略扩展点**：
  - `LoginStrategyDispatcher`：注入 `LoginStrategy` 列表，支持用户名密码、邮箱密码、手机号密码、邮箱验证码、手机验证码等实现。
  - `CaptchaStrategyDispatcher`：根据 `CaptchaMethodEnum` 调用短信或邮箱验证码服务。
- 登录成功后：
  - 采用 `JWT.create()`（无签名，仅示例）生成 token；
  - 把 `UserInfoResponse` JSON 缓存到 Redis `USER_INFO_PREFIX + token`，TTL 1 小时；
  - 发布 `UserLoginEvent`，异步审查最近登录记录并可能发送邮件。
- **内控**：`ForwardInternalTokenInterceptor` 会自动把 `X-Internal-Token` 头透传到后续 Feign 调用，监听器异步线程通过 `InternalTokenContext` 维护该 token。
- **外部依赖**：Redis（登陆计数与快照）、SMTP/阿里短信（验证码与提醒）、`ms-user` Feign（查询/写入用户信息）。

### 用户服务 `bit-user-service`
- 暴露注册与登录历史相关接口，继承 `BaseController` 获得 `ClientMetaInfo` 与分页能力。
- `RegisterStrategyDispatcher` 类似认证服务的策略模式，基于 `RegisterTypeEnum` 支持手机/邮箱验证码注册。
- DAO 层使用 MyBatis-Plus，`user_info` 表字段覆盖账号、个人资料、安全控制等 40+ 列。
- 登录历史 (`user_login_history`) 供认证服务查询最近登录、写入新记录，并提供管理端分页接口。

### Elasticsearch 服务 `bit-elasticsearch`
- 采用两个 `DocumentStrategy`（酒店/房源）封装索引名、mapping 与实体转换，控制器里根据 `strategy` 参数选择执行。
- `IndexController`、`DocumentController`、`QueryDocController`、`DisposeResultController` 等分别处理索引生命周期、文档 CRUD、搜索与高亮分页。
- 双客户端：`RestHighLevelClient`（旧版）与 `RestClient`（新版），方便在迁移到 8.x API 时比对行为。

### 缓存示例 `bit-cache`
- `CacheController` 通过多个 REST 入口演示不同缓存层级，`PersonsInfoServiceImpl` 中实现命中率统计、脱敏展示等逻辑。
- 展示旁路更新 (`/person/cache/aside`) 以及缓存击穿互斥 (`/person/cache/breakdown`) 的实现，利于培训与复用。

### AI 服务 `bit-ai`
- `/ai/chat/qwen` 接入阿里云通义千问，支持可选 `sessionId`，若未传则自动生成。
- `/test/generator/id` 使用 `IdGenerator.nextId(redisTemplate)`（Redis + Lua）产出分布式 ID，可作链路雪花号来源，与 `doc/id-generator.md` 相呼应。

### 公共组件 `bit-common-*`
- `bit-common-core`：`ApiResponse`、`ApiStatus`、常量、异常抽象。
- `bit-common-web`：`BaseController`、`ClientMetaInfo`（依赖来自客户端的 `X-Client-*` 头）。
- `bit-common-utils`：`IdGenerator`、`BCryptUtils`、`JwtUtils`、`EmailSendUtils` 等。
- `bit-common-security`：`@CheckLogin`、`UserContext`、安全切面（加密、鉴权）。
- `bit-common-database`：统一分页插件、MetaObjectHandler。

### API 契约 `bit-api`
- 把 `UserInfoFeignClient`、`UserLoginHistoryFeignClient`、`IdPrefixRegionApiService` 等抽离到公共模块，消费方只需引入 API 模块即可获取接口与实体定义。

## 依赖关系
1. **入口层**：`bit-gateway` 依靠 Nacos 发现服务并转发请求。
2. **业务层**：
   - `ms-auth` <-> `ms-user`：通过 Feign 获取用户数据与写登录历史。
   - `ms-auth` -> Redis：缓存 token、登录计数。
   - `ms-user` -> MySQL：主存用户与登录数据。
   - `ms-elasticsearch` -> Elasticsearch：索引/文档管理。
   - `bit-cache` -> MySQL + Redis：展示缓存策略。
   - `ms-ai` -> Redis + 外部 AI API。
3. **公共层**：所有服务均依赖 `bit-common-*` 与 `bit-api`。

该目录可作为新成员入项或做模块拆分、需求排期时的索引。更细节的接口、数据库、配置、部署、测试与运维事项请参见同级其它文档。


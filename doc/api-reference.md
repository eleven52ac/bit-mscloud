# 接口文档（主干版本）

> 说明：以下接口均基于当前代码实现梳理，仅覆盖核心服务（认证、用户、缓存、Elasticsearch、AI）。更多模块可按此模板扩展。统一返回体为 `ApiResponse<T>`，成功状态码为 200，详见 `bit-common-core`。

## 1. 认证服务 `ms-auth`

### 1.1 登录
- **URL**：`POST /api/auth/auth/token/login`
- **请求体** `TokenRequestVo`：
  - `loginType`：`username_password`、`email_password`、`phone_password`、`phone_captcha`、`email_captcha`
  - `username` / `email` / `phone`：按登录类型选择
  - `password`：密码登录必填
  - `captcha`：验证码登录必填
- **响应**：`ApiResponse<String>`，data 为简化版 JWT token
- **逻辑摘要**：
  - 根据 `loginType` 选择策略；
  - 进行参数校验、防暴力破解、密码/验证码校验；
  - 将用户快照写入 Redis，TTL 1 小时；
  - 发布 `UserLoginEvent` 做登录审计。

### 1.2 获取验证码
- **URL**：`GET /api/auth/auth/token/captcha`
- **Query**：
  - `identifier`：手机号或邮箱
  - `captchaMethod`：`phone_captcha`、`email_captcha`
- **响应**：验证码发送结果信息字符串
- **说明**：会调用短信或邮件发送，`ClientMetaInfo` 用于记录设备/IP。

## 2. 用户服务 `ms-user`

### 2.1 用户注册
- **URL**：`POST /api/user/user/register`
- **Body** `RegisterRequestVo`：
  - `registerType`：`phone_code` / `email_code`
  - `username`、`password`（可选，视策略）、
  - `captcha`、`phoneNumber`、`email`、`referralCode` 等
- **响应**：`ApiResponse<String>`，成功返回注册确认信息
- **说明**：`RegisterStrategyDispatcher` 根据注册类型调用相应实现，内部写 `user_info` 表并联动验证码校验。

### 2.2 最近登录记录（内部调用）
- **URL**：`GET /api/user/user/login/history/userId`
- **Query**：`userId`
- **响应**：`List<UserLoginHistoryDo>`
- **用途**：供 `ms-auth` 风控查询最近 N 条登录记录。

### 2.3 保存登录记录
- **URL**：`POST /api/user/user/login/history/save`
- **Body**：`UserLoginHistoryDo`，由 `ms-auth` 写入当前登录详情
- **响应**：`ApiResponse<String>`

### 2.4 登录历史分页
- **URL**：`GET /api/user/user/login/history/list`
- **Query**：`userId`、`pageNum`、`pageSize`
- **响应**：`ApiResponse<PageDataInfo>`
- **说明**：供管理端查看登录历史。

## 3. 缓存示例服务 `bit-cache`

| Method | Path | 说明 |
| --- | --- | --- |
| `POST` | `/api/cache/cache/person/list` | 分页查询人员档案，支持姓名模糊、排序 |
| `GET` | `/api/cache/cache/person/nocache/id` | 直连数据库读取 |
| `GET` | `/api/cache/cache/person/readonly/id` | `@Transactional(readOnly=true)` 下的直连查询 |
| `GET` | `/api/cache/cache/person/cache/id` | Redis 缓存读取，含穿透保护 |
| `GET` | `/api/cache/cache/person/local/cache/id` | ConcurrentHashMap 本地缓存示例 |
| `POST` | `/api/cache/cache/person/cache/aside` | 缓存旁路（Cache-Aside）写入/更新 |
| `GET` | `/api/cache/cache/person/cache/breakdown` | 缓存击穿互斥锁示例 |
| `POST` | `/api/cache/cache/person/count` | 回传当前数据量，便于压测 |

## 4. Elasticsearch 服务 `ms-elasticsearch`

### 4.1 索引管理
- `GET /api/es/index/create/old`：使用 `RestHighLevelClient` 创建索引（需 `indexName`、`mappingTemplate`）
- `GET /api/es/index/create/new`：使用 `RestClient` PUT `/hotel`
- `GET /api/es/index/delete/old`、`/delete/new`：删除索引
- `GET /api/es/index/exist/old`、`/exist/new`：判断索引存在

### 4.2 文档 CRUD（`/api/es/doc/...`）
| Method | Endpoint | 描述 |
| --- | --- | --- |
| `POST /doc/add/old` | 旧客户端添加文档（参数：`hotelId`, `strategy`） |
| `POST /doc/add/new` | 新客户端添加文档 |
| `POST /doc/update/old` / `/update/new` | 更新文档 |
| `POST /doc/update/new/async` | 新客户端异步更新 |
| `POST /doc/delete/old` / `/delete/new` | 删除文档 |
| `POST /doc/query/old` / `/query/new` | 查询单文档 |
| `GET /doc/batch/add/old` / `/batch/add/new` | 批量导入 |

### 4.3 搜索与结果处理
- `GET /api/es/doc/query/old`：策略驱动的全文检索（在 `QueryDocController` 中）。
- `GET /api/es/dispose/pagination`：分页+排序封装。
- `GET /api/es/dispose/highlighter/old`：高亮渲染。

## 5. AI 服务 `ms-ai`

### 5.1 通义千问聊天
- **URL**：`POST /api/ai/ai/chat/qwen`
- **Query/Form**：
  - `sessionId`（可选，无则自动生成 UUID）
  - `message`：必填
- **响应**：直接返回大模型回复字符串
- **备注**：后续可扩展为流式/策略模式接入其它模型。

### 5.2 分布式 ID 生成
- **URL**：`GET /api/ai/test/generator/id`
- **响应**：`ApiResponse<Long>`
- **说明**：封装在公共 `IdGenerator`，需 Redis 作为位图+锁支持。

## 6. 共通约定
- **请求头**：
  - 需要传递客户端信息时，请携带 `X-Client-IP`、`X-Client-OS`、`X-Client-Device`、`X-Client-Region`（Base64 编码）、`X-Client-Network`、`X-Internal-Token`。
  - 网关可注入这些头，也可由终端直接上传。
- **鉴权**：
  - 当前示例未在网关统一拦截，生产需结合 `@CheckLogin` 或网关 Filter 校验 Redis token。
- **错误码**：
  - 统一由 `ApiStatus` 定义（如 200、204、400、401、403、404、500）。

> 后续新增服务/接口请在提交代码同时更新本文件，保持描述与实现一致，避免 문档漂移。


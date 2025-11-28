# 测试与质量保障

## 1. 测试策略
| 层级 | 范围 | 重点 |
| --- | --- | --- |
| 单元测试 | 策略类、工具类、事件监听 | 登录/注册策略、验证码服务、`UserLoginEventListener`、`PersonsInfoServiceImpl` 缓存逻辑 |
| 组件测试 | Feign 调用、Redis/ES 集成 | `UserInfoFeignClient`、`UserLoginHistoryFeignClient`、Redis token 缓存、ES 索引 CRUD |
| 集成测试 | REST Controller + 持久层 | `/auth/token/login`、`/user/register`、`/cache/person/cache/id` 等端到端流程 |
| 合同测试 | API 模块与服务实现 | `bit-api` 定义与 `bit-user`/`bit-auth` 实现保持一致 |
| 性能测试 | 登录、缓存、ES 查询 | 注重防暴力逻辑、Redis 锁争抢、ES 批量导入 |
| 安全测试 | 鉴权、敏感信息、风控 | Token 校验、`X-Internal-Token` 透传、暴力破解、异常登录提醒 |

## 2. 单元测试建议
- 使用 JUnit 5 + Mockito。
- 样例：
  - `UsernamePasswordLoginTest`：验证参数校验、防暴力、密码校验通过/失败分支。
  - `CaptchaStrategy` 实现的验证码生成/发送逻辑，可对第三方服务 Mock。
  - `PersonsInfoServiceImplTest`：验证缓存击穿互斥锁、旁路更新流程。
- 对时间相关逻辑可注入 `Clock` 或使用 `TimeMachine` 辅助。

## 3. 集成测试建议
- 引入 `spring-boot-starter-test`，使用 `@SpringBootTest(webEnvironment = RANDOM_PORT)`。
- 依赖容器化中间件可借助 Testcontainers：
  - MySQL、Redis、Elasticsearch 均可通过 Testcontainers 启动，避免本地环境依赖。
- 对 Feign 调用可在测试中启动 `WireMock` 模拟远程服务。

### 示例：登录接口集成测试
1. 准备用户数据（直接写入内存数据库或 H2）。
2. Mock `UserInfoApi` 返回预期用户。
3. 注入 `StringRedisTemplate`，验证 token 缓存写入。
4. 通过 `MockMvc` 调用 `/auth/token/login`，断言 HTTP 200 + token。

## 4. API 回归清单
| 模块 | 场景 | 预期 |
| --- | --- | --- |
| 认证 | 正常密码登录 | 返回 token，Redis 写入 |
| 认证 | 密码错误 6 次 | 返回账户锁定 |
| 认证 | 获取验证码 - 邮箱 | 调用邮件服务成功 |
| 用户 | 邮箱注册 + 重复邮箱 | 第一次成功，第二次返回冲突 |
| 用户 | 登录历史保存 | 返回成功并查询可见 |
| 缓存 | `/person/cache/id` | 首次落库，之后命中缓存 |
| 缓存 | `/person/cache/breakdown` 并发 | 只有一次 DB 查询 |
| ES | `/index/create/old` | 状态 200，索引存在 |
| ES | `/doc/batch/add/new` | 返回成功数 |
| AI | `/ai/chat/qwen` | 返回文本 |

## 5. 性能 / 压测
- 使用 JMeter、k6、Locust。
- 关注指标：
  - `/auth/token/login`：并发 500 下平均响应 < 150ms，Redis QPS、失败率、锁等待。
  - `/cache/person/cache/id`：命中率、Redis RTT、本地缓存一致性。
  - ES 批量导入：每秒写入量、失败重试。
- 建议在压测环境开启 APM（SkyWalking/Pinpoint/Arthas）定位瓶颈。

## 6. 安全测试
- **鉴权**：确保所有敏感接口最终会校验 token（可在网关或服务内）。
- **Header 注入**：对 `X-Client-Region` 等进行 Base64 解码异常处理，防止恶意输入。
- **暴力破解**：模拟同一用户名连续密码错误，检查锁定与解锁逻辑。
- **敏感信息**：抓包确认日志与响应不泄露邮箱授权码、短信 AKSK。
- **内部 token**：验证 `ForwardInternalTokenInterceptor` 功能，避免丢失内网凭证。

## 7. 提交流程
1. 代码提交前跑 `mvn test`（至少涉及修改模块）。
2. 对公共模块/接口改动需同步更新本目录文档与 `doc/api-reference.md`。
3. 重要改动走 MR/PR，附加测试报告（可截图 Jenkins/JUnit 报告）。

## 8. 自动化建议
- 接入 CI 工具（GitHub Actions/GitLab CI/Jenkins）：
  - 步骤：Checkout → 缓存 Maven → `mvn verify` → 生成测试报告 → 归档。
  - 可在流水线条件满足后自动构建 Docker 镜像。
- 接入 SonarQube 做静态扫描（代码异味、重复、覆盖率）。

> 随着业务扩展，建议建立测试用例追踪表（TestRail、禅道等）并对关键流程（支付、风控、AI 调用）配置监控式自动化脚本，确保每次发版前均可一键执行。


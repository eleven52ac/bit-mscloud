# 配置指南

> 本指南整理运行后端各服务所需的配置项与敏感信息管理方式，便于本地、测试、生产环境快速落地。

## 1. 全局要求
- JDK 21、Maven 3.9+。
- Nacos 地址：`100.97.223.54:8848`（默认 public namespace，可抽离到环境变量 `NACOS_ADDR`）。
- 所有服务需在启动前确保：
  - `spring.application.name` 唯一；
  - `spring.cloud.nacos.discovery.*` 正确；
  - `logging.level` 根据环境调整（生产建议 INFO）。
- 建议使用 `application-<env>.yml` 或配置中心管理环境差异，当前代码将部分敏感信息写死，仅供开发验证。

## 2. 认证服务 `ms-auth`

| 配置项 | 说明 |
| --- | --- |
| `server.port=19903` | 服务端口 |
| `spring.mail.*` | SMTP（163 邮箱示例），包括授权码 |
| `spring.data.redis.host=100.97.223.54` | Redis 主机，`database=2` |
| `alibaba.*` | 阿里云短信签名、模板、AK/SK |
| `ai.qwen.api-key` | 通义千问 API Key，当前硬编码 |

**建议**：把邮件、Redis、短信、AI 等敏感信息改为环境变量或 Nacos 配置，避免提交到仓库。可通过 `@ConfigurationProperties` + `--D` 参数或 Docker/K8S Secret 注入。

## 3. 用户服务 `ms-user`

| 配置项 | 说明 |
| --- | --- |
| `server.port=19902` | 服务端口 |
| `spring.datasource.dynamic.datasource.master.*` | MySQL 连接（URL、用户名、密码） |
| `mybatis-plus.mapper-locations=classpath:/mapper/*Mapper.xml` | Mapper 扫描 |
| `logging.level.org.springframework.web=DEBUG` | 开发调试用 |

**安全建议**：数据库凭证仅用于开发环境，生产请走配置中心+密钥管理（KMS/Vault），并启用只读账号。

## 4. Elasticsearch 服务

| 配置 | 说明 |
| --- | --- |
| `server.port=19912` | 端口（示例） |
| `elasticsearch.oldElasticsearchClient` | 通过 `RestHighLevelClient` 连接老集群 |
| `elasticsearch.newElasticsearchClient` | 使用 `RestClient` 指定 HTTPS/认证等 |
| `mapper-locations=classpath:/mapper/*.xml` | 若组合 MyBatis |

**SSL**：`bit-elasticsearch/src/main/resources/application.yml` 中预置了 keystore/truststore，可用于 mTLS。

## 5. AI 服务 `ms-ai`

- `spring.datasource.dynamic`：同时配置 `hotel`、`prhouseserver`，便于读取不同库。
- `spring.data.redis.*`：用于 `IdGenerator`。
- `ai.qwen.*`：同认证服务，可共享配置。

## 6. 缓存服务 `bit-cache`

- 需配置 MySQL（保障人员库）与 Redis（缓存/互斥锁）连接。
- `PersonsInfoServiceImpl` 中的脱敏规则依赖配置 `@Value`（可扩展黑名单）。

## 7. 网关 `bit-gateway`

| 配置 | 说明 |
| --- | --- |
| `server.port=19010` | 网关端口 |
| `spring.cloud.gateway.routes` | 静态路由三条，StripPrefix=1 |
| `spring.cloud.gateway.httpclient.ssl.*` | 预留下游 mTLS |
| `logging.level.org.springframework.cloud.gateway=DEBUG` | 可调 |

**头部传递**：若后端需要 `X-Client-*` 元信息，可在网关 Filter 注入；也可由终端直接传输。

## 8. 通用配置

### 8.1 `ClientMetaInfo` 头说明
| Header | 示例 | 说明 |
| --- | --- | --- |
| `X-Client-IP` | `203.0.113.10` | 真实 IP |
| `X-Client-OS` | `iOS 18.1` | 操作系统 |
| `X-Client-Device` | `iPhone 15 Pro` | 设备标识 |
| `X-Client-Region` | `5pel5pys5LiA5Y2X`（Base64: 上海市） | 区域需 Base64 编码 |
| `X-Client-Network` | `WiFi/CMCC` | 网络 |
| `X-Internal-Token` | `internal-123` | 服务内部调用凭证（由网关或上游发放） |

### 8.2 日志
- 可通过 `logging.config` 指定 logback 配置，当前默认控制台输出 + `logs/*.log`。
- 建议按模块拆分日志目录，结合 ELK/EFK 采集。

### 8.3 运行参数模板
```bash
java -jar bit-auth-service.jar \
  --spring.profiles.active=dev \
  --spring.cloud.nacos.discovery.server-addr=${NACOS_ADDR} \
  --spring.data.redis.password=${REDIS_PWD} \
  --alibaba.accessKeyId=${SMS_AK} \
  --alibaba.accessKeySecret=${SMS_SK}
```

## 9. 敏感信息治理
- 建议使用以下策略：
  1. **配置中心**：Nacos / Apollo / Consul；
  2. **密钥管理**：KMS、Vault、Secrets Manager；
  3. **密文文件**：`application-secret.yml` + `git-crypt`；
  4. **容器注入**：Kubernetes Secret、Docker 环境变量。
- 对仓库中已存在的明文（邮箱授权码、Redis 密码、AI Key 等）应尽快替换并吊销旧凭证。

## 10. 环境矩阵建议

| 环境 | 特性 |
| --- | --- |
| `dev` | 本地单机，依赖 Docker Compose 的 MySQL/Redis/Elasticsearch |
| `test` | 与 QA 共用 Nacos namespace，启用 Mock 短信/邮箱 |
| `pre` | 与生产等同配置，开启 SSL、限流、鉴权 |
| `prod` | 多实例部署，启用 mTLS、集中日志、APM、熔断 |

> 配置文件变更需和 DevOps 同步，确立变更审批流程，避免上线后才发现密钥或端口错误。


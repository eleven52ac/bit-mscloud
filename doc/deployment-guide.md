# 部署指南

## 1. 准备工作
- **运行环境**：JDK 21、Maven 3.9+、Git、Docker（可选）。
- **基础设施**：MySQL、Redis、Elasticsearch、Nacos、SMTP/短信服务、可选 RabbitMQ。
- **系统账户**：创建最少权限账号（DB 读写、Redis 命名空间、ES 角色等）。
- **构建方式**：统一用 Maven。根目录执行 `mvn -T 1C clean install -DskipTests` 可一次性构建全部模块。

## 2. 构建产物
1. **全量构建**
   ```bash
   mvn clean package -DskipTests
   ```
   - 产物位于各子模块 `target/*.jar`。
2. **单服务构建**
   ```bash
   mvn -pl bit-auth/bit-auth-service -am spring-boot:repackage -DskipTests
   ```
   `-pl` 指定模块，`-am` 自动构建依赖。

## 3. 启动顺序
建议按照「基础设施 → 网关依赖服务 → 网关」顺序：
1. **基础服务**：MySQL、Redis、Elasticsearch、Nacos、SMTP/短信、AI Key、（可选）RabbitMQ。
2. **公共组件**：无需单独运行，作为依赖打入。
3. **业务服务**：
   - `ms-user`
   - `ms-auth`
   - `ms-elasticsearch`
   - `ms-ai`
   - `bit-cache`（如需）
4. **边缘层**：`ms-gateway`

> 若有前端或其它调用方，请在服务全部注册进 Nacos 后再放开流量。

## 4. 运行命令模板
```bash
java -Xms512m -Xmx512m \
  -Dspring.profiles.active=prod \
  -Dspring.cloud.nacos.discovery.server-addr=${NACOS_ADDR} \
  -jar bit-auth-service.jar
```

### 常用 JVM / Spring 参数
- `-Dspring.cloud.nacos.discovery.namespace=prod`
- `-Dspring.cloud.nacos.discovery.username=xxx`
- `-Dspring.config.import=nacos:ms-auth.yaml?group=DEFAULT_GROUP`
- `-Dspring.data.redis.password=***`
- `-Dlogging.file.name=/data/logs/ms-auth.log`

## 5. 容器化（示例）
```dockerfile
FROM eclipse-temurin:21-jre
WORKDIR /opt/app
COPY target/bit-auth-service.jar app.jar
ENV JAVA_OPTS="-Xms512m -Xmx512m"
ENTRYPOINT ["sh","-c","java $JAVA_OPTS -jar app.jar"]
```

部署到 Kubernetes 时：
- 使用 `ConfigMap` 注入通用配置，`Secret` 注入敏感变量。
- `livenessProbe` 与 `readinessProbe` 指向 `/actuator/health`（需要在 `application.yml` 开启 `management.endpoints.web.exposure.include=health,info`）。
- 设置 `resources.requests/limits`，并开启 `HorizontalPodAutoscaler`。

## 6. 数据迁移
- 推荐使用 Flyway/Liquibase 管理 `user_info`、`user_login_history` 等表结构。
- 部署前执行 `mvn -pl bit-user/bit-user-service flyway:migrate -Dflyway.configFiles=...`（如已集成）。
- 缓存示例模块仅演示用途，可通过 SQL 脚本预置数据。

## 7. 验证清单
1. `curl http://<gateway>:19010/api/user/user/register`（可 Mock 请求体）返回 200。
2. `curl http://<gateway>:19010/api/auth/auth/token/login` 成功返回 token。
3. `curl http://<gateway>:19010/api/auth/auth/token/captcha?...` 能发送验证码。
4. `curl http://<gateway>:19010/api/user/user/login/history/list?userId=1` 返回分页数据。
5. `curl http://<gateway>:19010/api/es/index/exist/old` 返回“索引存在/不存在”。
6. `curl http://<gateway>:19010/api/ai/ai/chat/qwen` 返回大模型文本。
7. `curl http://<gateway>:19010/api/cache/cache/person/cache/id?id=1` 返回缓存结果。

## 8. 回滚策略
- **蓝绿 / 灰度**：先在新实例验证，Nacos 切流量，再下线旧实例。
- **数据库**：升级前创建快照或备份；如涉及 DDL，准备回滚脚本。
- **配置**：采用版本化配置中心，可快速回退。

## 9. 常见问题
| 问题 | 排查建议 |
| --- | --- |
| 服务未注册到网关 | 检查 `spring.cloud.nacos.discovery`、网络安全组 |
| 登录一直失败 | 查看 Redis 是否可用、防暴力计数是否清零 |
| 登录提醒不发送 | 检查 SMTP 凭证、`EmailSendUtils` 日志 |
| ES API 报 4xx | 确认索引存在、策略参数 `strategy` 是否正确 |
| AI 接口报 429 | 确认通义千问 QPS 配额，必要时添加重试/限流 |

> 部署脚本与 CI/CD（如 GitLab CI、GitHub Actions、Jenkins Pipeline）可在此基础上扩展，实现自动化构建、镜像推送、灰度发布等能力。


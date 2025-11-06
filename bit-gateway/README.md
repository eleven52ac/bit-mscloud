# Gateway Service 网关服务

## 简介

Gateway Service 是基于 Spring Cloud Gateway 构建的微服务网关，提供路由转发、服务发现、JWT认证、限流等功能。

## 功能特性

- ✅ **服务发现与路由**：集成 Nacos，自动发现后端服务并路由转发
- ✅ **JWT认证**：全局JWT token验证，保护需要认证的接口
- ✅ **跨域支持**：全局CORS配置，支持跨域请求
- ✅ **日志记录**：自动记录所有请求和响应日志
- ✅ **异常处理**：统一异常响应格式
- ✅ **限流支持**：基于IP的限流配置（可选）

## 项目结构

```
bit-gateway-service/
├── src/
│   └── main/
│       ├── java/
│       │   └── msgateway/
│       │       ├── MsGatewayApplication.java    # 启动类
│       │       ├── config/
│       │       │   ├── CorsConfig.java          # 跨域配置
│       │       │   └── GatewayConfig.java       # 网关配置（限流等）
│       │       ├── filter/
│       │       │   ├── GlobalLogFilter.java     # 全局日志过滤器
│       │       │   └── JwtAuthenticationFilter.java # JWT认证过滤器
│       │       └── exception/
│       │           └── GlobalExceptionHandler.java  # 全局异常处理
│       └── resources/
│           ├── application.yml                   # 应用配置
│           └── bootstrap.yml                    # Nacos配置
└── pom.xml
```

## 配置说明

### 端口配置
- 默认端口：`8080`
- 可在 `application.yml` 中修改

### Nacos配置
- 默认地址：`localhost:8848`
- 可在 `application.yml` 或 `bootstrap.yml` 中修改

### 路由配置

当前已配置的路由：
- **elasticsearch-service**: `/api/elasticsearch/**` → `lb://spring-cloud-es`

添加新路由示例：
```yaml
routes:
  - id: user-service
    uri: lb://user-service
    predicates:
      - Path=/api/user/**
    filters:
      - StripPrefix=2
```

### JWT认证

JWT认证过滤器会自动验证所有非白名单路径的请求。

**白名单路径**（不需要认证）：
- `/api/auth/**` - 认证相关接口
- `/api/public/**` - 公共接口
- `/actuator/**` - 监控端点

**Token格式**：
```
Authorization: Bearer <token>
```

## 启动步骤

1. **启动 Nacos 服务**
   ```bash
   # 确保Nacos运行在 localhost:8848
   ```

2. **启动后端服务**
   ```bash
   # 启动 elasticsearch-service 等其他微服务
   ```

3. **启动网关服务**
   ```bash
   cd bit-gateway-service
   mvn spring-boot:run
   ```

4. **验证服务**
   ```bash
   # 访问网关
   curl http://localhost:8080/api/elasticsearch/...
   ```

## 限流配置（可选）

如需启用限流功能，需要添加 Redis 依赖：

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-data-redis-reactive</artifactId>
</dependency>
```

然后在 `application.yml` 中启用限流配置（已注释的示例）。

## 注意事项

1. Gateway 使用 WebFlux，不能使用 `spring-boot-starter-web`
2. 确保 Nacos 服务正常运行
3. JWT密钥配置在 `bit-common` 模块的 `JwtUtil` 中
4. 修改白名单路径需要修改 `JwtAuthenticationFilter` 中的 `WHITE_LIST`

## 开发建议

1. **添加新服务路由**：在 `application.yml` 中添加路由配置
2. **自定义过滤器**：实现 `GlobalFilter` 接口
3. **修改认证规则**：编辑 `JwtAuthenticationFilter`
4. **调整日志级别**：修改 `application.yml` 中的 `logging.level` 配置


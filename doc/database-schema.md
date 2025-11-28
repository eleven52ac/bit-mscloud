# 数据库设计

> 当前代码涉及 MySQL 数据源 `bit-mscloud`（用户域）与示例库 `prhouseserver`（保障人员），本文件按照实体与 MyBatis 映射梳理核心表结构。字段类型根据实体推断，实际以建表 SQL 为准。

## 1. `user_info`（用户主数据）

| 字段 | 类型 | 说明 |
| --- | --- | --- |
| `user_id` (PK) | BIGINT AUTO_INCREMENT | 用户唯一 ID |
| `username` | VARCHAR(64) | 唯一登录名（允许字母、数字、`_-.@`） |
| `password` | VARCHAR(128) | `BCryptUtils` 加密后的密码，可为空（验证码注册） |
| `phone_number` | VARCHAR(32) | 手机号 |
| `email` | VARCHAR(128) | 邮箱 |
| `first_name` / `last_name` | VARCHAR(64) | 姓名 |
| `gender` | TINYINT / CHAR | 性别（可扩展枚举） |
| `date_of_birth` | DATE | 出生日期 |
| `avatar_url` | VARCHAR(255) | 头像 |
| `signature` | VARCHAR(255) | 个性签名 |
| `address` | VARCHAR(255) | 地址 |
| `created_at` / `updated_at` | DATETIME | 审计字段 |
| `is_active` | TINYINT | 状态标记 |
| `is_deleted` | TINYINT | 逻辑删除 |
| `last_login` | DATETIME | 最近登录时间 |
| `login_count` | INT | 登录次数 |
| `role` | VARCHAR(64) | 角色（可配合 RBAC） |
| `failed_login_attempts` | INT | 连续失败次数（Redis 亦存冗余） |
| `lock_time` | DATETIME | 锁定截至时间 |
| `password_reset_token` / `password_reset_expires` | VARCHAR / DATETIME | 重置密码凭证 |
| `created_by` / `updated_by` | BIGINT | 操作人 |
| `facebook_id` / `google_id` / `twitter_id` | VARCHAR(128) | 第三方账号绑定 |
| `last_activity` | DATETIME | 最近活跃时间 |
| `total_spent` | DECIMAL(18,2) | 累计消费 |
| `membership_level` | VARCHAR(32) | 会员等级 |
| `is_verified` | TINYINT | 邮箱/手机验证状态 |
| `is_banned` | TINYINT | 是否封禁 |
| `ban_reason` | VARCHAR(255) | 封禁原因 |
| `locale` / `timezone` | VARCHAR(32) | 语言与时区 |
| `birthplace` | VARCHAR(128) | 出生地 |
| `referral_code` | VARCHAR(64) | 推荐码 |

**索引建议**：
- `UNIQUE (username)`、`UNIQUE (email)`、`UNIQUE (phone_number)`；
- 组合索引 `(is_deleted, is_active)` 便于后台查询；
- `(last_login)` or `(updated_at)` 支持排序。

## 2. `user_login_history`（登录记录）

| 字段 | 类型 | 说明 |
| --- | --- | --- |
| `id` (PK) | BIGINT AUTO_INCREMENT | 主键 |
| `user_id` | BIGINT | 关联 `user_info.user_id` |
| `ip` | VARCHAR(64) | 登录 IP |
| `country`/`province`/`city` | VARCHAR(64) | 地理信息（来自 IP 库） |
| `region` | VARCHAR(255) | 原始地区字符串 |
| `isp` | VARCHAR(64) | 运营商 |
| `device` | VARCHAR(128) | 设备 |
| `os` | VARCHAR(64) | 操作系统 |
| `browser` | VARCHAR(64) | 浏览器 |
| `network` | VARCHAR(255) | 网络详情 JSON |
| `login_time` | DATETIME | 登录时间 |
| `logout_time` | DATETIME | 登出时间 |
| `login_result` | TINYINT | 0=失败，1=成功 |
| `is_suspicious` | TINYINT | 1=异常 |
| `remark` | VARCHAR(255) | 异常说明 |

**索引建议**：
- `INDEX idx_user_time (user_id, login_time DESC)` 用于最近登录查询；
- `INDEX idx_ip (ip)` 便于风控统计。

## 3. `person_info`（缓存示例-人员表）

| 字段 | 类型 | 说明 |
| --- | --- | --- |
| `id` (PK) | BIGINT AUTO_INCREMENT |
| `name` | VARCHAR(64) |
| `age` | INT |
| `birthday` | DATE / VARCHAR |
| `gender` | CHAR(1) |
| `register` | VARCHAR(128) | 户籍 |
| `id_card` | VARCHAR(32) |
| `is_delete` | TINYINT |

**用途**：`PersonInfoService` 在缓存击穿示例中使用，配合 Redis + 本地缓存验证不同策略表现。

## 4. `prh_protected_persons_info`（保障人员档案）

字段较多，主要涵盖家庭编号、证件、收入、户籍、银行、数据来源等信息，参见 `PersonsInfoEntity`。建议重点索引：
- `family_code`、`person_code`、`card_number` 唯一性索引；
- `person_status`、`data_source` 组合索引支撑筛选；
- `data_time` 排序索引便于增量同步。

## 5. Redis Key 约定

| Key 前缀 | 用途 | 说明 |
| --- | --- | --- |
| `login:attempt:` (`LOGIN_ATTEMPT_PREFIX`) | 登录失败计数 | >5 次触发临时锁 |
| `login:user:` (`USER_INFO_PREFIX`) | token -> 用户快照 | TTL 1 小时 |
| `id:generator:` | 分布式 ID 雪花位存储 | 详见 `doc/id-generator.md` |
| `cache:person:` | 人员信息缓存 | 区分 Redis、本地缓存 |

## 6. 数据源配置
- `ms-user`：动态数据源 `master` 指向 `jdbc:mysql://100.97.223.54:3306/bit-mscloud`。
- `bit-ai` / `bit-elasticsearch`：示例中还配置了 `hotel`、`prhouseserver` 数据源，可扩展读写分离。

> 建议把建表 SQL、索引、DDL 版本管理纳入数据库迁移工具（Flyway/Liquibase），并在提交新字段或新表时同步此文档。


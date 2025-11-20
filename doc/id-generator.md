# 全局唯一 ID 生成器说明文档

更新时间：2025-11-20

## 背景与目标
- 在分布式系统中，需要高性能、强唯一、有序的全局 ID。
- 本项目采用 Twitter Snowflake 思想实现的本地生成方案，并在主生成失败时引入 Redis 自增容灾兜底。

## 代码位置
- 入口工具类：`bit-common/bit-common-utils/src/main/java/com/bit/common/utils/core/IdGenerator.java`
- 雪花实现：`bit-common/bit-common-utils/src/main/java/com/bit/common/utils/core/SnowflakeIdGenerator.java`

## 设计概要
- 主生成方式：Snowflake 64 位有序 ID，包含时间戳、数据中心 ID、工作节点 ID、序列号。
- 容灾兜底：当本地 Snowflake 生成失败时，使用 Redis `INCR` 在“秒粒度”键上取序号，组合毫秒时间戳与序列形成唯一 ID。
- 节点识别：`workerId` 基于本机 IP 哈希，`datacenterId` 基于主机名哈希，均限制在 `0~31`。
- 并发与时钟处理：`synchronized` 保证并发安全，序列溢出等待下一毫秒，小范围时钟回拨等待，大范围回拨直接拒绝生成。

## ID 位结构
- 定义与位宽（`SnowflakeIdGenerator.java:37-41`）：
  - `1 bit` 保留位（固定为 0）
  - `41 bit` 时间戳（毫秒）
  - `5 bit` 数据中心 ID（`datacenterId`）
  - `5 bit` 工作节点 ID（`workerId`）
  - `12 bit` 序列号（同毫秒内自增）
- 起始纪元（`twepoch`）：`2023-01-01 00:00:00 UTC`（`SnowflakeIdGenerator.java:55`），41 位可用约 69 年。

## 关键实现
- 单例与懒加载（DCL）：`IdGenerator#getSnowflakeInstance`（`IdGenerator.java:115-130`）
- Worker/DataCenter 自动分配：
  - `workerId` ← 本机 IP 哈希（`IdGenerator.java:136-145`，`SnowflakeIdGenerator.java:254-263`）
  - `datacenterId` ← 主机名哈希（`IdGenerator.java:152-161`，`SnowflakeIdGenerator.java:277-286`）
- 雪花生成核心（`SnowflakeIdGenerator#nextId`，`SnowflakeIdGenerator.java:177-209`）：
  - 同毫秒内序列号 `12 bit` 自增，溢出等待下一毫秒
  - 小于上次时间戳的情况（时钟回拨）：
    - 回拨 ≤ 5ms：等待到新毫秒（`SnowflakeIdGenerator.java:183-185`）
    - 回拨 > 5ms：抛出异常拒绝生成（`SnowflakeIdGenerator.java:186-188`）

## Redis 容灾方案
- 入口方法：`IdGenerator#nextId(StringRedisTemplate)`（`IdGenerator.java:49-79`）
- 逻辑：
  - 主路径：调用雪花 `nextId()` 生成（`IdGenerator.java:51-53`）
  - 异常时进入 Redis 兜底：
    - 高位：当前毫秒时间戳（`IdGenerator.java:59`）
    - 低位：该“秒”键上的自增序号（`INCR`），并设置 TTL（`IdGenerator.java:61-68`）
    - 组合：`fallbackId = (timestamp << 20) | (seq % (1 << 20))`（`IdGenerator.java:70`）
  - Redis 连接失败：抛出运行时异常提示不可用（`IdGenerator.java:73-77`）

## 容量与性能参考
- 单节点每毫秒最多 `4096` 个 ID（12 位序列）。
- 理论单机每秒可达 `~409.6 万`，实际受时钟与锁影响，工程稳定在 `10~30 万 QPS`。
- 节点规模上限：`32` 数据中心 × `32` 工作节点，共 `1024` 组合（`SnowflakeIdGenerator.java:58-85`）。

## 使用方式
- 无 Redis 容灾（本地雪花）：
  - `long id = IdGenerator.nextId();`（`IdGenerator.java:92-95`）
- 启用 Redis 容灾：
  - `long id = IdGenerator.nextId(redisTemplate);`（`IdGenerator.java:49-79`）
- 项目示例：`ms-ai` 提供了测试接口（`bit-ai/src/main/java/com/bit/ai/controller/IdGeneratorController.java:22`）。

## 部署与配置建议
- 稳定分配 `workerId` 与 `datacenterId`：
  - 通过环境变量或 JVM 参数注入（例如 `-Dsnowflake.workerId=7 -Dsnowflake.datacenterId=3`），替换自动哈希兜底方式。
- 时钟同步：启用 NTP，避免显著时钟回拨导致生成失败。
- Redis 容灾键管理：按“秒粒度”创建并设置 TTL，避免键累积；根据业务吞吐适当调整位宽（例如 `seq` 20 bit 的取模）。
- 异常监控：对“回拨 > 5ms”与 Redis 连接失败进行告警。

## 典型坑位与规避
- 时钟回拨：务必监控并在大回拨时降级外部序列或暂缓写入。
- 节点 ID 冲突：同环境内务必保证 `(datacenterId, workerId)` 唯一，否则可能产生日志异常与 ID 碰撞风险。
- 热点毫秒序列溢出：极端峰值可能出现等待下一毫秒的短暂阻塞，可结合业务做异步缓冲或批量分配。

## 扩展方向
- 支持外部注册中心或配置中心统一分配节点 ID。
- 增加“批量预取”与 RingBuffer 提升序列申请吞吐。
- 提供二进制解析工具，将 `long` ID 解析为各字段便于审计与追踪。
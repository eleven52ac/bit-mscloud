# 全局唯一 ID 生成器说明文档

更新时间：2025-11-27

## 背景与目标
- 在分布式系统中，需要高性能、强唯一、有序的全局 ID。
- 本项目采用 Twitter Snowflake 思想实现的本地生成方案，并在主生成失败时引入 Redis 自增容灾兜底。

## 代码位置
- 入口工具类：`bit-common/bit-common-utils/src/main/java/com/bit/common/utils/core/IdGenerator.java`
- 雪花实现：`bit-common/bit-common-utils/src/main/java/com/bit/common/utils/core/SnowflakeIdGenerator.java`

## 设计概要
- **主生成方式**：`SnowflakeIdGenerator` 生成 64 位有序 ID（41 位时间戳 + 5 位数据中心 + 5 位工作节点 + 12 位序列）。
- **容灾兜底**：`IdGenerator#nextId(StringRedisTemplate)` 在 Snowflake 抛出异常时切换 Redis `INCR` 的 fallback。
- **节点标识**：
  - 优先从环境变量 `SNOWFLAKE_WORKER_ID` / `SNOWFLAKE_DATACENTER_ID` 或 JVM 参数 `-Dsnowflake.workerId` / `-Dsnowflake.datacenterId` 读取；
  - 若未配置则分别使用本机 IP、主机名的哈希值（低 5 bit），再不行退回随机数/默认值。
- **并发与时钟**：`nextId()` 为同步方法；同毫秒序列溢出等待下一毫秒；时钟回拨 ≤5ms 自旋等待，>5ms 直接抛异常提醒调用方。
- **容灾策略**：Redis fallback 以“秒粒度” key 存储递增序列，TTL 60s，最终 ID 由 `timestamp<<20 | (seq mod 2^20)` 组成，确保临时唯一性。
- **封装方式**：`IdGenerator` 为最终工具类（final + 私有构造），提供带/不带 Redis 的静态方法，内部懒加载雪花单例。

## ID 位结构
- `1 bit` 保留位（固定 0，保证为正数）
- `41 bit` 时间戳（毫秒，起始纪元 `2023-01-01 00:00:00 UTC`，可使用约 69 年）
- `5 bit` 数据中心 ID (`datacenterId`)
- `5 bit` 工作节点 ID (`workerId`)
- `12 bit` 序列号（同毫秒内 0~4095）
- 位移定义：
  - `timestampLeftShift = 22`
  - `datacenterIdShift = 17`
  - `workerIdShift = 12`
  - `sequenceMask = 0xFFF`

## 关键实现
- **单例懒加载**：`IdGenerator#getSnowflakeInstance()` 采用 DCL，首次创建时调用 `SnowflakeIdGenerator.getInstance()` 并记录初始化日志。
- **Worker / Datacenter 分配**：
  1. 读取环境变量 `SNOWFLAKE_WORKER_ID`、`SNOWFLAKE_DATACENTER_ID`;
  2. 读取 JVM 参数；
  3. 兜底：IP、主机名哈希取低 5 bit；若失败则随机/默认。
  4. 校验必须落在 `[0,31]`，否则警告并回退兜底策略。
- **雪花序列生成**：
  - `synchronized long nextId()`；
  - 同毫秒自增序列，`sequenceMask` 保证溢出清零并等待下一毫秒；
  - 时钟回拨分两段处理；
  - 最终 ID 通过位移拼接 `(timestamp - twepoch)<<22 | datacenter<<17 | worker<<12 | sequence`。

## Redis 容灾方案
- 入口方法：`IdGenerator#nextId(StringRedisTemplate redisTemplate)`
- 常量：
  - Fallback Key：`ID_GENERATOR_FALLBACK:`
  - TTL：`60s`
- 流程：
  1. 先调用 `getSnowflakeInstance().nextId()`；
  2. 捕获任何异常后记录错误并进入 fallback；
  3. fallback 取 `timestampPart = System.currentTimeMillis()`；
  4. 使用 `timestampPart / 1000` 作为 key 后缀执行 `INCR`；
  5. 对 key 设置 TTL，防止秒级 key 堆积；
  6. 组装 `fallbackId = (timestampPart << 20) | (seq & ((1<<20)-1))`；
  7. Redis 连接失败则抛出 `RuntimeException` 告知整体不可用。
- 适用场景：极少数雪花异常（时钟回拨过大、worker 初始化失败等）时保证业务可继续写入。

## 容量与性能参考
- 单节点每毫秒最多 `4096` 个 ID（12 位序列）。
- 理论单机每秒可达 `~409.6 万`，实际受时钟与锁影响，工程稳定在 `10~30 万 QPS`。
- 节点规模上限：`32` 数据中心 × `32` 工作节点，共 `1024` 组合（`SnowflakeIdGenerator.java:58-85`）。

## 使用方式
- **直接雪花**：
  ```java
  long id = IdGenerator.nextId();
  ```
- **带 Redis 容灾**：
  ```java
  @Autowired
  private StringRedisTemplate redisTemplate;

  long id = IdGenerator.nextId(redisTemplate);
  ```
- **接口示例**：`ms-ai` 暴露 `/test/generator/id`，可用于联调。

## 部署与配置建议
- **唯一节点 ID**：为每个实例固定配置 `SNOWFLAKE_WORKER_ID`、`SNOWFLAKE_DATACENTER_ID` 或 JVM 参数，避免依赖 IP/主机名哈希导致漂移。
- **时钟同步**：所有节点配置 NTP/NTPsec，感谢 `nextId()` 对 >5ms 回拨直接报错，需监控。
- **Redis 权限**：fallback 使用的 Redis 库需具备 `INCR` + `EXPIRE` 权限；如需更高吞吐可调大低位位宽或改为 Lua 自定义逻辑。
- **监控告警**：
  - Snowflake 初始化失败 / 回拨异常；
  - Redis fallback 使用频率；
  - Redis fallback 键数量、延迟。

## 典型坑位与规避
- **时钟回拨**：超过 5ms 直接抛异常，建议结合监控自动切换到 Redis fallback（或暂停写入）并触发告警。
- **节点 ID 冲突**：容器自动扩缩时如无固定 workerId，可能由相同 IP 哈希导致冲突，务必通过配置或 StatefulSet 保证唯一。
- **热点序列溢出**：单节点同毫秒并发 >4096 会阻塞，建议横向扩容或引入批量预取方案。
- **Redis fallback 抖动**：当 fallback 被频繁触发时要尽快定位 Snowflake 异常原因（如 JVM 时钟、网络时间同步、workerId 配置问题）。

## 扩展方向
- 引入配置中心/注册中心统一分配节点 ID。
- 扩展 RingBuffer/Disruptor 方案，提升超高并发下的单节点吞吐。
- 提供 ID 解析与回溯工具（拆解时间戳/节点/序列），便于审计。
- 根据业务需要增加多 Region 容灾（如 workerId 映射外部唯一编号表）。
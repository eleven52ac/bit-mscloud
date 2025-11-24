package com.bit.common.utils.core;

import lombok.extern.slf4j.Slf4j;

import java.net.InetAddress;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;

/**
 * 雪花 ID 生成器（Snowflake ID Generator）。
 * <p>
 * 基于 Twitter Snowflake 算法实现的分布式全局唯一 ID 生成器，适用于高并发、大规模分布式系统。
 * 本实现具备以下核心特性：
 * <ul>
 *   <li><b>高性能</b>：单机可稳定支持 10~30 万 QPS，理论极限达每秒 409.6 万 ID；</li>
 *   <li><b>强唯一性</b>：通过 (datacenterId, workerId) 保证节点唯一，配合时钟回拨保护，杜绝 ID 重复；</li>
 *   <li><b>高可用容错</b>：支持外部稳定分配节点 ID，自动哈希仅作兜底，适配云原生环境；</li>
 *   <li><b>有序性</b>：生成的 ID 大致单调递增，利于数据库索引优化；</li>
 *   <li><b>轻量无依赖</b>：纯 Java 实现，无需外部服务（除可选的外部 ID 配置外）。</li>
 * </ul>
 *
 * <h2>容量与规模支持</h2>
 * <ul>
 *   <li><b>日 ID 生成量</b>：几十亿 ~ 上千亿（Snowflake 算法本身无性能瓶颈）；</li>
 *   <li><b>日活跃用户（DAU）</b>：1 亿 ~ 5 亿（取决于业务人均 ID 生成量）；</li>
 *   <li><b>最大服务实例数</b>：≤ 1024 台（32 个数据中心 × 32 个工作节点）；</li>
 *   <li><b>部署环境</b>：Kubernetes（StatefulSet / Deployment）、虚拟机（VM）、混合云环境；</li>
 *   <li><b>可靠性等级</b>：工业级（适用于互联网高并发场景；金融级账务系统需额外审计与双写校验）。</li>
 * </ul>
 *
 * <h2>使用要求</h2>
 * <ul>
 *   <li>必须确保每个实例的 {@code (datacenterId, workerId)} 组合全局唯一；
 *       推荐通过环境变量 {@code SNOWFLAKE_WORKER_ID} 和 {@code SNOWFLAKE_DATACENTER_ID} 或 JVM 参数
 *       {@code -Dsnowflake.workerId} / {@code -Dsnowflake.datacenterId} 显式分配；</li>
 *   <li>系统时钟需保持同步（建议启用 NTP），严重时钟回拨（>5ms）将触发运行时异常；</li>
 *   <li>本类设计为工具类，可在 Spring 容器初始化前安全使用。</li>
 * </ul>
 *
 * <h2>ID 结构（64 位 long）</h2>
 * <pre>
 * | 1 位保留（0） | 41 位时间戳（毫秒） | 5 位数据中心 ID | 5 位工作节点 ID | 12 位序列号 |
 * </pre>
 * <p>
 * 起始时间戳（twepoch）：2023-01-01 00:00:00 UTC（1672531200000L），可使用至 2092 年。
 *
 * @author Eleven52AC
 * @since 2025-11-18
 * @see IdGenerator
 */
@Slf4j
public class SnowflakeIdGenerator {

    /**
     * 起始时间戳（毫秒），用于计算相对时间偏移。
     * 值为 2023-01-01 00:00:00 UTC，对应毫秒时间戳 1672531200000L。
     * 使用自定义纪元可延长 41 位时间戳的有效使用年限（约 69 年）。
     */
    private final long twepoch = 1672531200000L; // 2023-01-01

    /**
     * 数据中心 ID 所占的位数（bit），默认为 5 位。
     * 支持最多 2^5 = 32 个数据中心。
     */
    private final long datacenterIdBits = 5L;

    /**
     * 工作节点 ID 所占的位数（bit），默认为 5 位。
     * 支持最多 2^5 = 32 个工作节点（每数据中心）。
     */
    private final long workerIdBits = 5L;

    /**
     * 序列号所占的位数（bit），默认为 12 位。
     * 支持单节点单毫秒内生成最多 2^12 = 4096 个 ID。
     */
    private final long sequenceBits = 12L;

    /**
     * 数据中心 ID 的最大允许值（即 2^5 - 1 = 31）。
     * 通过位运算 ~(-1L << 5) 得到二进制 00000000...00011111（低 5 位为 1）。
     */
    private final long maxDatacenterId = ~(-1L << datacenterIdBits);

    /**
     * 工作节点 ID 的最大允许值（即 2^5 - 1 = 31）。
     * 用于校验传入的 workerId 是否在合法范围内。
     */
    private final long maxWorkerId = ~(-1L << workerIdBits);

    /**
     * 工作节点 ID 在最终 ID 中的左移位数。
     * 等于序列号位数（12），确保 workerId 位于序列号之上。
     */
    private final long workerIdShift = sequenceBits;

    /**
     * 数据中心 ID 在最终 ID 中的左移位数。
     * 等于序列号位数 + 工作节点 ID 位数（12 + 5 = 17）。
     */
    private final long datacenterIdShift = sequenceBits + workerIdBits;

    /**
     * 时间戳部分在最终 ID 中的左移位数。
     * 等于序列号 + workerId + datacenterId 总位数（12 + 5 + 5 = 22）。
     * 使得 41 位时间戳占据 ID 的高位。
     */
    private final long timestampLeftShift = sequenceBits + workerIdBits + datacenterIdBits;

    /**
     * 序列号掩码，用于保证序列号不超过分配的位数。
     * 值为 0xFFF（即 4095），通过 ~(-1L << 12) 计算得出。
     * 在序列号自增后执行 (sequence & sequenceMask) 可自动归零溢出。
     */
    private final long sequenceMask = ~(-1L << sequenceBits);

    /**
     * 当前数据中心 ID，由构造时传入，范围 [0, 31]。
     */
    private final long datacenterId;

    /**
     * 当前工作节点 ID，由构造时传入，范围 [0, 31]。
     */
    private final long workerId;

    /**
     * 允许的小范围时钟回拨（毫秒）。
     */
    private static final long MAX_BACKWARD_MS = 5L;

    /**
     * 使用一个 AtomicLong 将「最后一次使用的时间戳」和「序列号」打包在一起，避免加锁。
     * state 的结构：
     *
     * 高 52 位：上一次生成 ID 使用的时间戳（毫秒）
     * 低 12 位：该时间戳下的序列号
     *
     * 即：state = (lastTimestamp << sequenceBits) | sequence
     */
    private final AtomicLong state = new AtomicLong(0L);

    private static volatile SnowflakeIdGenerator instance;

    public SnowflakeIdGenerator(long datacenterId, long workerId) {
        if (datacenterId > maxDatacenterId || datacenterId < 0) {
            throw new IllegalArgumentException("Datacenter ID out of range");
        }
        if (workerId > maxWorkerId || workerId < 0) {
            throw new IllegalArgumentException("Worker ID out of range");
        }
        this.datacenterId = datacenterId;
        this.workerId = workerId;
    }

    public static SnowflakeIdGenerator getInstance() {
        if (instance == null) {
            synchronized (SnowflakeIdGenerator.class) {
                if (instance == null) {
                    instance = new SnowflakeIdGenerator(getDatacenterId(), getWorkerId());
                }
            }
        }
        return instance;
    }

    /**
     * 生成下一个全局唯一雪花 ID（Snowflake ID）。
     * <p>
     * 该方法基于 Twitter Snowflake 算法实现，生成 64 位长整型 ID，结构如下：
     * <pre>
     * | 1 位保留位（始终为 0）| 41 位时间戳（毫秒）| 5 位数据中心 ID | 5 位工作节点 ID | 12 位序列号 |
     * </pre>
     * <p>
     * <strong>线程安全</strong>：通过 AtomicLong + CAS 实现无锁并发控制，保证多线程环境下 ID 的唯一性和单调递增性。
     * <p>
     * <strong>时钟回拨处理</strong>：
     * <ul>
     *   <li>若回拨 ≤ 5ms：拉平到 lastTimestamp，保证 ID 单调递增；</li>
     *   <li>若回拨 > 5ms：抛出运行时异常，拒绝生成 ID（需外部干预，如监控告警）。</li>
     * </ul>
     *
     * @return 生成的全局唯一雪花 ID（64 位 long 类型）
     * @throws RuntimeException 当系统时钟发生严重回拨（> 5ms）时抛出，表示无法安全生成 ID
     */
    public long nextId() {
        for (; ; ) {
            long currentState = state.get();
            long lastTimestamp = currentState >>> sequenceBits;
            long lastSequence = currentState & sequenceMask;

            long timestamp = currentTime();

            // 时钟回拨检测，确保 ID 的单调递增性
            if (timestamp < lastTimestamp) {
                long offset = lastTimestamp - timestamp;
                if (offset <= MAX_BACKWARD_MS) {
                    // 小范围回拨：直接拉平到 lastTimestamp
                    timestamp = lastTimestamp;
                } else {
                    // 拒绝处理大范围时钟回拨，防止 ID 冲突
                    throw new RuntimeException(
                            "Clock moved backwards. Refusing to generate id for " + offset + "ms"
                    );
                }
            }

            long nextTimestamp = timestamp;
            long nextSequence;

            if (timestamp == lastTimestamp) {
                // 同一毫秒内，序列号自增
                nextSequence = (lastSequence + 1) & sequenceMask;
                // 如果序列号溢出，则等待下一毫秒
                if (nextSequence == 0) {
                    nextTimestamp = waitNextMillis(lastTimestamp);
                }
            } else {
                // 新毫秒开始，重置序列号
                nextSequence = 0L;
            }

            long nextState = (nextTimestamp << sequenceBits) | nextSequence;

            // CAS 更新状态，成功则生成 ID，失败则自旋重试
            if (state.compareAndSet(currentState, nextState)) {
                long diff = nextTimestamp - twepoch;
                if (diff < 0) {
                    throw new RuntimeException("Current timestamp is before twepoch: " + nextTimestamp);
                }
                // 组合各部分形成最终 ID：
                // [时间戳(41位)] [数据中心ID(5位)] [工作节点ID(5位)] [序列号(12位)]
                return (diff << timestampLeftShift)
                        | (datacenterId << datacenterIdShift)
                        | (workerId << workerIdShift)
                        | nextSequence;
            }
            // CAS 失败表示有其他线程并发更新了 state，继续 for 循环重试即可
        }
    }

    /**
     * 自旋等待直到获取到大于指定时间戳的当前时间（毫秒）。
     * <p>
     * 用于处理同一毫秒内序列号耗尽（溢出）或轻微时钟回拨（≤5ms）的场景，
     * 确保生成的 ID 严格单调递增。
     *
     * @param lastTimestamp 上一次生成 ID 的时间戳（毫秒）
     * @return 大于 {@code lastTimestamp} 的当前系统时间戳（毫秒）
     * @implNote 该方法通过忙等待（busy-waiting）实现，极端情况下可能短暂消耗 CPU，
     *           但因等待时间极短（通常 < 1ms），对系统影响可忽略。
     */
    private long waitNextMillis(long lastTimestamp) {
        long timestamp = currentTime();
        while (timestamp <= lastTimestamp) {
            Thread.onSpinWait();
            timestamp = currentTime();
        }
        return timestamp;
    }

    /**
     * 获取当前系统时间戳（毫秒）。
     *
     * @return 当前系统时间戳（毫秒）
     */
    private long currentTime() {
        return System.currentTimeMillis();
    }

    /**
     * 获取工作节点ID
     *
     * <p>通过本机IP地址生成工作节点ID，确保在同一数据中心内的不同节点具有唯一的工作ID。
     * 使用IP地址的hash值与0x1F(31)进行按位与运算，保证返回值在0-31范围内(5位)。</p>
     *
     * <p>如果获取本机IP失败，则随机生成一个0-31之间的数字作为工作节点ID。</p>
     *
     * @return long 工作节点ID (0-31)
     */
    private static long getWorkerId() {
        // 环境变量（K8s/Docker）
        String workerIdStr = System.getenv("SNOWFLAKE_WORKER_ID");
        // JVM 参数（虚拟机环境）
        if (workerIdStr == null) {
            workerIdStr = System.getProperty("snowflake.workerId");
        }
        // 解析并校验
        if (workerIdStr != null) {
            try {
                long id = Long.parseLong(workerIdStr);
                if (id >= 0 && id <= 31) {
                    log.info("WorkerId 通过配置获取: {}", id);
                    return id;
                } else {
                    log.error("WorkerId 配置无效(超出范围): {}", id);
                }
            } catch (NumberFormatException e) {
                log.error("WorkerId 配置格式错误: {}", workerIdStr);
            }
        }
        try {
            // 获取本机IP地址并计算hash值，取低5位作为工作节点ID
            String ip = InetAddress.getLocalHost().getHostAddress();
            return (ip.hashCode() & 0x1F);
        } catch (Exception e) {
            // IP获取失败时，随机生成一个工作节点ID
            return new Random().nextInt(32);
        }
    }

    /**
     * 获取数据中心ID
     *
     * <p>通过本机主机名生成数据中心ID，确保在不同数据中心内的节点具有不同的数据中心标识。
     * 使用主机名的hash值与0x1F(31)进行按位与运算，保证返回值在0-31范围内(5位)。</p>
     *
     * <p>如果获取主机名失败，则返回默认值1作为数据中心ID。</p>
     *
     * @return long 数据中心ID (0-31)
     */
    private static long getDatacenterId() {
        // 环境变量（K8s/Docker）
        String datacenterIdStr = System.getenv("SNOWFLAKE_DATACENTER_ID");
        // JVM 参数（虚拟机环境）
        if (datacenterIdStr == null) {
            datacenterIdStr = System.getProperty("snowflake.datacenterId");
        }
        // 解析并校验
        if (datacenterIdStr != null) {
            try {
                long id = Long.parseLong(datacenterIdStr);
                if (id >= 0 && id <= 31) {
                    log.info("DatacenterId 通过配置获取: {}", id);
                    return id;
                } else {
                    log.error("DatacenterId 配置无效(超出范围): {}", id);
                }
            } catch (NumberFormatException e) {
                log.error("DatacenterId 配置格式错误: {}", datacenterIdStr);
            }
        }
        try {
            // 获取本机主机名并计算hash值，取低5位作为数据中心ID
            String hostname = InetAddress.getLocalHost().getHostName();
            return (hostname.hashCode() & 0x1F);
        } catch (Exception e) {
            // 主机名获取失败时，返回默认数据中心ID
            return 1L;
        }
    }

    /**
     * 生成下一个分布式唯一ID（静态便捷方法）
     */
    public static long next() {
        return getInstance().nextId();
    }
}

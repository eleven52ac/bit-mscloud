package common.utils;

/**
 * 雪花算法实现类（Snowflake ID Generator）
 * 生成全局唯一、趋势递增的 64 位 long 型 ID。
 */
public class SnowflakeIdGenerator {

    /**
     * @description： 起始时间戳（自定义，单位：毫秒），用于减少生成的时间值位数。
     * @eg: 2023-01-01 00:00:00
     */
    private final long twepoch = 1672531200000L;

    /**
     * 数据中心 ID 所占的位数（5）
     */
    private final long datacenterIdBits = 5L;

    /**
     * 机器 ID 所占的位数（5）
     */
    private final long workerIdBits = 5L;

    /**
     * 毫秒内序列号所占的位数（12）
     */
    private final long sequenceBits = 12L;

    /**
     * 最大值（位移后所有位都是 1），最大数据中心 ID：31。
     */
    private final long maxDatacenterId = ~(-1L << datacenterIdBits);

    /**
     * 最大机器 ID：31
     */
    private final long maxWorkerId = ~(-1L << workerIdBits);

    // 位移偏移量（用于将各字段左移到正确位置）
    /**
     * 毫秒内序列号左移 12
     */
    private final long workerIdShift = sequenceBits;
    /**
     * 数据中心 ID左移17位
     */
    private final long datacenterIdShift = sequenceBits + workerIdBits;
    /**
     * 时间戳左移22位
     */
    private final long timestampLeftShift = sequenceBits + workerIdBits + datacenterIdBits;

    // 毫秒内序列的掩码（用于对序列进行取模，范围 0~4095）
    /**
     * 毫秒内序列的掩码（用于对序列进行取模，范围 0~4095）
     */
    private final long sequenceMask = ~(-1L << sequenceBits); // 4095

    // 运行时变量
    private long datacenterId; // 当前实例的数据中心 ID
    private long workerId;     // 当前实例的机器 ID
    private long sequence = 0L; // 当前毫秒内的计数器
    private long lastTimestamp = -1L; // 上一次生成 ID 的时间戳（单位：毫秒）

    /**
     * 构造函数，传入数据中心 ID 和机器 ID
     */
    public SnowflakeIdGenerator(long datacenterId, long workerId) {
        // 参数合法性校验
        if (datacenterId > maxDatacenterId || datacenterId < 0)
            throw new IllegalArgumentException("Datacenter ID out of range");
        if (workerId > maxWorkerId || workerId < 0)
            throw new IllegalArgumentException("Worker ID out of range");

        this.datacenterId = datacenterId;
        this.workerId = workerId;
    }

    /**
     * 生成下一个全局唯一 ID（线程安全）
     */
    public synchronized long nextId() {
        long timestamp = currentTime(); // 当前时间戳（毫秒）

        // 如果系统时钟回退了，拒绝生成 ID，避免重复
        if (timestamp < lastTimestamp)
            throw new RuntimeException("Clock moved backwards. Refusing to generate id.");

        if (timestamp == lastTimestamp) {
            // 同一毫秒内生成 ID，序列号自增
            sequence = (sequence + 1) & sequenceMask;

            // 如果序列号超过最大值（4095），阻塞到下一毫秒
            if (sequence == 0) {
                timestamp = waitNextMillis(lastTimestamp);
            }
        } else {
            // 时间戳变化，重置序列号
            sequence = 0L;
        }

        lastTimestamp = timestamp;

        // 通过左移将各部分拼接成最终 64 位 ID
        return ((timestamp - twepoch) << timestampLeftShift)    // 时间戳部分（41 位）
                | (datacenterId << datacenterIdShift)           // 数据中心 ID（5 位）
                | (workerId << workerIdShift)                   // 机器 ID（5 位）
                | sequence;                                     // 序列号（12 位）
    }

    /**
     * 等待直到下一毫秒（用于序列号溢出时）
     */
    private long waitNextMillis(long lastTimestamp) {
        long timestamp = currentTime();
        while (timestamp <= lastTimestamp) {
            timestamp = currentTime(); // 自旋等待
        }
        return timestamp;
    }

    /**
     * 获取当前系统时间（毫秒）
     */
    private long currentTime() {
        return System.currentTimeMillis();
    }

    public static void main(String[] args) {
        SnowflakeIdGenerator idGenerator = new SnowflakeIdGenerator(1, 1);
        for (int i = 0; i < 10000000; i++) {
            long id = idGenerator.nextId();
            System.out.println(id);
        }
    }
}

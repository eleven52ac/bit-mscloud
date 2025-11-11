package common.utils.core;

import java.net.InetAddress;
import java.util.Random;

public class SnowflakeIdGenerator {

    private final long twepoch = 1672531200000L; // 2023-01-01

    private final long datacenterIdBits = 5L;
    private final long workerIdBits = 5L;
    private final long sequenceBits = 12L;

    private final long maxDatacenterId = ~(-1L << datacenterIdBits);
    private final long maxWorkerId = ~(-1L << workerIdBits);

    private final long workerIdShift = sequenceBits;
    private final long datacenterIdShift = sequenceBits + workerIdBits;
    private final long timestampLeftShift = sequenceBits + workerIdBits + datacenterIdBits;
    private final long sequenceMask = ~(-1L << sequenceBits);

    private long datacenterId;
    private long workerId;
    private long sequence = 0L;
    private long lastTimestamp = -1L;

    private static volatile SnowflakeIdGenerator instance;

    public SnowflakeIdGenerator(long datacenterId, long workerId) {
        if (datacenterId > maxDatacenterId || datacenterId < 0)
            throw new IllegalArgumentException("Datacenter ID out of range");
        if (workerId > maxWorkerId || workerId < 0)
            throw new IllegalArgumentException("Worker ID out of range");
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

    public synchronized long nextId() {
        long timestamp = currentTime();

        // 时钟回拨检测
        if (timestamp < lastTimestamp) {
            long offset = lastTimestamp - timestamp;
            if (offset <= 5) {
                timestamp = waitNextMillis(lastTimestamp);
            } else {
                throw new RuntimeException("Clock moved backwards. Refusing to generate id for " + offset + "ms");
            }
        }

        if (timestamp == lastTimestamp) {
            sequence = (sequence + 1) & sequenceMask;
            if (sequence == 0) {
                timestamp = waitNextMillis(lastTimestamp);
            }
        } else {
            sequence = 0L;
        }

        lastTimestamp = timestamp;

        return ((timestamp - twepoch) << timestampLeftShift)
                | (datacenterId << datacenterIdShift)
                | (workerId << workerIdShift)
                | sequence;
    }

    private long waitNextMillis(long lastTimestamp) {
        long timestamp = currentTime();
        while (timestamp <= lastTimestamp) {
            timestamp = currentTime();
        }
        return timestamp;
    }

    private long currentTime() {
        return System.currentTimeMillis();
    }

    private static long getWorkerId() {
        try {
            String ip = InetAddress.getLocalHost().getHostAddress();
            return (ip.hashCode() & 0x1F);
        } catch (Exception e) {
            return new Random().nextInt(32);
        }
    }

    private static long getDatacenterId() {
        try {
            String hostname = InetAddress.getLocalHost().getHostName();
            return (hostname.hashCode() & 0x1F);
        } catch (Exception e) {
            return 1L;
        }
    }

    public static long next() {
        return getInstance().nextId();
    }
}

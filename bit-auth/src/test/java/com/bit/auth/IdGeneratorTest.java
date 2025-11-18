package com.bit.auth;

import com.bit.common.utils.core.IdGenerator;
import org.junit.jupiter.api.Test;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @Datetime: 2025年11月18日17:32
 * @Author: Eleven52AC
 * @Description: 测试类
 */
public class IdGeneratorTest {

    @Test
    public void testVirtualThreadsHighConcurrency() throws InterruptedException {
        int virtualThreadCount = 80_000; // 启动 1 万个虚拟线程
        int idsPerThread = 1_000;        // 每个线程生成 1000 个 ID
        long totalExpected = (long) virtualThreadCount * idsPerThread;

        // 使用 ConcurrentHashMap 检测重复（也可用 LongAdder + Set，但内存大）
        ConcurrentHashMap<Long, Boolean> idSet = new ConcurrentHashMap<>();
        AtomicLong duplicateCount = new AtomicLong(0);
        AtomicLong generatedCount = new AtomicLong(0);

        long start = System.currentTimeMillis();

        // 使用 StructuredTaskScope（JDK 21 推荐）或 ExecutorService
        try (var executor = Executors.newVirtualThreadPerTaskExecutor()) {
            for (int i = 0; i < virtualThreadCount; i++) {
                executor.submit(() -> {
                    for (int j = 0; j < idsPerThread; j++) {
                        try {
                            long id = IdGenerator.nextId(); // 无 Redis 容灾版
                            generatedCount.incrementAndGet();
                            if (idSet.putIfAbsent(id, Boolean.TRUE) != null) {
                                duplicateCount.incrementAndGet();
                            }
                        } catch (Exception e) {
                            System.err.println("ID 生成异常: " + e.getMessage());
                        }
                    }
                    return null;
                });
            }
        } // 自动 await termination

        long end = System.currentTimeMillis();
        long durationMs = end - start;
        double qps = totalExpected * 1000.0 / durationMs;

        System.out.printf("""
                === 测试结果 ===
                总生成 ID 数: %d
                预期生成数: %d
                重复 ID 数: %d
                耗时: %d ms
                平均 QPS: %.0f
                CPU: Apple M4 (ARM64)
                JDK: %s
                结论: %s
                """,
                generatedCount.get(),
                totalExpected,
                duplicateCount.get(),
                durationMs,
                qps,
                System.getProperty("java.version"),
                duplicateCount.get() == 0 ? "✅ 无重复，线程安全！" : "❌ 发现重复 ID！"
        );

        // 断言无重复
        if (duplicateCount.get() > 0) {
            throw new AssertionError("检测到 " + duplicateCount.get() + " 个重复 ID！");
        }
    }
}
package com.bit.auth;

import com.bit.common.utils.core.IdGenerator;
import org.junit.jupiter.api.Test;

import java.lang.management.ManagementFactory;
import java.lang.management.OperatingSystemMXBean;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @Datetime: 2025年11月18日17:32
 * @Author: Eleven52AC
 * @Description: 测试类（动态收集系统信息）
 */
public class IdGeneratorTest {

    @Test
    public void testVirtualThreadsHighConcurrency() throws InterruptedException {

        // ====== 动态获取系统信息 ======
        OperatingSystemMXBean osBean = ManagementFactory.getOperatingSystemMXBean();
        String osName = osBean.getName();
        String osArch = osBean.getArch();
        int osCores = osBean.getAvailableProcessors();

        long totalMem = -1;
        long freeMem = -1;
        long usedMem = -1;

        // 尝试获取更丰富的系统指标（com.sun.management）
        if (osBean instanceof com.sun.management.OperatingSystemMXBean sunOsBean) {
            totalMem = sunOsBean.getTotalPhysicalMemorySize();
            freeMem = sunOsBean.getFreePhysicalMemorySize();
            usedMem = totalMem - freeMem;
        }

        int virtualThreadCount = 50_000; // 启动 5 万个虚拟线程
        int idsPerThread = 1_000;        // 每个线程生成 1000 个 ID
        long totalExpected = (long) virtualThreadCount * idsPerThread;

        ConcurrentHashMap<Long, Boolean> idSet = new ConcurrentHashMap<>();
        AtomicLong duplicateCount = new AtomicLong(0);
        AtomicLong generatedCount = new AtomicLong(0);

        long start = System.currentTimeMillis();

        try (var executor = Executors.newVirtualThreadPerTaskExecutor()) {
            for (int i = 0; i < virtualThreadCount; i++) {
                executor.submit(() -> {
                    for (int j = 0; j < idsPerThread; j++) {
                        try {
                            long id = IdGenerator.nextId();
                            generatedCount.incrementAndGet();
                            if (idSet.putIfAbsent(id, Boolean.TRUE) != null) {
                                duplicateCount.incrementAndGet();
                            }
                        } catch (Exception e) {
                            System.err.println("ID生成异常: " + e.getMessage());
                        }
                    }
                    return null;
                });
            }
        }

        long end = System.currentTimeMillis();
        long durationMs = end - start;
        double qps = totalExpected * 1000.0 / durationMs;

        // ====== 输出格式 ======
        System.out.printf("""
                === 测试结果 ===
                总生成 ID 数: %d
                预期生成数: %d
                重复 ID 数: %d
                耗时: %d ms
                平均 QPS: %.0f

                === 系统信息 ===
                操作系统: %s
                架构: %s
                CPU 核心数: %d
                JDK 版本: %s

                内存总量: %s
                已用内存: %s
                可用内存: %s

                结论: %s
                """,
                generatedCount.get(),
                totalExpected,
                duplicateCount.get(),
                durationMs,
                qps,
                osName,
                osArch,
                osCores,
                System.getProperty("java.version"),
                formatBytes(totalMem),
                formatBytes(usedMem),
                formatBytes(freeMem),
                duplicateCount.get() == 0 ? "✅ 无重复，线程安全！" : "❌ 发现重复 ID！"
        );

        if (duplicateCount.get() > 0) {
            throw new AssertionError("检测到 " + duplicateCount.get() + " 个重复 ID！");
        }
    }

    /** 将字节格式化输出，自动转换为 MB/GB */
    private static String formatBytes(long bytes) {
        if (bytes < 0) return "未知";
        double kb = bytes / 1024.0;
        double mb = kb / 1024.0;
        double gb = mb / 1024.0;

        if (gb > 1) return String.format("%.2f GB", gb);
        if (mb > 1) return String.format("%.2f MB", mb);
        return String.format("%.2f KB", kb);
    }

    @Test
    public void testNextIdPureQPS() throws Exception {
        int threadCount = 32;   // 固定线程（32核 CPU 也能匹配）
        long idPerThread = 2_000_000;  // 每线程生成 200 万
        long total = idPerThread * threadCount;
        ExecutorService pool = Executors.newFixedThreadPool(threadCount);
        long start = System.currentTimeMillis();
        CountDownLatch latch = new CountDownLatch(threadCount);
        for (int t = 0; t < threadCount; t++) {
            pool.submit(() -> {
                for (int i = 0; i < idPerThread; i++) {
                    IdGenerator.nextId();
                }
                latch.countDown();
            });
        }
        latch.await();
        long end = System.currentTimeMillis();
        double qps = total * 1000.0 / (end - start);
        System.out.println("总生成ID=" + total);
        System.out.println("耗时=" + (end - start) + " ms");
        System.out.println("QPS=" + String.format("%.0f", qps));
    }

}

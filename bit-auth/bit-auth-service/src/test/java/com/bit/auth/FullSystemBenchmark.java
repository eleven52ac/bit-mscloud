package com.bit.auth;

import java.io.*;
import java.security.*;
import java.util.*;
import java.util.concurrent.*;
import java.util.stream.IntStream;

public class FullSystemBenchmark {

    private static final int FILE_SIZE_MB = 100; // 100MB
    private static final int ARRAY_SIZE_MB = 1024; // 1GB
    private static final int PRIME_LIMIT = 50_000_000;

    public static void main(String[] args) throws Exception {
        long totalStart = System.currentTimeMillis();

        ExecutorService executor = Executors.newFixedThreadPool(4);
        List<Callable<Void>> tasks = Arrays.asList(
                FullSystemBenchmark::cpuTest,
                FullSystemBenchmark::memoryTest,
                FullSystemBenchmark::diskTest,
                FullSystemBenchmark::hashTest
        );

        executor.invokeAll(tasks);
        executor.shutdown();

        long totalEnd = System.currentTimeMillis();
        System.out.println("🔧 Total benchmark time: " + (totalEnd - totalStart) + " ms");
    }

    // CPU 密集：计算素数
    private static Void cpuTest() {
        long start = System.currentTimeMillis();
        long count = IntStream.range(2, PRIME_LIMIT)
                .parallel()
                .filter(FullSystemBenchmark::isPrime)
                .count();
        long end = System.currentTimeMillis();
        System.out.println("🧠 CPU Test (prime count up to " + PRIME_LIMIT + "): " + count + " in " + (end - start) + " ms");
        return null;
    }

    // 内存密集：分配并遍历 1GB 数组
    private static Void memoryTest() {
        long start = System.currentTimeMillis();
        byte[] data = new byte[ARRAY_SIZE_MB * 1024 * 1024];
        for (int i = 0; i < data.length; i += 4096) {
            data[i] = (byte) (i % 256);
        }
        long checksum = 0;
        for (byte b : data) checksum += b;
        long end = System.currentTimeMillis();
        System.out.println("💾 Memory Test (1GB array traversal): " + (end - start) + " ms, checksum: " + checksum);
        return null;
    }

    // 磁盘测试：写入 & 读取大文件
    private static Void diskTest() throws IOException {
        long start = System.currentTimeMillis();
        File file = new File("disk_benchmark_test.bin");
        byte[] buffer = new byte[1024 * 1024];
        Arrays.fill(buffer, (byte) 1);

        try (FileOutputStream out = new FileOutputStream(file)) {
            for (int i = 0; i < FILE_SIZE_MB; i++) {
                out.write(buffer);
            }
        }

        long checksum = 0;
        try (FileInputStream in = new FileInputStream(file)) {
            int read;
            while ((read = in.read(buffer)) != -1) {
                for (int i = 0; i < read; i++) {
                    checksum += buffer[i];
                }
            }
        }

        long end = System.currentTimeMillis();
        file.delete(); // 清理
        System.out.println("📀 Disk Test (100MB write + read): " + (end - start) + " ms, checksum: " + checksum);
        return null;
    }

    // Hash 压力测试：SHA-256 执行 100 万次
    private static Void hashTest() throws NoSuchAlgorithmException {
        long start = System.currentTimeMillis();
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] input = "benchmark".getBytes();
        for (int i = 0; i < 1_000_000; i++) {
            digest.update(input);
            digest.digest();
        }
        long end = System.currentTimeMillis();
        System.out.println("🔒 Hash Test (1M SHA-256): " + (end - start) + " ms");
        return null;
    }

    private static boolean isPrime(int n) {
        if (n < 2) return false;
        if (n == 2 || n == 3) return true;
        if (n % 2 == 0 || n % 3 == 0) return false;
        for (int i = 5; i * i <= n; i += 6) {
            if (n % i == 0 || n % (i + 2) == 0) return false;
        }
        return true;
    }
}

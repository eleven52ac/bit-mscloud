package com.bit.auth;

import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

public class ExtremePerformanceTest {

    public static void main(String[] args) {
        try {
            long start = System.currentTimeMillis();
            runExtremeTest();
            long end = System.currentTimeMillis();
            System.out.println("Extreme test completed in " + (end - start) + " ms");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void runExtremeTest() throws Exception {
        ExecutorService executor = Executors.newFixedThreadPool(4);
        List<Callable<Void>> tasks = List.of(
                () -> { cpuIntensiveTask(); return null; },
                () -> { memoryIntensiveTask(); return null; },
                () -> { diskIntensiveTask(); return null; }
        );
        executor.invokeAll(tasks);
        executor.shutdown();
    }

    public static void cpuIntensiveTask() throws Exception {
        long limit = 100_000_000L;
        int cores = Runtime.getRuntime().availableProcessors();
        ExecutorService pool = Executors.newFixedThreadPool(cores);
        long step = limit / cores;
        List<Future<Long>> results = new ArrayList<>();

        for (int i = 0; i < cores; i++) {
            long start = i * step + 1;
            long end = (i + 1) * step;
            results.add(pool.submit(() -> countPrimes(start, end)));
        }

        long total = 0;
        for (Future<Long> f : results) total += f.get();
        pool.shutdown();
        System.out.println("Prime count up to " + limit + ": " + total);
    }

    private static long countPrimes(long start, long end) {
        long count = 0;
        for (long i = start; i <= end; i++) {
            if (isPrime(i)) count++;
        }
        return count;
    }

    private static boolean isPrime(long n) {
        if (n < 2) return false;
        if (n == 2 || n == 3) return true;
        if (n % 2 == 0 || n % 3 == 0) return false;
        for (long i = 5; i * i <= n; i += 6) {
            if (n % i == 0 || n % (i + 2) == 0) return false;
        }
        return true;
    }

    public static void memoryIntensiveTask() {
        try {
            int gb = 12;
            byte[] bigArray = new byte[gb * 1024 * 1024 * 1024];
            bigArray[0] = 1;
            System.out.println("Allocated " + gb + " GB memory.");
        } catch (OutOfMemoryError e) {
            System.out.println("Memory allocation failed: " + e.getMessage());
        }
    }

    public static void diskIntensiveTask() throws Exception {
        String path = "r9_disk_test.txt";
        String data = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789".repeat(1024); // ~36KB
        try (FileWriter fw = new FileWriter(path)) {
            for (int i = 0; i < 30_000; i++) {
                fw.write(data);
            }
        }
        System.out.println("Disk intensive task completed.");
    }
}

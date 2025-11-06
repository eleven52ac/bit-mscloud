package com.bit.auth;

import java.util.*;
import java.util.concurrent.*;

public class ExtremeCpuStressTest {

    private static final int THREADS = Runtime.getRuntime().availableProcessors();

    public static void main(String[] args) throws InterruptedException {
        long startTime = System.currentTimeMillis();

        System.out.println("🏁 Starting Extreme CPU Stress Test with " + THREADS + " threads...");

        ExecutorService executor = Executors.newFixedThreadPool(THREADS);
        List<Future<String>> futures = new ArrayList<>();

        for (int i = 0; i < THREADS; i++) {
            futures.add(executor.submit(new StressWorker(i)));
        }

        for (Future<String> future : futures) {
            try {
                System.out.println(future.get());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        executor.shutdown();
        executor.awaitTermination(5, TimeUnit.MINUTES);

        long endTime = System.currentTimeMillis();
        System.out.println("✅ Test finished in " + (endTime - startTime) + " ms");
    }

    static class StressWorker implements Callable<String> {
        private final int id;

        StressWorker(int id) {
            this.id = id;
        }

        @Override
        public String call() {
            long ops = 0;
            long start = System.nanoTime();
            long endTime = System.currentTimeMillis() + 10_000; // 每线程运行 10 秒

            while (System.currentTimeMillis() < endTime) {
                double x = Math.random();
                for (int i = 0; i < 10_000; i++) {
                    x = Math.sin(x) * Math.cos(x) + Math.sqrt(x * x + 1);
                }
                ops++;
            }

            long duration = System.nanoTime() - start;
            return "🧠 [Thread " + id + "] ops=" + ops + ", time=" + (duration / 1_000_000) + "ms";
        }
    }
}

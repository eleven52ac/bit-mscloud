package com.bit.auth;

import org.junit.jupiter.api.Test;

import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicLong;

public class ListPerformanceTest {

    // 测试参数
    private static final int THREADS = 100;         // 并发线程数
    private static final int READ_TIMES = 1000;    // 读少
    private static final int WRITE_TIMES = 10000; // 写多

    public static void main(String[] args) throws InterruptedException {
        // 测试1：SynchronizedList
        List<Integer> syncList = Collections.synchronizedList(new ArrayList<>());
        long syncTime = testPerformance("SynchronizedList", syncList);

        // 测试2：CopyOnWriteArrayList
        List<Integer> cowList = new CopyOnWriteArrayList<>();
        long cowTime = testPerformance("CopyOnWriteArrayList", cowList);


        System.out.println("\n=== 结果对比 ===");
        System.out.printf("SynchronizedList 耗时: %d ms\n", syncTime);
        System.out.printf("CopyOnWriteArrayList 耗时: %d ms\n", cowTime);
    }

    private static long testPerformance(String name, List<Integer> list) throws InterruptedException {
        ExecutorService pool = Executors.newFixedThreadPool(THREADS);
        CountDownLatch latch = new CountDownLatch(THREADS);
        AtomicLong sum = new AtomicLong();

        long start = System.currentTimeMillis();

        for (int t = 0; t < THREADS; t++) {
            pool.execute(() -> {
                Random random = new Random();
                // 模拟读操作（多）
                for (int i = 0; i < READ_TIMES; i++) {
                    if (!list.isEmpty()) {
                        int idx = random.nextInt(list.size());
                        sum.addAndGet(list.get(idx));
                    }
                }
                // 模拟写操作（少）
                for (int i = 0; i < WRITE_TIMES; i++) {
                    list.add(random.nextInt(1000));
                }
                latch.countDown();
            });
        }

        latch.await();
        long end = System.currentTimeMillis();

        pool.shutdown();
        System.out.printf("%s 完成，耗时: %d ms, 最终大小: %d, 计算sum=%d\n",
                name, (end - start), list.size(), sum.get());

        return end - start;
    }


    @Test
    public void testHashSet(){
        Set<Integer> set = new HashSet<>();
        set.add(1);
        set.add(2);
        set.add(3);
        boolean add = set.add(1);
        System.out.println(add);
        System.out.println(set);
    }

    @Test
    public void testLinkedHashSet(){
        Set<Integer> set = new LinkedHashSet<>();
        set.add(101);
        set.add(23);
        set.add(456);
        System.out.println(set);
    }

    @Test
    public void testTreeSet(){
        Set<Integer> set = new TreeSet<>();
        set.add(234);
        set.add(543);
        set.add(12);
        System.out.println(set);
    }
}

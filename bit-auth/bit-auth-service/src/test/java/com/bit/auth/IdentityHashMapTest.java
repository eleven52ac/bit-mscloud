package com.bit.auth;

import org.junit.jupiter.api.Test;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @Datetime: 2025年08月29日15:04
 * @Author: Eleven52AC
 * @Description:
 */
public class IdentityHashMapTest {

    @Test
    public void testIdentityHashMap() {
        StringBuilder str1 = new StringBuilder("str");
        StringBuilder str2 = new StringBuilder("str");
        int i = System.identityHashCode(str1);
        int j = System.identityHashCode(str2);
        System.out.println(i);
        System.out.println(j);
    }

    @Test
    public void testIdentityHashMap2() {
        ThreadPoolExecutor executor = new ThreadPoolExecutor(
                5, 10,
                60L, TimeUnit.SECONDS,
                new LinkedBlockingQueue<>()
        );

        // 默认情况下还没有线程被创建
        System.out.println("初始线程数: " + executor.getPoolSize()); // 0

        // 预启动所有核心线程
        int started = executor.prestartAllCoreThreads();
        System.out.println("启动的核心线程数: " + started); // 5
        System.out.println("当前线程数: " + executor.getPoolSize()); // 5
    }
}

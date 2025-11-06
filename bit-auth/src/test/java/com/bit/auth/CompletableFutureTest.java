package com.bit.auth;

import org.junit.jupiter.api.Test;

import java.util.concurrent.CompletableFuture;

/**
 * @Datetime: 2025年09月05日16:44
 * @Author: Eleven52AC
 * @Description:
 */
public class CompletableFutureTest {

    @Test
    public void testSupplyAsyncByDefaultThreadPool() {
        // 1. 提交异步任务
        CompletableFuture<String> future = CompletableFuture.supplyAsync(() -> {
            try {
                Thread.sleep(1000); // 模拟耗时任务
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return "Hello, World!";
        });

        // 2. 对异步结果进行转换
        CompletableFuture<String> stringCompletableFuture = future.thenApply(result -> {
            return "Result: " + result;
        });

        // 3. 消费结果
        stringCompletableFuture.thenAccept(result -> {
            System.out.println(result); // 输出 "Result: Hello, World!"
        });

        stringCompletableFuture.join(); // 阻塞直到异步任务完成
    }


}

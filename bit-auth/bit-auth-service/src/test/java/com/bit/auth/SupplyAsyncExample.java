package com.bit.auth;

import java.util.Random;
import java.util.concurrent.*;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

public class SupplyAsyncExample {
    public static void main(String[] args) {
        long startTime = System.currentTimeMillis();
        ExecutorService executor = new ThreadPoolExecutor(2, 2, 0L, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<Runnable>());
        try{
            CompletableFuture<String> supplyAsync = CompletableFuture.supplyAsync(new CompletableTask(new Random().nextInt(1000000)), executor);
            supplyAsync.thenAcceptAsync(value -> System.out.println("The result is: " + value));

            CompletableFuture<Void> future = supplyAsync.thenAcceptAsync(new Consumer<String>() {
                @Override
                public void accept(String result) {
                    System.out.println(result);
                }
            });

            supplyAsync.exceptionallyAsync(new Function<Throwable, String>() {
                @Override
                public String apply(Throwable throwable) {
                    return "";
                }
            });

            //future.join();
            Thread.sleep(1000000);

            long endTime = System.currentTimeMillis();

            System.out.println("耗时：" + (endTime - startTime) + "ms");

        }catch (Exception e){
            e.printStackTrace();
            executor.shutdownNow();
        }finally {
            executor.shutdown();
        }
    }
}


/**
 * 计算水仙花数的任务
 */
class CompletableTask implements Supplier<String> {

    private Integer number;
    // 构造函数，接受一个整数作为计算范围
    CompletableTask(Integer number){
        this.number = number;
    }

    @Override
    public String get() {
        // 设置当前线程的名字为当前计算范围
        Thread.currentThread().setName(number + "以内的水仙数为：");
        StringBuilder result = new StringBuilder();
        // 遍历所有数字，查找水仙花数
        for (int i = 1; i <= number; i++){
            if (isNarcissisticNumber(i)){
                result.append(i).append("\t"); // 将水仙花数添加到结果中
            }
        }
        // 返回计算结果，包含当前线程名称
        return Thread.currentThread().getName() + result;
    }

    /**
     * 判断一个数字是否为水仙花数
     */
    private boolean isNarcissisticNumber(int number){
        int originalNumber = number;
        int digits = String.valueOf(number).length(); // 计算数字的位数
        int sum = 0;
        // 计算每一位的数字的幂并求和
        while (number > 0){
            int digit = number % 10; // 获取数字的每一位
            sum += Math.pow(digit, digits); // 将该位数字的幂加到总和
            number /= 10; // 移除最后一位
        }
        // 如果总和等于原始数字，则为水仙花数
        return sum == originalNumber;
    }
}

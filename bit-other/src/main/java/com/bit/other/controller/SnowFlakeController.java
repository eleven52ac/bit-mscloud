package com.bit.other.controller;

import com.bit.other.entity.SnowflakeRecord;
import com.bit.other.service.SnowflakeRecordService;
import common.dto.response.ApiResponse;
import common.utils.core.SnowflakeIdGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;
import java.util.concurrent.*;

/**
 * @Datetime: 2025年05月17日17:23
 * @Author: Camellia.xiaohua/Bitspark
 * @Package: webservice.controller
 * @Project: SpringCloud
 * @Description:
 */
@RestController("/snowflake")
public class SnowFlakeController {

    @Autowired
    private SnowflakeRecordService snowflakeRecordService;

    private static final int TOTAL = 7_000_000;
    private static final int BATCH_SIZE = 3000;

    @GetMapping("/generate/data")
    public ApiResponse generateData() {
        long startTime = System.currentTimeMillis();
        SnowflakeIdGenerator snowflakeIdGenerator = new SnowflakeIdGenerator(1, 1);
        ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor();

        try {
            for (int batchStart = 0; batchStart < TOTAL; batchStart += BATCH_SIZE) {
                int from = batchStart;
                int to = Math.min(TOTAL, from + BATCH_SIZE);
                List<Future<SnowflakeRecord>> futures = new ArrayList<>(to - from);

                // 提交任务并收集 Future
                for (int i = from; i < to; i++) {
                    futures.add(executor.submit(() -> {
                        long id = snowflakeIdGenerator.nextId();
                        Map<String,String> snowflakeNumber = binarySnowflakeNumber(id);
                        return new SnowflakeRecord.Builder()
                                .id(id)
                                .idBinary(snowflakeNumber.get("idBinary"))
                                .datacenterId(Integer.valueOf(snowflakeNumber.get("datacenterId")))
                                .sequence(Integer.valueOf(snowflakeNumber.get("sequence")))
                                .workerId(Integer.valueOf(snowflakeNumber.get("workerId")))
                                //.timestampDatetime(snowflakeNumber.get("timestamp"))
                                .createdAt(new Date())
                                .build();
                    }));
                }
                // 获取结果并保存
                List<SnowflakeRecord> buffer = new ArrayList<>(to - from);
                for (Future<SnowflakeRecord> future : futures) {
                    try {
                        buffer.add(future.get()); // 虚拟线程下 get() 是轻量的
                    } catch (ExecutionException e) {
                        e.printStackTrace(); // 可以根据需要记录异常
                    }
                }
                snowflakeRecordService.saveBatch(buffer);
            }

            long timeSpent = System.currentTimeMillis() - startTime;
            return ApiResponse.success("生成完毕，共 " + TOTAL + " 条，耗时：" + timeSpent + "ms");

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return ApiResponse.error("数据生成被中断");
        } finally {
            executor.shutdown();
        }
    }



    private Map<String,String> binarySnowflakeNumber(long id){
        // 转成 64 位二进制，左补 0
        String binary = String.format("%64s", Long.toBinaryString(id)).replace(' ', '0');
        Map<String,String> map = new HashMap<>();
        // 切割字段：符号位 | 时间戳 | 数据中心ID | 机器ID | 序列号
        map.put("idBinary", binary);
        String signBit      = binary.substring(0, 1);
        map.put("signBit", signBit);
        String timestamp    = binary.substring(1, 42);
        map.put("timestamp", timestamp);
        String datacenterId = binary.substring(42, 47);
        map.put("datacenterId", datacenterId);
        String workerId     = binary.substring(47, 52);
        map.put("workerId", workerId);
        String sequence     = binary.substring(52, 64);
        map.put("sequence", sequence);
        return map;
    }


}

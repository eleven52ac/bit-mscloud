package com.bit.ai.controller;

import com.bit.common.core.dto.response.ApiResponse;
import com.bit.common.utils.core.IdGenerator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Datetime: 2025年11月18日15:54
 * @Author: Eleven52AC
 * @Description: IdGeneratorController 测试
 */
@Slf4j
@RestController
@RequestMapping("/test/generator/id")
public class IdGeneratorController {

    @Autowired
    private StringRedisTemplate redisTemplate;

    @GetMapping("")
    public ApiResponse<Long> getId() throws Exception{
        long id = IdGenerator.nextId(redisTemplate);
        log.info("id: {}", id);
        return ApiResponse.success(id);
    }
}

package com.bit.web.controller;

import com.bit.framework.ai.service.AiService;
import commons.response.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

import static commons.response.ApiUtils.success;

/**
 * @Datetime: 2025年10月28日15:58
 * @Author: Eleven52AC
 * @Description:
 */
@RestController
@RequestMapping("/test")
public class TestController {

    @Autowired
    private AiService aiService;

    @GetMapping("/test")
    public ApiResponse test() {
        String s = null;
        return success(s.toString());
    }



}

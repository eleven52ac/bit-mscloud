package com.bit.auth.controller;

import common.dto.response.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

import static common.dto.response.ApiUtils.success;


/**
 * @Datetime: 2025年10月28日15:58
 * @Author: Eleven52AC
 * @Description:
 */
@RestController
@RequestMapping("/test")
public class TestController {


    @GetMapping("/test")
    public ApiResponse test() {
        String s = null;
        return success(s.toString());
    }



}

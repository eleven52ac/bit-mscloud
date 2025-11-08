package com.bit.file.upload.controller;

import common.dto.response.ApiResponse;
import common.dto.response.ApiUtils;
import common.utils.DirectoryUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * @Datetime: 2025年05月11日04:12
 * @Author: Camellia.xiaohua/Bitspark
 * @Package: webservice.controller
 * @Project: SpringCloud
 * @Description:
 */

@RestController
@RequestMapping("/upload")
public class UploadController {

    /**
     * 获取一级子目录
     * @param dir
     * @return
     */
    @GetMapping("/directory")
    public ApiResponse getDirectory(@RequestParam(value = "dir", required = false) String dir) {
        try {
            List<Map<String, String>> result = DirectoryUtils.listFilesAndDirs(dir);
            return ApiUtils.success(result);
        } catch (IllegalArgumentException e) {
            return ApiUtils.error(e.getMessage());
        }
    }


    @PostMapping("/upload/usedir")
    public ApiResponse usedir(@RequestParam("path") String path) {
        // 在这里处理目录逻辑
        return new ApiResponse<>(200, "目录已使用: " + path);
    }







}

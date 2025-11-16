package com.bit.user.controller;

import com.bit.common.core.dto.response.ApiResponse;
import com.bit.common.utils.core.IdGenerator;
import com.bit.user.entity.UserLoginHistoryEntity;
import com.bit.user.service.UserLoginHistoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 用户登录记录
 * @Datetime: 2025年11月12日20:08
 * @Author: Eleven52AC
 * @Description: 用户登录记录控制类
 */
@RestController
@RequestMapping("/user/login/history")
@RequiredArgsConstructor
public class UserLoginHistoryController {

    @Autowired
    private UserLoginHistoryService userLoginHistoryService;

    /**
     * 获取用户最近登录记录（内部调用）
     * @Author: Eleven52AC
     * @Description:
     * @param userId
     * @return List<UserLoginHistoryEntity>
     */
    @GetMapping("/userId")
    public List<UserLoginHistoryEntity> recentLoginData(@RequestParam("userId") Long userId){
        List<UserLoginHistoryEntity> list = userLoginHistoryService.queryRecentLoginData(userId);
        return list;
    }

    /**
     * 保存当前登录记录
     * @Author: Eleven52AC
     * @Description:
     * @param record
     */
    @PostMapping("/save")
    public ApiResponse<String> saveCurrentLoginRecord(@RequestBody UserLoginHistoryEntity record){
        record.setId(IdGenerator.nextId());
        boolean saved = userLoginHistoryService.save(record);
        return saved ? ApiResponse.success("保存登录记录成功") : ApiResponse.error("保存登录记录失败");
    }
}

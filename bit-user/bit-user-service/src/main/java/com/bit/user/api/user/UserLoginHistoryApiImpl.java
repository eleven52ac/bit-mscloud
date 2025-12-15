package com.bit.user.api.user;

import cn.hutool.core.bean.BeanUtil;
import com.bit.common.core.dto.response.ApiResponse;
import com.bit.common.utils.core.IdGenerator;
import com.bit.user.api.user.dto.request.UserLoginHistoryRequest;
import com.bit.user.api.user.dto.response.UserLoginHistoryResponse;
import com.bit.user.constant.user.UserApiConstants;
import com.bit.user.repository.dataobject.user.UserLoginHistoryDo;
import com.bit.user.service.user.UserLoginHistoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;


@RestController
@RequestMapping(UserApiConstants.API_PREFIX) // RPC 前缀
public class UserLoginHistoryApiImpl implements UserLoginHistoryApi{

    @Autowired
    private UserLoginHistoryService userLoginHistoryService;

    @GetMapping({"/userId"})
    public List<UserLoginHistoryResponse> recentLoginData(@RequestParam("userId") Long userId){
        List<UserLoginHistoryDo> userLoginHistoryDos = userLoginHistoryService.queryRecentLoginData(userId);
        List<UserLoginHistoryResponse> responses = new ArrayList<>();
        for (UserLoginHistoryDo userLoginHistoryDo : userLoginHistoryDos){
            UserLoginHistoryResponse response = new UserLoginHistoryResponse();
            BeanUtil.copyProperties(userLoginHistoryDo, response);
            responses.add(response);
        }
        return responses;
    }

    @PostMapping({"/save"})
    public ApiResponse<String> saveCurrentLoginRecord(@RequestBody UserLoginHistoryRequest request){
        UserLoginHistoryDo record = new UserLoginHistoryDo();
        BeanUtil.copyProperties(request, record);
        record.setId(IdGenerator.nextId());
        boolean saved = userLoginHistoryService.save(record);
        return ApiResponse.success(saved ? "保存成功" : "保存失败");
    }
}

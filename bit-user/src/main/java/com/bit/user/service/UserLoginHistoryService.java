package com.bit.user.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.bit.user.dto.request.UserLoginHistoryRequest;
import com.bit.user.entity.UserLoginHistoryEntity;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
* @author camel
* @description 针对表【user_login_history(用户登录历史表)】的数据库操作Service
* @createDate 2025-11-09 16:03:18
*/
public interface UserLoginHistoryService extends IService<UserLoginHistoryEntity> {

    List<UserLoginHistoryEntity> queryRecentLoginData(Long userId);

    /**
     * 获取用户登录历史
     * @Author: Eleven52AC
     * @Description:
     * @param mybatisPage
     * @param request
     * @return
     */
    Page<UserLoginHistoryEntity> getUserLoginHistory(Page<UserLoginHistoryEntity> page, Long userId);
}

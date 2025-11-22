package com.bit.user.service.user;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.bit.user.repository.dataobject.user.UserLoginHistoryDo;

import java.util.List;

/**
* @author camel
* @description 针对表【user_login_history(用户登录历史表)】的数据库操作Service
* @createDate 2025-11-09 16:03:18
*/
public interface UserLoginHistoryService extends IService<UserLoginHistoryDo> {

    List<UserLoginHistoryDo> queryRecentLoginData(Long userId);

    /**
     * 获取用户登录历史
     * @Author: Eleven52AC
     * @Description:
     * @param mybatisPage
     * @param request
     * @return
     */
    Page<UserLoginHistoryDo> getUserLoginHistory(Page<UserLoginHistoryDo> page, Long userId);
}

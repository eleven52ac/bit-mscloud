package com.bit.auth.event;

import com.bit.auth.config.InternalTokenContext;
import com.bit.auth.message.MessageService;
import com.bit.user.api.model.UserInfoEntity;
import com.bit.user.api.model.UserLoginHistoryEntity;
import com.bit.user.api.service.UserLoginHistoryFeignClient;
import common.dto.reuqest.ClientMetaInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Component
public class UserLoginEventListener {

    @Autowired
    private UserLoginHistoryFeignClient userLoginHistoryFeignClient;

    @Autowired
    private MessageService messageService;

    @Async
    @EventListener
    public void handleUserLogin(UserLoginEvent event) {
        try{
            // 从 ClientMetaInfo 中取 token
            String token = event.getClientInfo().getInternalToken();
            if (token != null) {
                InternalTokenContext.set(token);
            }
            UserInfoEntity user = event.getUserInfo();
            ClientMetaInfo info = event.getClientInfo();
            // 查询上一次登录记录
            List<UserLoginHistoryEntity> lasts = userLoginHistoryFeignClient.recentLoginData(user.getUserId());
            // ip
            Set<String> ips = lasts.stream().map(UserLoginHistoryEntity::getIp).collect(Collectors.toSet());
            // 登录地
            Set<String> regions = lasts.stream().map(UserLoginHistoryEntity::getRegion).collect(Collectors.toSet());
            boolean suspicious = ips.contains(info.getIp());
            String remark = null;
            if (!suspicious){
                remark = "异地登录";
            }
            suspicious = regions.contains(info.getRegion());
            if (!suspicious){
                remark = "新登录地";
            }
            // 保存当前登录记录
            saveCurrentLoginRecord(user.getUserId(), info,  suspicious,  remark);
            // 异步提醒用户
            if (!suspicious) {
                log.warn("⚠️ 检测到用户 [{}] {}", user.getUsername(), remark);
                messageService.sendLoginAlert(user, info, remark);
            }
        }finally {
            InternalTokenContext.clear();
        }

    }

    /**
     *
     * @Author: Eleven52AC
     * @Description: 保存当前登录记录
     * @param userId 用户ID
     * @param info 客户端信息
     * @param suspicious 是否可疑
     * @param remark 备注
     */
    private void saveCurrentLoginRecord(Long userId, ClientMetaInfo info, boolean suspicious, String remark) {
        UserLoginHistoryEntity record = UserLoginHistoryEntity.builder().build()
                .setUserId(userId)
                .setIp(info.getIp())
                .setRegion(info.getRegion())
                .setDevice(info.getDevice())
                .setOs(info.getOs())
                .setIsp(info.getNetwork())
                .setLoginTime(LocalDateTime.now())
                .setIsSuspicious(suspicious ? 1 : 0)
                .setRemark(remark);
        userLoginHistoryFeignClient.saveCurrentLoginRecord(record);
    }


}

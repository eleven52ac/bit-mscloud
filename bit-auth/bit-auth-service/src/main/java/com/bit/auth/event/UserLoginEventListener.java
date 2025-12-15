package com.bit.auth.event;

import com.bit.user.api.user.UserLoginHistoryApi;
import com.bit.user.api.user.dto.request.UserLoginHistoryRequest;
import com.bit.user.api.user.dto.response.UserInfoResponse;
import com.bit.auth.config.InternalTokenContext;
import com.bit.auth.message.MessageService;
import com.bit.common.web.context.ClientMetaInfo;
import com.bit.user.api.model.UserLoginHistoryEntity;
import com.bit.user.api.service.UserLoginHistoryFeignClient;
import com.bit.user.api.user.dto.response.UserLoginHistoryResponse;
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
    private UserLoginHistoryApi userLoginHistoryApi;

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
            UserInfoResponse user = event.getUserInfo();
            ClientMetaInfo info = event.getClientInfo();
            // 查询上一次登录记录
            List<UserLoginHistoryResponse> lasts = userLoginHistoryApi.recentLoginData(user.getUserId());
            // ip
            Set<String> ips = lasts.stream().map(UserLoginHistoryResponse::getIp).collect(Collectors.toSet());
            // 登录地
            Set<String> regions = lasts.stream().map(UserLoginHistoryResponse::getRegion).collect(Collectors.toSet());
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
        UserLoginHistoryRequest record = UserLoginHistoryRequest.builder()
                .userId(userId)
                .ip(info.getIp())
                .region(info.getRegion())
                .device(info.getDevice())
                .os(info.getOs())
                .isp(info.getNetwork())
                .loginTime(LocalDateTime.now())
                .isSuspicious(suspicious ? 1 : 0)
                .remark(remark)
                .build();
        userLoginHistoryApi.saveCurrentLoginRecord(record);
    }



}

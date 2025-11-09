package com.bit.auth.message;

import com.bit.user.api.model.UserInfoEntity;
import common.dto.reuqest.ClientMetaInfo;
import common.utils.EmailSendUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Slf4j
@Service
public class MessageService {

    @Autowired
    private EmailSendUtils emailSendUtils;

    @Async
    public void sendLoginAlert(UserInfoEntity user, ClientMetaInfo info, String reason) {
        try {
            String msg = String.format("""
                    【安全提醒】
                    您的账户检测到 %s：
                    地区：%s
                    设备：%s
                    IP：%s
                    时间：%s
                    若非本人操作，请尽快修改密码。
                    """,
                    reason,
                    info.getRegion(),
                    info.getDevice(),
                    info.getIp(),
                    LocalDateTime.now()
            );
            emailSendUtils.sendHtmlEmail("安全提醒", msg, user.getEmail());
            log.info("📧 已发送安全提醒给用户 [{}]，原因：{}", user.getUsername(), reason);
        } catch (Exception e) {
            log.error("❌ 发送登录提醒失败：用户 [{}]，原因：{}，错误信息：{}",
                    user.getUsername(), reason, e.getMessage(), e);
        }
    }
}

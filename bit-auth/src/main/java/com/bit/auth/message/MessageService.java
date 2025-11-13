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
            String target = null;
            // 优先使用邮箱
            if (user.getEmail() != null && !user.getEmail().isBlank()) {
                target = user.getEmail();
            }// 否则使用手机号（假设你支持短信或可以作为备用通知）
            else if (user.getPhoneNumber() != null && !user.getPhoneNumber().isBlank()) {
                target = user.getPhoneNumber();
            }// 最后 fallback 到用户名（可能用来发送站内信或日志提示）
            else {
                target = user.getUsername();
            }
            emailSendUtils.sendHtmlEmail("安全提醒", msg, target);
            log.info("📧 已发送安全提醒给用户 [{}]，原因：{}", user.getUsername(), reason);
        } catch (Exception e) {
            log.error("❌ 发送登录提醒失败：用户 [{}]，原因：{}，错误信息：{}",
                    user.getUsername(), reason, e.getMessage(), e);
        }
    }
}

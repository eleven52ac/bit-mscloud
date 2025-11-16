package com.bit.common.core.enums;

import lombok.Getter;

@Getter
public enum EmailTemplateEnum {
    // 验证码邮件
    VERIFICATION_CODE_EMAIL_HTML(
            "<html>" +
                    "<head>" +
                    "<style>" +
                    "  body { font-family: Arial, sans-serif; color: #333; margin: 0; padding: 0; background-color: #f4f4f9; }" +
                    "  .container { width: ; max-width: 600px; margin: 20px auto; background-color: #ffffff; padding: 30px; border-radius: 8px; box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1); }" +  // 这里将 100% 改为 100%%
                    "  h1 { color: #4CAF50; font-size: 28px; margin: 0; }" +
                    "  p { font-size: 16px; line-height: 1.5; color: #333; margin: 10px 0; }" +
                    "  .footer { font-size: 12px; color: #888; text-align: center; margin-top: 20px; }" +
                    "  .verification-code { font-size: 32px; font-weight: bold; color: #4CAF50; padding: 10px; background-color: #e9f5e3; border-radius: 5px; text-align: center; }" +
                    "</style>" +
                    "</head>" +
                    "<body>" +
                    "<div class='container'>" +
                    "<h1>验证码验证</h1>" +
                    "<p>亲爱的用户，您好！</p>" +
                    "<p>感谢您使用我们的服务，您的验证码是：</p>" +
                    "<div class='verification-code'>%s</div>" +
                    "<p>请在五分钟内完成注册。若您未进行此操作，请忽略此邮件。</p>" +
                    "<p class='footer'>此邮件由系统自动发送，请勿回复。如需任何帮助，请通过camellia084@163.com联系我们</p>" +
                    "</div>" +
                    "</body>" +
                    "</html>",
            "登录验证"
    ),
    // 用户被封禁邮件通知
    USER_BANNED_EMAIL("用户你好，你已经被管理员封禁，封禁原因:%s", "封禁通知");
 
    private final String template;
    private final String subject;
 
    EmailTemplateEnum(String template, String subject) {
        this.template = template;
        this.subject = subject;
    }

    public String set(String captcha) {
        return String.format(this.template, captcha);
    }


}
package com.bit.common.utils.ict;

import jakarta.mail.Authenticator;
import jakarta.mail.PasswordAuthentication;
import jakarta.mail.Session;

import java.util.Properties;

/**
 * @Datetime: 2024/11/4下午3:50
 * @author: Camellia.xioahua
 */
public class EmailUtils {
    //邮件服务器地址
    private static String smtp = "smtp.163.com";
    // 登录用户名
    private static String username = "camellia084@163.com";
    // 登录口令
    private static String password = "DLiL2kNcruiAtVRH";

    private static Session session=null;

    public static Session getInstance(){
        // 连接到SMTP服务器465端口:
        Properties props = new Properties();
        props.put("mail.smtp.host", smtp); // SMTP主机名
        props.put("mail.smtp.port", "465"); // 主机端口号
        props.put("mail.smtp.auth", "true"); // 是否需要用户认证
        props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
        props.put("mail.smtp.ssl.enable", "true"); // 启用SSL
        session = Session.getInstance(props, new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(username, password);
            }
        });
        return session;
    }
}

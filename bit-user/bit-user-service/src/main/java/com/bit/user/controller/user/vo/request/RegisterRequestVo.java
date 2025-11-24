package com.bit.user.controller.user.vo.request;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Date;

/**
 * 用户注册请求参数
 * @Datetime: 2025年11月22日15:38
 * @Author: Eleven52AC
 */
@Data
public class RegisterRequestVo implements Serializable {

    @Serial
    private static final long serialVersionUID = -1167627986235887990L;

    /**
     * 注册类型
     */
    private String registerType;

    /**
     * 用户名，唯一，用于登录
     */
    private String username;

    /**
     * 密码
     */
    private String password;

    /**
     * 验证码
     */
    private String captcha;

    /**
     * 手机号
     */
    private String phoneNumber;

    /**
     * 邮箱地址
     */
    private String email;

    /**
     * 性别
     */
    private Object gender;

    /**
     * 出生日期
     */
    private Date dateOfBirth;

    /**
     * 推荐码
     */
    private String referralCode;
}

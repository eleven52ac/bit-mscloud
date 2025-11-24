package com.bit.user.repository.dataobject.user;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Date;

/**
 * 用户信息表
 * @TableName user_info
 */
@TableName(value ="user_info")
@Data
@Accessors(chain = true)
public class UserInfoDo implements Serializable {

    @Serial
    @TableField(exist = false)
    private static final long serialVersionUID = -7135456169406093948L;

    /**
     * 用户ID，唯一标识
     */
    @TableId(type = IdType.AUTO)
    private Long userId;

    /**
     * 用户名，唯一，用于登录
     */
    private String username;

    /**
     * 加密后的密码
     */
    private String password;

    /**
     * 手机号
     */
    private String phoneNumber;

    /**
     * 邮箱地址
     */
    private String email;

    /**
     * 名字
     */
    private String firstName;

    /**
     * 姓氏
     */
    private String lastName;

    /**
     * 性别
     */
    private Object gender;

    /**
     * 出生日期
     */
    private Date dateOfBirth;

    /**
     * 头像URL
     */
    private String avatarUrl;

    /**
     * 个性签名
     */
    private String signature;

    /**
     * 地址
     */
    private String address;

    /**
     * 创建时间
     */
    private LocalDateTime createdAt;

    /**
     * 更新时间
     */
    private LocalDateTime updatedAt;

    /**
     * 是否激活
     */
    private Integer isActive;

    /**
     * 逻辑删除标志
     */
    private Integer isDeleted;

    /**
     * 最后登录时间
     */
    private LocalDateTime lastLogin;

    /**
     * 登录次数
     */
    private Integer loginCount;

    /**
     * 用户角色
     */
    private String role;

    /**
     * 连续登录失败次数
     */
    private Integer failedLoginAttempts;

    /**
     * 锁定时间
     */
    private LocalDateTime lockTime;

    /**
     * 密码重置token
     */
    private String passwordResetToken;

    /**
     * token过期时间
     */
    private LocalDateTime passwordResetExpires;

    /**
     * 创建者ID
     */
    private Long createdBy;

    /**
     * 修改者ID
     */
    private Long updatedBy;

    /**
     * Facebook登录ID
     */
    private String facebookId;

    /**
     * Google登录ID
     */
    private String googleId;

    /**
     * Twitter登录ID
     */
    private String twitterId;

    /**
     * 最后活动时间
     */
    private LocalDateTime lastActivity;

    /**
     * 总消费金额
     */
    private BigDecimal totalSpent;

    /**
     * 会员等级
     */
    private String membershipLevel;

    /**
     * 是否已验证邮箱/手机
     */
    private Integer isVerified;

    /**
     * 是否被封禁
     */
    private Integer isBanned;

    /**
     * 封禁原因
     */
    private String banReason;

    /**
     * 语言地区
     */
    private String locale;

    /**
     * 时区
     */
    private String timezone;

    /**
     * 出生地
     */
    private String birthplace;

    /**
     * 推荐码
     */
    private String referralCode;

}
package com.bit.common.core.model;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 用户信息表，存储所有用户的基本信息、账户状态、登录记录、角色权限、社交账号和其他相关信息。
 */
@Data
public class UserInfo implements Serializable {

    @Serial
    private static final long serialVersionUID = -3793740871725596981L;

    /**
     * 用户ID，自动生成的唯一标识，每个用户在表中具有唯一的标识。
     */
    private Integer userId;

    /**
     * 用户名，最大50个字符，唯一，不能为空，用于用户登录和显示。
     */
    private String username;

    /**
     * 加密后的密码，最大255个字符，不能为空，存储加密后的用户密码。
     */
    private String password;

    /**
     * 用户手机号码，最多15个字符，存储用户的手机号码，可以为空。
     */
    private String phoneNumber;

    /**
     * 用户邮箱地址，最大100个字符，用于邮件联系及找回密码。
     */
    private String email;

    /**
     * 用户的名字，最大50个字符，可以为空，记录用户的名字。
     */
    private String firstName;

    /**
     * 用户的姓氏，最大50个字符，可以为空，记录用户的姓氏。
     */
    private String lastName;

    /**
     * 用户的性别，最大10个字符，可以为空，存储用户性别如 "Male", "Female" 等。
     */
    private String gender;

    /**
     * 用户的出生日期，存储为日期格式，可以为空。
     */
    private Date dateOfBirth;

    /**
     * 用户头像的URL，最大255个字符，可以为空，存储头像图片地址。
     */
    private String avatarUrl;

    /**
     * 用户的个人签名，允许自由文本，可以为空。
     */
    private String signature;

    /**
     * 用户的居住地址，最大255个字符，可以为空，记录用户的住址。
     */
    private String address;

    /**
     * 账户创建时间，记录用户账户创建的时间，默认当前时间。
     */
    private Date createdAt;

    /**
     * 账户的最后更新时间，记录用户账户信息的修改时间，默认当前时间。
     */
    private Date updatedAt;

    /**
     * 账户是否激活，标识用户账户是否已激活，默认为TRUE。
     */
    private Boolean isActive;

    /**
     * 账户是否已删除，标识用户账户是否被逻辑删除，默认为FALSE。
     */
    private Boolean isDeleted;

    /**
     * 用户最后一次登录的时间，可以为空，记录用户最近一次登录的时间。
     */
    private Date lastLogin;

    /**
     * 用户的登录次数，记录用户账户的登录次数，默认为0。
     */
    private Integer loginCount;

    /**
     * 用户角色，默认为普通用户（"user"），可以设置为 "admin", "moderator" 等角色，用于区分不同权限的用户。
     */
    private String role;

    /**
     * 用户连续失败登录次数，记录用户的失败登录次数，用于防止暴力破解。
     */
    private Integer failedLoginAttempts;

    /**
     * 账户锁定时间，记录用户账户被锁定的时间，用于处理频繁的失败登录。
     */
    private Date lockTime;

    /**
     * 密码重置令牌，用户请求重置密码时生成的唯一令牌，验证用户身份。
     */
    private String passwordResetToken;

    /**
     * 密码重置令牌的过期时间，记录密码重置令牌的有效期限，过期后无法使用。
     */
    private Date passwordResetExpires;

    /**
     * 记录创建该用户账户的管理员ID，用于追溯谁创建了该用户。
     */
    private Integer createdBy;

    /**
     * 记录最后修改该用户账户的管理员ID，用于追溯谁修改了该用户。
     */
    private Integer updatedBy;

    /**
     * Facebook社交登录ID，用于Facebook授权登录时使用。
     */
    private String facebookId;

    /**
     * Google社交登录ID，用于Google授权登录时使用。
     */
    private String googleId;

    /**
     * Twitter社交登录ID，用于Twitter授权登录时使用。
     */
    private String twitterId;

    /**
     * 记录用户最后一次活动的时间，帮助分析用户的活跃度。
     */
    private Date lastActivity;

    /**
     * 记录用户在平台上的总消费金额，用于统计消费情况。
     */
    private BigDecimal totalSpent;

    /**
     * 用户的会员等级，如 "gold", "silver" 等，默认为 "user"，用于标识用户在平台的权限级别。
     */
    private String membershipLevel;

    /**
     * 用户是否已完成验证，默认为FALSE，标识用户是否已通过邮箱或手机号验证。
     */
    private Boolean isVerified;

    /**
     * 用户账户是否已被禁用，默认为FALSE，禁用后无法登录或进行交易。
     */
    private Boolean isBanned;

    /**
     * 记录用户账户被禁用的原因，提供账户禁用的详细信息。
     */
    private String banReason;

    /**
     * 用户的地区设置，如 "en_US" 或 "zh_CN"，影响界面语言和日期格式等。
     */
    private String locale;

    /**
     * 用户的时区设置，如 "UTC+8"，影响时间的显示。
     */
    private String timezone;

    /**
     * 用户的出生地，最大100个字符，可以为空。
     */
    private String birthplace;

    /**
     * 用户的推荐码，用于社交推荐，最多50个字符。
     */
    private String referralCode;

}
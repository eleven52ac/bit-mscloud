package com.bit.user.api.user.dto.request;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * @Datetime: 2025年11月20日14:14
 * @Author: Eleven52AC
 * @Description: 用户登录记录请求参数
 */
@Data
public class UserLoginHistoryRequest implements Serializable {

    @Serial
    private static final long serialVersionUID = 98337738233131143L;

    private Long userId;

}

package com.bit.auth.event;

import com.bit.user.api.user.dto.response.UserInfoResponse;
import com.bit.common.web.context.ClientMetaInfo;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UserLoginEvent {

    /**
     * 用户信息
     */
    private final UserInfoResponse userInfo;

    /**
     * 客户端信息
     */
    private final ClientMetaInfo clientInfo;
}

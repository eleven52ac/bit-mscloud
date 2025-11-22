package com.bit.auth.event;

import com.bit.common.web.context.ClientMetaInfo;
import com.bit.user.api.model.UserInfoEntity;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UserLoginEvent {

    /**
     * 用户信息
     */
    private final UserInfoEntity userInfo;

    /**
     * 客户端信息
     */
    private final ClientMetaInfo clientInfo;
}

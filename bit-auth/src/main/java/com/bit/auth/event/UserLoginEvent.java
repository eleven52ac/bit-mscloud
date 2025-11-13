package com.bit.auth.event;

import com.bit.user.api.model.UserInfoEntity;
import common.dto.reuqest.ClientMetaInfo;
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

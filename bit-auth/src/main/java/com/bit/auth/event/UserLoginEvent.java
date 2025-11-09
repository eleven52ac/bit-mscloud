package com.bit.auth.event;

import com.bit.user.api.model.UserInfoEntity;
import common.dto.reuqest.ClientMetaInfo;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UserLoginEvent {
    private final UserInfoEntity userInfo;
    private final ClientMetaInfo clientInfo;
}

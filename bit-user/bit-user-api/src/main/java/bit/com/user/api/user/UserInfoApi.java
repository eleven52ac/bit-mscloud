package bit.com.user.api.user;

import bit.com.user.api.user.dto.response.UserInfoResponse;
import bit.com.user.constant.user.UserApiConstants;
import com.bit.common.core.dto.response.ApiResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * User Info API "RPC 服务 - 用户"
 * @Datetime: 2025年11月21日14:03
 * @Author: Eleven52AC
 */
@FeignClient(
        name = UserApiConstants.SERVICE_ID,
        path = UserApiConstants.API_PREFIX,
        contextId = "userInfoApi"
)
public interface UserInfoApi {


    /**
     * 根据用户名查询用户信息
     * @Author: Eleven52AC
     * @param username
     */
    @GetMapping("/username")
    ApiResponse<UserInfoResponse> getUserInfoByUsername(@RequestParam(name = "username") String username);


    /**
     * 根据邮箱查询用户信息
     * @Author: Eleven52AC
     * @param email
     */
    @GetMapping("/existsByEmail")
    ApiResponse<Boolean> existsByEmail(@RequestParam (name = "email") String email);


    /**
     * 根据邮箱查询用户信息
     * @Author: Eleven52AC
     * @param email
     */
    @GetMapping("/email")
    ApiResponse<UserInfoResponse> getUserInfoByEmail(@RequestParam ("email") String email);

}

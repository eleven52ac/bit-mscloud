package com.bit.auth.service.impl;

import com.bit.auth.service.CaptchaService;
import com.bit.auth.service.UserInfoService;
import cn.hutool.core.util.RandomUtil;
import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.IAcsClient;
import com.aliyuncs.dysmsapi.model.v20170525.SendSmsRequest;
import com.aliyuncs.dysmsapi.model.v20170525.SendSmsResponse;
import com.aliyuncs.exceptions.ClientException;
import com.aliyuncs.http.MethodType;
import com.aliyuncs.profile.DefaultProfile;
import com.aliyuncs.profile.IClientProfile;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import common.dto.response.ApiResponse;
import common.dto.response.ApiUtils;
import common.utils.BCryptUtil;
import common.utils.RegexUtils;
import commons.config.AliyunSmsConfig;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.bit.auth.entity.UserInfo;

import java.util.Date;

/**
 * @Datetime: 2025年04月01日22:19
 * @Author: Camellia.xiaohua/Bitspark
 * @Package: webservice.service
 * @Project: camellia
 * @Description:
 */
@Service("smsCaptchaServiceImpl")
@Slf4j
public class SmsCaptchaServiceImpl implements CaptchaService {

    @Autowired
    private AliyunSmsConfig aliyunSmsConfig;

    @Autowired
    private UserInfoService userInfoService;

    public Boolean sendCaptcha(String phoneNumber, HttpSession session) {
        try{
            // 3. 校验号码是否正确
            if (RegexUtils.isPhoneInvalid(phoneNumber)){
                log.info("手机号格式错误");
                return false;
            }
            // 4. 生成验证码
            String captcha = RandomUtil.randomNumbers(6);
            log.info("短信验证码：{}", captcha);
            // 5. 验证码保存到session中
            session.setAttribute("captcha", captcha);
            // 6. 发送短信
            IClientProfile profile = DefaultProfile.getProfile(aliyunSmsConfig.getRegionId(), aliyunSmsConfig.getAccessKeyId(), aliyunSmsConfig.getAccessKeySecret());
            DefaultProfile.addEndpoint(aliyunSmsConfig.getRegionId(), aliyunSmsConfig.getRegionId(), aliyunSmsConfig.getProduct(), aliyunSmsConfig.getDomain());
            IAcsClient acsClient = new DefaultAcsClient(profile);
            SendSmsRequest request = new SendSmsRequest();
            request.setMethod(MethodType.POST);
            request.setPhoneNumbers(phoneNumber);
            request.setSignName(aliyunSmsConfig.getSignName());
            request.setTemplateCode(aliyunSmsConfig.getTemplateCode());
            String templateParam = "{\"code\":\"" + captcha + "\"}";
            request.setTemplateParam(templateParam);
            SendSmsResponse sendSmsResponse = acsClient.getAcsResponse(request);
            if ((sendSmsResponse.getCode() != null) && (sendSmsResponse.getCode().equals("OK"))) {
                log.info("发送成功,code:" + sendSmsResponse.getCode());
                return true;
            } else {
                log.info("发送失败,code:" + sendSmsResponse.getCode());
                return false;
            }
        } catch (ClientException e) {
            log.error("发送失败,系统错误！", e);
            return false;
        }
    }

    @Override
    public ApiResponse<String> loginOrRegister(String phoneNumber, String password, String captcha, HttpSession session) {
        // 3. 手机号合法校验
        if (RegexUtils.isPhoneInvalid(phoneNumber)){
            return ApiUtils.error("手机号格式错误");
        }
        // 4. 验证码校验
        String rightCaptcha = (String) session.getAttribute("captcha");
        if (!captcha.equals(rightCaptcha)){
            return ApiUtils.error("验证码错误");
        }
        session.removeAttribute("captcha");
        // 5. 检查用户是否存在。
        QueryWrapper<UserInfo> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("phone_number", phoneNumber);
        UserInfo userInfo = userInfoService.getOne(queryWrapper);
        // 6. 用户不存在直接新建
        if (userInfo == null) {
            userInfo = new UserInfo();
            userInfo.setUsername(RandomUtil.randomString(10));
            userInfo.setPhoneNumber(phoneNumber);
            userInfo.setPassword(BCryptUtil.encode(password));
            userInfo.setLoginCount(1);
            userInfo.setCreatedAt(new Date());
            userInfoService.save(userInfo);
        } else{ // 7. 用户存在就校验密码
            boolean matchesResult = BCryptUtil.matches(password, userInfo.getPassword());
            if (!matchesResult){
                return ApiUtils.error("密码错误");
            }
        }
        session.setAttribute("user", userInfo);
        return ApiUtils.success("登录成功");
    }

}

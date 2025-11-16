//package com.bit.common.security.aop;
//
//import cn.hutool.core.util.StrUtil;
//import cn.hutool.json.JSONUtil;
//import common.constant.RedisConstants;
//import common.pojo.UserInfo;
//import common.utils.UserThreadLocal;
//import jakarta.servlet.http.HttpServletRequest;
//import jakarta.servlet.http.HttpSession;
//import org.aspectj.lang.JoinPoint;
//import org.aspectj.lang.annotation.*;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.data.redis.core.StringRedisTemplate;
//import org.springframework.stereotype.Component;
//
//
//@Aspect
//@Component
//public class LoginAspect {
//
//    @Autowired
//    private HttpServletRequest request;
//
//    @Autowired
//    private StringRedisTemplate redisTemplate;
//
//    @Pointcut("@annotation(common.annotation.CheckLogin) || @within(common.annotation.CheckLogin)")
//    public void loginCheckPointcut() {}
//
//    @Before("loginCheckPointcut()")
//    public void checkLogin(JoinPoint joinPoint) {
//        HttpSession session = request.getSession();
//        String token = request.getHeader("authorization");
//
//        UserInfo user = (UserInfo) session.getAttribute("user");
//        if (user != null) {
//            UserThreadLocal.setUserInfo(user);
//            return;
//        }
//
//        if (StrUtil.isBlank(token)) {
//            throw new RuntimeException("未登录");
//        }
//
//        String userJson = redisTemplate.opsForValue().get(RedisConstants.USER_INFO_PREFIX + token);
//        if (StrUtil.isBlank(userJson)) {
//            throw new RuntimeException("未登录或登录已过期");
//        }
//
//        user = JSONUtil.toBean(userJson, UserInfo.class);
//        UserThreadLocal.setUserInfo(user);
//    }
//
//    @AfterReturning("loginCheckPointcut()")
//    public void cleanThreadLocal() {
//        UserThreadLocal.removeUserInfo();
//    }
//}

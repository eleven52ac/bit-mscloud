//package common.security.aop;
//
//import com.fasterxml.jackson.core.JsonProcessingException;
//import com.fasterxml.jackson.databind.ObjectMapper;
//import jakarta.servlet.http.HttpServletRequest;
//import org.aspectj.lang.ProceedingJoinPoint;
//import org.aspectj.lang.annotation.*;
//import org.aspectj.lang.reflect.MethodSignature;
//import org.slf4j.*;
//import org.springframework.stereotype.Component;
//import org.springframework.web.context.request.RequestAttributes;
//import org.springframework.web.context.request.RequestContextHolder;
//import org.springframework.web.context.request.ServletRequestAttributes;
//
//import java.util.Arrays;
//
//@Aspect
//@Component
//public class LogAspect {
//
//    private static final Logger logger = LoggerFactory.getLogger(LogAspect.class);
//
//    private static final ObjectMapper objectMapper = new ObjectMapper();
//
//
//    /**
//     * 日志 AOP 拦截所有 controller 包下的方法
//     */
//    @Pointcut("execution(* *.controller..*(..))")
//    public void logPointcut() {}
//
//    @Around("logPointcut()")
//    public Object logAround(ProceedingJoinPoint joinPoint) throws Throwable {
//        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
//        String className = joinPoint.getTarget().getClass().getSimpleName();
//        String methodName = signature.getName();
//        String operation = className + "." + methodName;
//
//        Object[] args = joinPoint.getArgs();
//        String paramJson;
//        try {
//            paramJson = objectMapper.writeValueAsString(args);
//        } catch (JsonProcessingException e) {
//            paramJson = Arrays.toString(args);
//        }
//
//        // 请求信息（只在 Web 环境下可用）
//        RequestAttributes attrs = RequestContextHolder.getRequestAttributes();
//        if (attrs instanceof ServletRequestAttributes) {
//            HttpServletRequest request = ((ServletRequestAttributes) attrs).getRequest();
//            String ip = getClientIp(request);
//            String uri = request.getRequestURI();
//            String methodType = request.getMethod();
//            String userAgent = request.getHeader("User-Agent");
//            logger.info("▶【接收请求】IP: {} - 方法: {} - 路径: {} - 类: {} - UA: {}", ip, methodType, uri, operation, userAgent);
//        }
//
//        logger.info("🟢【开始】{}，参数：{}", operation, paramJson);
//        long startTime = System.currentTimeMillis();
//
//        try {
//            Object result = joinPoint.proceed();
//            long elapsed = System.currentTimeMillis() - startTime;
//
//            String resultJson;
//            try {
//                resultJson = objectMapper.writeValueAsString(result);
//            } catch (JsonProcessingException e) {
//                resultJson = String.valueOf(result);
//            }
//
//            logger.info("✅【完成】{}，耗时：{}ms，返回结果：{}", operation, elapsed, resultJson);
//            return result;
//
//        } catch (Throwable ex) {
//            long elapsed = System.currentTimeMillis() - startTime;
//            logger.error("❌【异常】{}，耗时：{}ms，错误信息：{}", operation, elapsed, ex.getMessage(), ex);
//            throw ex;
//        }
//    }
//
//    private String getClientIp(HttpServletRequest request) {
//        String[] headerKeys = {
//                "X-Forwarded-For", "Proxy-Client-IP", "WL-Proxy-Client-IP",
//                "HTTP_CLIENT_IP", "HTTP_X_FORWARDED_FOR"
//        };
//        for (String key : headerKeys) {
//            String ip = request.getHeader(key);
//            if (ip != null && ip.length() != 0 && !"unknown".equalsIgnoreCase(ip)) {
//                return ip.split(",")[0].trim();
//            }
//        }
//        return request.getRemoteAddr();
//    }
//
//
//}

package common.aop;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSON;
import cn.hutool.json.JSONUtil;
import common.utils.AESUtil;
import common.utils.HashUtils;
import common.utils.RSAUtils;
import commons.enums.RSASecretKey;
import jakarta.servlet.http.HttpServletRequest;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.HashMap;

import static com.fasterxml.jackson.databind.type.LogicalType.Map;

/**
 * @Datetime: 2025年06月04日21:45
 * @Author: Eleven也想AC
 * @Description: 安全通信校验切面
 */
@Aspect
@Component
public class EncryptCheckAspect {

    @Pointcut("@annotation(common.annotation.EncryptCheck) || @within(common.annotation.EncryptCheck)")
    public void encryptCheckPointcut() {}

    /**
     * 「可选加密通信 + 明文注入」的方案
     * @param joinPoint
     * @return
     * @throws UnsupportedEncodingException
     */
    @Around("encryptCheckPointcut()")
    public Object checkEncrypt(ProceedingJoinPoint joinPoint) throws Throwable {
        HttpServletRequest request = getRequest();
        // 密文（使用AES加密的密文）
        String requestBody = getRequestBody(request);
        HashMap requestBodyMap = JSONUtil.toBean(requestBody, HashMap.class);
        String encryptedBody = (String) requestBodyMap.get("ciphertext");
        // 密钥（使用RSA加密的AES密钥）
        String encryptedKey = request.getHeader("X-Encrypt-Key");
        // 签名（密文的哈希值）
        String signature = request.getHeader("X-Signature");
        // 时间戳
        String timestamp = request.getHeader("X-Timestamp");
        // 随机数
        String nonce = request.getHeader("X-Nonce");
        if (StrUtil.isAllBlank(encryptedBody, signature, timestamp)) {
            throw new SecurityException("缺少加密验证信息");
        }
        // 判断时间戳是否超时
        if (!checkTimestamp(timestamp)) {
            throw new SecurityException("请求过期");
        }
        // 计算密文的哈希值，与签名进行比对。
        String calculatedHash = HashUtils.calculateBase64SHA256(encryptedBody);
        if (!calculatedHash.equals(signature)) {
            throw new SecurityException("签名验证失败");
        }
        // 解密AES密钥。
        RSAUtils decryptor = new RSAUtils(RSASecretKey.RSA_SECRET_KEY_1.getPrivateKey());
        String AesKey = decryptor.decryptToString(encryptedKey);
        // 解密密文。
        String plainText = AESUtil.decrypt(encryptedBody, AESUtil.restoreKeyFromBase64(AesKey), AESUtil.Mode.GCM);
        System.out.println("明文：" + plainText);
        // 获取方法参数
        MethodSignature signatureObj = (MethodSignature) joinPoint.getSignature();
        Method method = signatureObj.getMethod();
        Class<?>[] parameterTypes = method.getParameterTypes();

        // 反序列化明文为原方法第一个参数类型
        Object[] args = joinPoint.getArgs();
        if (parameterTypes.length > 0 && StrUtil.isNotBlank(plainText)) {
            Object param = JSONUtil.toBean(plainText, parameterTypes[0]);
            args[0] = param;
        }

        // 继续执行原方法
        return joinPoint.proceed(args);
    }

    private boolean checkTimestamp(String timestamp) {
        // 简单检查，允许 5 分钟偏差
        long now = System.currentTimeMillis();
        long clientTime = parseTimestamp(timestamp);
        return Math.abs(now - clientTime) < 5 * 60 * 1000;
    }

    private long parseTimestamp(String ts) {
        try {
            return new SimpleDateFormat("yyyyMMddHHmmss").parse(ts).getTime();
        } catch (Exception e) {
            return 0;
        }
    }

    /**
     * 获取当前请求
     * @return
     */
    private HttpServletRequest getRequest() {
        ServletRequestAttributes attrs = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        return attrs.getRequest();
    }

    public static String getRequestBody(HttpServletRequest request) throws IOException {
        StringBuilder stringBuilder = new StringBuilder();
        BufferedReader reader = request.getReader();
        String line;
        while ((line = reader.readLine()) != null) {
            stringBuilder.append(line);
        }
        return stringBuilder.toString();
    }


}

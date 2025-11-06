package com.bit.auth;

import cn.hutool.core.util.CharsetUtil;
import cn.hutool.crypto.SecureUtil;
import cn.hutool.crypto.symmetric.AES;
import com.fasterxml.jackson.databind.ObjectMapper;
import common.constant.SaltConstants;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.File;
import java.io.IOException;
import java.util.Map;


@SpringBootTest
class WebServiceApplicationTests {

    @Test
    void contextLoads() {
        String content = "test中文";
        byte[] key = SaltConstants.SALT_KEY_1.getBytes();

        AES aes = SecureUtil.aes(key);

        byte[] encrypt = aes.encrypt(content);
// 解密
        byte[] decrypt = aes.decrypt(encrypt);

// 加密为16进制表示
        String encryptHex = aes.encryptHex(content);
// 解密为字符串
        String decryptStr = aes.decryptStr(encryptHex, CharsetUtil.CHARSET_UTF_8);
        System.out.println(decryptStr);
    }

    public static class JsonToMapReader {

        public static Map<String, Object> readJsonToMap(String filePath) {
            ObjectMapper mapper = new ObjectMapper();
            Map<String, Object> resultMap = null;
            try {
                resultMap = mapper.readValue(new File(filePath), Map.class);
            } catch (IOException e) {
                System.err.println("读取 JSON 文件失败: " + e.getMessage());
            }
            return resultMap;
        }

        public static void main(String[] args) {
            String filePath = "D:\\gongzufangyijianshi\\yinlian\\yinlianConf.json";
            Map<String, Object> configMap = readJsonToMap(filePath);

            if (configMap != null) {
                System.out.println("成功读取并封装到 Map:");
                for (Map.Entry<String, Object> entry : configMap.entrySet()) {
                    System.out.println(entry.getKey() + ": " + entry.getValue());
                }
            }
        }
    }
}

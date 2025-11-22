package com.bit.auth;//package com.bit.web;
//
//import cn.hutool.json.JSONUtil;
//import commons.enums.RSAAlgorithmEnum;
//import commons.utils.AESUtil;
//import commons.utils.HashUtils;
//import commons.utils.RSAUtils;
//import org.junit.jupiter.api.Test;
//import org.springframework.http.HttpEntity;
//import org.springframework.http.HttpHeaders;
//import org.springframework.http.MediaType;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.client.RestTemplate;
//
//import javax.crypto.Cipher;
//import javax.crypto.SecretKey;
//
//import java.nio.charset.StandardCharsets;
//import java.text.SimpleDateFormat;
//import java.util.*;
//
//import static commons.enums.RSASecretKey.RSA_SECRET_KEY_1;
//import static commons.utils.AESUtil.generateKey;
//
///**
// * @Datetime: 2025年06月05日14:25
// * @Author: Eleven也想AC
// * @Description: 模拟加密测试
// */
//public class EncryptCheckTest {
//
//    @Test
//    public void testEncryptCheck() throws Exception {
//        // 加密明文
//        String  plaintext =
//                "{\n" +
//                        "  \"voucher\": {\n" +
//                        "    \"shopId\": 1001,\n" +
//                        "    \"title\": \"夏日特惠券\",\n" +
//                        "    \"subTitle\": \"全场满减\",\n" +
//                        "    \"rules\": \"单笔消费满100元可用，限堂食\",\n" +
//                        "    \"payValue\": 500,\n" +
//                        "    \"actualValue\": 1000,\n" +
//                        "    \"type\": 0\n" +
//                        "  },\n" +
//                        "  \"seckillVoucher\": {\n" +
//                        "    \"stock\": 100,\n" +
//                        "    \"beginTime\": \"2025-06-01T00:00:00\",\n" +
//                        "    \"endTime\": \"2025-06-30T23:59:59\"\n" +
//                        "  }\n" +
//                        "}\n";
//        SecretKey aesKey = generateKey();
//        String ciphertext = AESUtil.encrypt(plaintext, aesKey, AESUtil.Mode.GCM);
//        Map<String, String> requestBodyMap = Map.of( "ciphertext", ciphertext);
//        System.out.println("使用AES加密的密文为："+ciphertext);
//        String aesKeyBase64 = Base64.getEncoder().encodeToString(aesKey.getEncoded());
//        // 加密AES密钥
//        Map<String, String> requestHeader = new HashMap<>();
//        RSAUtils decryptor = new RSAUtils(RSA_SECRET_KEY_1.getPrivateKey(), RSA_SECRET_KEY_1.getPublicKey());
//        String transformation = RSAAlgorithmEnum.OAEP_SHA256.getAlgorithm();
//        Cipher encryptCipher = Cipher.getInstance(transformation);
//        encryptCipher.init(Cipher.ENCRYPT_MODE, decryptor.getPublicKey());
//        byte[] encryptedBytes = encryptCipher.doFinal(aesKeyBase64.getBytes(StandardCharsets.UTF_8));
//        String encryptedAesKey = Base64.getEncoder().encodeToString(encryptedBytes);
//        requestHeader.put("X-Encrypt-Key", encryptedAesKey);
//        String signature = HashUtils.calculateBase64SHA256(ciphertext);
//        requestHeader.put("X-Signature", signature);
//        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
//        String timestamp = sdf.format(new Date());
//
//
//        RestTemplate restTemplate = new RestTemplate();
//        HttpHeaders headers = new HttpHeaders();
//        headers.setContentType(MediaType.APPLICATION_JSON);
//        headers.set("X-Encrypt-Key", encryptedAesKey);
//        headers.set("X-Signature", signature);
//        headers.set("X-Timestamp", timestamp);
//        headers.set("X-Nonce", UUID.randomUUID().toString());
//        HttpEntity<String> entity = new HttpEntity<>(JSONUtil.toJsonStr(requestBodyMap), headers);
//        ResponseEntity<String> response = restTemplate.postForEntity("http://100.97.223.54:19200/voucher/add/seckill", entity, String.class);
//        System.out.println("响应状态: " + response.getStatusCode());
//        System.out.println("响应体: " + response.getBody());
//
//    }
//}

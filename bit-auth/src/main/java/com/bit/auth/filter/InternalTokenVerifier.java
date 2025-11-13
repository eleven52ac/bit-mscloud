package com.bit.auth.filter;

import common.constant.SaltConstants;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

@Component
public class InternalTokenVerifier implements Filter {

    private static final String SECRET = SaltConstants.INTERNAL_AUTH_TOKEN_SECRET;
    private static final long EXPIRE_TIME_MS = 10 * 1000; // 10秒有效期

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest req = (HttpServletRequest) request;
        String tokenHeader = req.getHeader("X-Internal-Token");

        if (tokenHeader == null) {
            ((HttpServletResponse) response).sendError(403, "Missing internal token");
            return;
        }

        try {
            String decoded = new String(Base64.getDecoder().decode(tokenHeader), StandardCharsets.UTF_8);
            String[] parts = decoded.split("\\|");
            if (parts.length != 3) throw new IllegalArgumentException("Invalid token format");

            String path = parts[0];
            long timestamp = Long.parseLong(parts[1]);
            String signature = parts[2];

            // 校验时间
            if (System.currentTimeMillis() - timestamp > EXPIRE_TIME_MS) {
                ((HttpServletResponse) response).sendError(403, "Token expired");
                return;
            }

            // 校验签名
            String expectedSig = hmacSha256(path + "|" + timestamp, SECRET);
            if (!expectedSig.equals(signature)) {
                ((HttpServletResponse) response).sendError(403, "Invalid signature");
                return;
            }

            chain.doFilter(request, response);
        } catch (Exception e) {
            ((HttpServletResponse) response).sendError(403, "Invalid token");
        }
    }

    private String hmacSha256(String data, String secret) throws Exception {
        Mac mac = Mac.getInstance("HmacSHA256");
        mac.init(new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), "HmacSHA256"));
        return Base64.getEncoder().encodeToString(mac.doFinal(data.getBytes(StandardCharsets.UTF_8)));
    }
}

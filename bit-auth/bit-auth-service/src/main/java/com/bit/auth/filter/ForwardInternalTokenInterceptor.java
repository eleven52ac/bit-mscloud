package com.bit.auth.filter;

import com.bit.auth.config.InternalTokenContext;
import feign.RequestInterceptor;
import feign.RequestTemplate;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Component
public class ForwardInternalTokenInterceptor implements RequestInterceptor {

    @Override
    public void apply(RequestTemplate template) {
        // 优先从 ThreadLocal 拿
        String internalToken = InternalTokenContext.get();

        // 如果当前线程没放，就从请求上下文取（普通 HTTP 请求时）
        if (internalToken == null) {
            ServletRequestAttributes attributes =
                    (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            if (attributes != null) {
                HttpServletRequest request = attributes.getRequest();
                internalToken = request.getHeader("X-Internal-Token");
            }
        }

        if (internalToken != null) {
            template.header("X-Internal-Token", internalToken);
        }
    }
}

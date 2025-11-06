package common.annotation;

import java.lang.annotation.*;

/**
 * @Datetime: 2025年06月04日21:48
 * @Author: Eleven也想AC
 * @Description: 安全通信注解
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface EncryptCheck {
}

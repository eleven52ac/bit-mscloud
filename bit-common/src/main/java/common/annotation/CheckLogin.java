package common.annotation;

import java.lang.annotation.*;

/**
 * CheckLogin注解用于标记需要进行登录检查的方法或类
 * 它的主要作用是通知拦截器或方法调用者，执行该方法或类中的方法之前需要进行登录状态的检查
 *
 * @author [CAMELLIA]
 * @since [1.0]
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface CheckLogin {
}

package common.utils;

import common.utils.spring.SpringBeanUtils;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;

public final class MessageUtils {

    private static final MessageSource messageSource = SpringBeanUtils.getBean(MessageSource.class);

    private MessageUtils() {}

    public static String get(String code, Object... args) {
        return messageSource.getMessage(code, args, LocaleContextHolder.getLocale());
    }

    public static String getOrDefault(String code, String defaultMessage, Object... args) {
        try {
            return get(code, args);
        } catch (Exception e) {
            return defaultMessage;
        }
    }
}

//package com.bit.common.core.exception.base;
//
//
//import org.apache.commons.lang3.StringUtils;
//
//import java.io.Serializable;
//
///**
// * 基础业务异常（兼容国际化 + 模块区分）
// */
//public class BaseException extends RuntimeException implements Serializable {
//
//    private static final long serialVersionUID = 6031765678585908529L;
//
//    /**
//     * 所属模块
//     */
//    private final String module;
//
//    /**
//     * 错误码
//     */
//    private final String code;
//
//    /**
//     * 错误码对应的参数
//     */
//    private final Object[] args;
//
//    /**
//     * 错误消息
//     */
//    private final String defaultMessage;
//
//    public BaseException(String module, String code, Object[] args, String defaultMessage) {
//        super(defaultMessage);
//        this.module = module;
//        this.code = code;
//        this.args = args;
//        this.defaultMessage = defaultMessage;
//    }
//
//    public BaseException(String module, String code, Object[] args) {
//        this(module, code, args, null);
//    }
//
//    public BaseException(String module, String defaultMessage) {
//        this(module, null, null, defaultMessage);
//    }
//
//    public BaseException(String code, Object[] args) {
//        this(null, code, args, null);
//    }
//
//    public BaseException(String defaultMessage) {
//        this(null, null, null, defaultMessage);
//    }
//
//    @Override
//    public String getMessage() {
//        String message = null;
//        if (StringUtils.isNotEmpty(code)) {
//            message = MessageUtils.get(code, args);
//        }
//        return message != null ? message : defaultMessage;
//    }
//
//    public String module() {
//        return module;
//    }
//
//    public String code() {
//        return code;
//    }
//
//    public Object[] args() {
//        return args;
//    }
//
//    public String defaultMessage() {
//        return defaultMessage;
//    }
//}

package com.bit.common.core.enums;

/**
 * @Datetime: 2025年11月09日17:27
 * @Author: Eleven52AC
 * @Description: 注册方式枚举
 */
public enum RegisterTypeEnum {

    PHONE_CAPTCHA("phone_code", "手机验证码注册"),
    EMAIL_CAPTCHA("email_code", "邮箱验证码注册");

    private final String code;

    private final String desc;

    RegisterTypeEnum(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public static RegisterTypeEnum fromCode(String code) {
        for (RegisterTypeEnum type : values()) {
            if (type.code.equals(code)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Unknown BusinessPdfTypeEnum code: " + code);
    }

    public String findCodeByDesc(String desc) {
        for (RegisterTypeEnum value : RegisterTypeEnum.values()) {
            if (value.desc.equals(desc)) {
                return value.code;
            }
        }
        return null;
    }

    public String findDescByCode(String code) {
        for (RegisterTypeEnum value : RegisterTypeEnum.values()) {
            if (value.code.equals(code)) {
                return value.desc;
            }
        }
        return null;
    }

}

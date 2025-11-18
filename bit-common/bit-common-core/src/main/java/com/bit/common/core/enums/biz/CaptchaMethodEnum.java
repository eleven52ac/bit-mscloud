package com.bit.common.core.enums.biz;

/**
 * @Datetime: 2025年11月09日17:53
 * @Author: Eleven52AC
 * @Description: 验证码枚举
 */
public enum CaptchaMethodEnum {


    PHONE_CAPTCHA("phone_captcha", "手机验证码"),
    EMAIL_CAPTCHA("email_captcha", "邮箱验证码");

    private final String code;

    private final String desc;

    CaptchaMethodEnum(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public static CaptchaMethodEnum fromCode(String code) {
        for (CaptchaMethodEnum type : values()) {
            if (type.code.equals(code)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Unknown BusinessPdfTypeEnum code: " + code);
    }

    public String findCodeByDesc(String desc) {
        for (CaptchaMethodEnum value : CaptchaMethodEnum.values()) {
            if (value.desc.equals(desc)) {
                return value.code;
            }
        }
        return null;
    }

    public String findDescByCode(String code) {
        for (CaptchaMethodEnum value : CaptchaMethodEnum.values()) {
            if (value.code.equals(code)) {
                return value.desc;
            }
        }
        return null;
    }

}

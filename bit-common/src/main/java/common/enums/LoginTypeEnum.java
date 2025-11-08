package common.enums;

/**
 * @Datetime: 2025年11月08日14:47
 * @Author: Eleven52AC
 * @Description: 登录方式枚举
 */
public enum LoginTypeEnum {

    USERNAME_PASSWORD("username_password", "用户名密码登录"),
    EMAIL_PASSWORD("email_password", "邮箱密码登录"),
    PHONE_PASSWORD("phone_password", "手机密码登录"),
    PHONE_CODE("phone_code", "手机验证码登录"),
    EMAIL_CODE("email_code", "邮箱验证码登录");

    private final String code;

    private final String desc;

    LoginTypeEnum(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public static LoginTypeEnum fromCode(String code) {
        for (LoginTypeEnum type : values()) {
            if (type.code.equals(code)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Unknown BusinessPdfTypeEnum code: " + code);
    }

    public String findCodeByDesc(String desc) {
        for (LoginTypeEnum value : LoginTypeEnum.values()) {
            if (value.desc.equals(desc)) {
                return value.code;
            }
        }
        return null;
    }

    public String findDescByCode(String code) {
        for (LoginTypeEnum value : LoginTypeEnum.values()) {
            if (value.code.equals(code)) {
                return value.desc;
            }
        }
        return null;
    }

}

package com.bit.common.core.enums;

public enum PdfTypeEnum {

    BZDX_FAMILY_CHANGE_INFO("bzdx_family_change_info", "公廉租房家庭变更PDF"),
    BZDX_FAMILY_QUALIFICATION_RETRIAL("bzdx_family_qualification_retrial", "家庭资格登记PDF");

    private final String code;
    private final String description;

    PdfTypeEnum(String code, String description) {
        this.code = code;
        this.description = description;
    }

    public String getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }

    public static PdfTypeEnum fromCode(String code) {
        for (PdfTypeEnum type : values()) {
            if (type.code.equals(code)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Unknown BusinessPdfTypeEnum code: " + code);
    }
}

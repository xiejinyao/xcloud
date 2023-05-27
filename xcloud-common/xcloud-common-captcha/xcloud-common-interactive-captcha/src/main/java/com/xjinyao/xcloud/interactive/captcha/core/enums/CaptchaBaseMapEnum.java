package com.xjinyao.xcloud.interactive.captcha.core.enums;

/**
 * 底图类型枚举
 */
public enum CaptchaBaseMapEnum {

    ORIGINAL("ORIGINAL", "滑动拼图底图"),
    SLIDING_BLOCK("SLIDING_BLOCK", "滑动拼图滑块底图"),
    PIC_CLICK("PIC_CLICK", "文字点选底图");

    private String codeValue;
    private String codeDesc;

    CaptchaBaseMapEnum(String codeValue, String codeDesc) {
        this.codeValue = codeValue;
        this.codeDesc = codeDesc;
    }

    public String getCodeValue() {
        return this.codeValue;
    }

    public String getCodeDesc() {
        return this.codeDesc;
    }

    /**
     * 根据codeValue获取枚举
     *
     * @param codeValue
     * @return
     */
    public static CaptchaBaseMapEnum parseFromCodeValue(String codeValue) {
        for (CaptchaBaseMapEnum e : CaptchaBaseMapEnum.values()) {
            if (e.codeValue.equals(codeValue)) {
                return e;
            }
        }
        return null;
    }

    /**
     * 根据codeValue获取描述
     *
     * @param codeValue
     * @return
     */
    public static String getCodeDescByCodeBalue(String codeValue) {
        CaptchaBaseMapEnum enumItem = parseFromCodeValue(codeValue);
        return enumItem == null ? "" : enumItem.getCodeDesc();
    }

    /**
     * 验证codeValue是否有效
     *
     * @param codeValue
     * @return
     */
    public static boolean validateCodeValue(String codeValue) {
        return parseFromCodeValue(codeValue) != null;
    }

    /**
     * 列出所有值字符串
     *
     * @return
     */
    public static String getString() {
        StringBuffer buffer = new StringBuffer();
        for (CaptchaBaseMapEnum e : CaptchaBaseMapEnum.values()) {
            buffer.append(e.codeValue).append("--").append(e.getCodeDesc()).append(", ");
        }
        buffer.deleteCharAt(buffer.lastIndexOf(","));
        return buffer.toString().trim();
    }

}

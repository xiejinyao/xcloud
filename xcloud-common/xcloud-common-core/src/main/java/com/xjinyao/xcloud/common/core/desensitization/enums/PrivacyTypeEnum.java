package com.xjinyao.xcloud.common.core.desensitization.enums;

/**
 * 隐私数据类型枚举
 *
 * @author 谢进伟
 * @createDate 2023/1/4 18:41
 */
public enum PrivacyTypeEnum {

    /**
     * 自定义（此项需设置脱敏的范围）
     */
    CUSTOMER,

    /**
     * 姓名
     */
    NAME,

    /**
     * 身份证号
     */
    ID_CARD,

    /**
     * 手机号
     */
    PHONE,

    /**
     * 邮箱
     */
    EMAIL,
}

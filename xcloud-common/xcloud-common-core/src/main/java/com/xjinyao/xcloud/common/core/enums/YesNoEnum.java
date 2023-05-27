package com.xjinyao.xcloud.common.core.enums;

import lombok.Getter;

import java.util.Arrays;

/**
 * 是否枚举(字典分组Id:10,字典分组编码:yes_no)
 * <p>
 * 所有枚举项的值应该与数据库数据字典对应，若数据字典更新，请更新此枚举类
 * </p>
 */
public enum YesNoEnum {

    Y(1, "是", "是"),
    N(0, "否", "否");

    /**
     * 枚举值对应数据字典的值
     */
    @Getter
    private final Integer value;
    /**
     * 枚举值对应数据字典的标签
     */
    @Getter
    private final String label;
    /**
     * 枚举值对应数据字典的备注
     */
    @Getter
    private final String remark;

    /**
     * 枚举
     *
     * @param value  值
     * @param label  标签
     * @param remark 备注
     */
    YesNoEnum(Integer value, String label, String remark) {
        this.value = value;
        this.label = label;
        this.remark = remark;
    }

    /**
     * 比较value是否相同
     *
     * @param value 需要比较的value值
     * @return Boolean
     */
    public boolean valueEquals(Integer value) {
        return this.value.equals(value);
    }

    /**
     * 通过值构建枚举
     *
     * @param value 枚举值
     * @return YesNoEnum
     */
    public static YesNoEnum ofByValue(Integer value) {
        return Arrays.stream(YesNoEnum.values())
                .filter(d -> d.valueEquals(value))
                .findFirst()
                .orElse(null);
    }

    public interface Codes {
        String Y = "1";
        String N = "0";
    }

    public interface Labels {
        String Y = "是";
        String N = "否";
    }
}

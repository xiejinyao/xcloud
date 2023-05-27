package com.xjinyao.xcloud.admin.api.enums;

import lombok.Getter;

/**
 * @author 谢进伟
 * @description 系统任务状态枚举，系统任务状态(字典分组Id:27)枚举，所有枚举项的值应该与数据库数据字典对应，若数据字典更新，请更新此枚举类
 * @createDate 2021/3/3 18:54
 */
public enum SysTaskStatusEnum {

    /**
     * 待处理
     */
    PENDING(1, "待处理"),
    /**
     * 处理中
     */
    PROCESSING(2, "处理中"),
    /**
     * 已处理
     */
    PROCESSED(3, "已处理");

    /**
     * 枚举值对应数据字典的值
     */
    @Getter
    private Integer value;
    @Getter
    private String remark;

    SysTaskStatusEnum(Integer value, String remark) {
        this.value = value;
        this.remark = remark;
    }

    /**
     * 比较value是否相同
     *
     * @param value 需要比较的value值
     * @return
     */
    public boolean valueEquals(Integer value) {
        return this.value.equals(value);
    }

    public static SysTaskStatusEnum ofByValue(Integer value) {
        for (SysTaskStatusEnum e : SysTaskStatusEnum.values()) {
            if (e.valueEquals(value)) {
                return e;
            }
        }
        return null;
    }
}

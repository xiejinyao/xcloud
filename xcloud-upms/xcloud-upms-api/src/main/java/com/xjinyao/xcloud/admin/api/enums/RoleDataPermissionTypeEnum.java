package com.xjinyao.xcloud.admin.api.enums;

import lombok.Getter;

import java.io.Serializable;

/**
 * 角色的数据权限的权重值越小优先级越大,所有角色数据权限不能并行使用，只会使用权重最高的角色数据权限
 *
 * @author 谢进伟
 * @description 角色数据权限类型(字典分组Id : 43)枚举，所有枚举项的值应该与数据库数据字典对应，若数据字典更新，请更新此枚举类
 * @createDate 2021/5/19 16:19
 */
public enum RoleDataPermissionTypeEnum implements Serializable {

    /**
     * 无任何权限
     */
    NONE(-1, Integer.MAX_VALUE, "无数据权限"),
    /**
     * 全部
     */
    ALL(0, 6, "全部"),
    /**
     * 自定义
     */
    CUSTOM(1, 5, "自定义"),
    /**
     * 同级节点及同级所有子节点
     */
    SIBLINGS_AND_CHILDREN(2, 4, "同级节点及同级所有子节点"),
    /**
     * 同级节点
     */
    SIBLINGS(3, 3, "同级节点"),
    /**
     * 自身及其子节点
     */
    SELF_AND_CHILDREN(4, 2, "自身及其子节点"),
    /**
     * 自身节点
     */
    SELF(5, 1, "自身节点");

    /**
     * 枚举值对应数据字典的值
     */
    @Getter
    private Integer value;
    @Getter
    private Integer weight;
    @Getter
    private String remark;

    RoleDataPermissionTypeEnum(Integer value, Integer weight, String remark) {
        this.value = value;
        this.weight = weight;
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

    public static RoleDataPermissionTypeEnum ofByValue(Integer value) {
        for (RoleDataPermissionTypeEnum e : RoleDataPermissionTypeEnum.values()) {
            if (e.valueEquals(value)) {
                return e;
            }
        }
        return null;
    }
}

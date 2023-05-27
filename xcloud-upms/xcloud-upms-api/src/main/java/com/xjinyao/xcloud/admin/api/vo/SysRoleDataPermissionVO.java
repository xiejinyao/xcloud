package com.xjinyao.xcloud.admin.api.vo;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @author 谢进伟
 * @description 角色数据权限
 * @createDate 2021/5/25 11:20
 */
@Data
@Builder
public class SysRoleDataPermissionVO implements Serializable {

    /**
     * 维度的枚举值
     */
    private String dimensionValue;

    /**
     * 数据标识值
     */
    private List<String> identifierValues;
}

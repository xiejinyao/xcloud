package com.xjinyao.xcloud.admin.api.dto;

import com.xjinyao.xcloud.admin.api.entity.SysRole;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * @author 谢进伟
 * @description 角色（包含角色的数据权限）
 * @createDate 2021/5/21 16:21
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ApiModel(value = "角色（包含角色的数据权限）", parent = SysRole.class)
public class RoleDTO extends SysRole {

    /**
     * 新增的数据权限维度数据
     */
    @ApiModelProperty(value = "新增的数据权限维度数据")
    private List<RoleDataPermissionDimensionIdentifier> addDimensionIdentifiers;
    /**
     * 修改的数据权限维度数据
     */
    @ApiModelProperty(value = "修改的数据权限维度数据")
    private List<RoleDataPermissionDimensionIdentifier> deleteDimensionIdentifiers;
}

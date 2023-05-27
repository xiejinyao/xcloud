package com.xjinyao.xcloud.admin.api.vo;

import com.xjinyao.xcloud.admin.api.entity.SysRole;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author 谢进伟
 * @description 用户角色分配详情
 * @createDate 2020/5/22 9:53
 */
@Data
@ApiModel(parent = SysRole.class)
public class RoleForUserVO extends SysRole {

    /**
     * 是否可取消
     */
    @ApiModelProperty(value = "是否可取消")
    private Boolean isCanCancel;
}

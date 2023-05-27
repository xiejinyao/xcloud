package com.xjinyao.xcloud.admin.api.vo;

import com.baomidou.mybatisplus.annotation.TableField;
import com.xjinyao.xcloud.admin.api.entity.SysRole;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author 谢进伟
 * @description 用户角色
 * @createDate 2021/5/8 15:57
 */
@Data
@ApiModel(value = "用户角色", parent = SysRole.class)
public class SysUserRoleVO extends SysRole {

    /**
     * 是否可取消
     */
    @ApiModelProperty(value = "是否可取消")
    @TableField(value = "is_can_cancel")
    private Boolean isCanCancel;
}

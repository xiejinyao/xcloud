package com.xjinyao.xcloud.admin.api.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import com.github.thinwonton.mybatis.metamodel.core.annotation.GenMetaModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * <p>
 * 角色菜单表
 * </p>
 *
 * @since 2019/2/1
 */
@Data
@GenMetaModel
@EqualsAndHashCode(callSuper = true)
public class SysRoleMenu extends Model<SysRoleMenu> {

    private static final long serialVersionUID = 1L;

    /**
     * 角色ID
     */
    @ApiModelProperty(value = "角色id")
    @TableField(value = "role_id")
    private Long roleId;

    /**
     * 菜单ID
     */
    @ApiModelProperty(value = "菜单id")
    @TableField(value = "menu_id")
    private Long menuId;

    /**
     * 支持再授权
     */
    @ApiModelProperty(value = "支持再授权")
    @TableField(value = "re_auth")
    private Boolean reAuth;

}

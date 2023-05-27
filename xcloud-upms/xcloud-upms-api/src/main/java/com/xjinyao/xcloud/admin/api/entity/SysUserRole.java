package com.xjinyao.xcloud.admin.api.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import com.github.thinwonton.mybatis.metamodel.core.annotation.GenMetaModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * <p>
 * 用户角色表
 * </p>
 *
 * @since 2019/2/1
 */
@Data
@GenMetaModel
@EqualsAndHashCode(callSuper = true)
public class SysUserRole extends Model<SysUserRole> {

    private static final long serialVersionUID = 1L;

    /**
     * 用户ID
     */
    @ApiModelProperty(value = "用户id")
    @TableField(value = "user_id")
    private Integer userId;

    /**
     * 角色ID
     */
    @ApiModelProperty(value = "角色id")
    @TableField(value = "role_id")
    private Integer roleId;

    /**
     * 是否可取消
     */
    @ApiModelProperty(value = "是否可取消")
    @TableField(value = "is_can_cancel")
    private Boolean isCanCancel;

}

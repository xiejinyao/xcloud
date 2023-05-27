package com.xjinyao.xcloud.admin.api.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import com.github.thinwonton.mybatis.metamodel.core.annotation.GenMetaModel;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * 角色数据权限
 *
 * @author 谢进伟
 * @date 2021-05-19 16:55:55
 */
@Data
@GenMetaModel
@TableName("sys_role_data_permission")
@EqualsAndHashCode(callSuper = true)
@ApiModel(value = "角色数据权限")
public class SysRoleDataPermission extends Model<SysRoleDataPermission> {
    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    @TableId("id")
    @ApiModelProperty(value = "主键")
    private Integer id;
    /**
     * 维度
     */
    @ApiModelProperty(value = "维度")
    @TableField(value = "dimension")
    private String dimension;
    /**
     * 数据标识值
     */
    @ApiModelProperty(value = "数据标识值")
    @TableField(value = "identifier_value")
    private String identifierValue;
    /**
     * 角色id
     */
    @ApiModelProperty(value = "角色id")
    @TableField(value = "role_id")
    private Integer roleId;
    /**
     * 备注
     */
    @ApiModelProperty(value = "备注")
    @TableField(value = "remark")
    private String remark;
    /**
     * 创建时间
     */
    @ApiModelProperty(value = "创建时间")
    @TableField(value = "create_time")
    private LocalDateTime createTime;
    /**
     * 最后一次修改时间
     */
    @ApiModelProperty(value = "最后一次修改时间")
    @TableField(value = "update_time")
    private LocalDateTime updateTime;
}

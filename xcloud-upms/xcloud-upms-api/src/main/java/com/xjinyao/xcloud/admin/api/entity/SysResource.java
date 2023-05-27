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
 * 资源管理
 *
 * @author 谢进伟
 * @date 2021-02-23 12:40:36
 */
@Data
@GenMetaModel
@TableName("sys_resource")
@EqualsAndHashCode(callSuper = true)
@ApiModel(value = "资源管理")
public class SysResource extends Model<SysResource> {
    private static final long serialVersionUID = 1L;

    /**
     * 主键id
     */
    @TableId
    @ApiModelProperty(value = "主键id")
    @TableField(value = "id")
    private Long id;
    /**
     * 资源类型
     */
    @ApiModelProperty(value = "资源类型")
    @TableField(value = "type")
    private Integer type;
    /**
     * 资源编码
     */
    @ApiModelProperty(value = "资源编码")
    @TableField(value = "code")
    private String code;
    /**
     * 资源名称
     */
    @ApiModelProperty(value = "资源名称")
    @TableField(value = "name")
    private String name;
    /**
     * 备注
     */
    @ApiModelProperty(value = "备注")
    @TableField(value = "remark")
    private String remark;
    /**
     * 删除标识
     */
    @ApiModelProperty(value = "删除标识")
    @TableField(value = "is_deleted")
    private String isDeleted;
    /**
     * 创建时间
     */
    @ApiModelProperty(value = "创建时间")
    @TableField(value = "create_time")
    private LocalDateTime createTime;
    /**
     * 更新时间
     */
    @ApiModelProperty(value = "更新时间")
    @TableField(value = "update_time")
    private LocalDateTime updateTime;
}

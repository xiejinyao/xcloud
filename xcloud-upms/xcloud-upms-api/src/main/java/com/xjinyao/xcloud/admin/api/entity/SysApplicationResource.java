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
 * 应用资源管理
 *
 * @author 谢进伟
 * @date 2021-02-23 11:58:04
 */
@Data
@GenMetaModel
@TableName("sys_application_resource")
@EqualsAndHashCode(callSuper = true)
@ApiModel(value = "应用资源管理")
public class SysApplicationResource extends Model<SysApplicationResource> {
    private static final long serialVersionUID = 1L;

    /**
     * 主键id
     */
    @TableId
    @ApiModelProperty(value = "主键id")
    @TableField(value = "id")
    private Long id;
    /**
     * 资源id
     */
    @ApiModelProperty(value = "资源id")
    @TableField(value = "resource_id")
    private Long resourceId;
    /**
     * 应用id
     */
    @ApiModelProperty(value = "应用id")
    @TableField(value = "application_id")
    private Integer applicationId;
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
}

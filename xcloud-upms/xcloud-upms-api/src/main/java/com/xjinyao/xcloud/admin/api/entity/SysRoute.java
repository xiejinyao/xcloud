package com.xjinyao.xcloud.admin.api.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.github.thinwonton.mybatis.metamodel.core.annotation.GenMetaModel;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * 系统路由表实体类
 *
 * @author 谢进伟
 * @since 2020-10-17
 */
@Data
@GenMetaModel
@TableName("sys_route")
@EqualsAndHashCode(callSuper = true)
@ApiModel(value = "SysRoute对象", description = "系统路由表")
public class SysRoute extends Model<SysRoute> {

    /**
     * 主键id
     */
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "主键id")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 接口名称
     */
    @ApiModelProperty(value = "接口名称")
    private String name;
    /**
     * 路径前缀
     */
    @ApiModelProperty(value = "路径前缀")
    private String path;
    /**
     * 地址
     */
    @ApiModelProperty(value = "地址")
    private String url;
    /**
     * 服务ID
     */
    @ApiModelProperty(value = "服务ID")
    @TableField("service_id")
    private String serviceId;
    /**
     * API状态
     */
    @ApiModelProperty(value = "API状态")
    private Boolean status;
    /**
     * 删除标识
     */
    @TableLogic
    @ApiModelProperty(value = "删除标识")
    @TableField("is_deleted")
    private Boolean isDeleted;
    /**
     * 租户ID
     */
    @ApiModelProperty(value = "租户ID")
    @TableField("tenant_id")
    private Integer tenantId;

    /**
     * 创建人
     */
    @ApiModelProperty(value = "创建人")
    @TableField("create_by")
    private String createBy;

    /**
     * 更新人
     */
    @ApiModelProperty(value = "更新人")
    @TableField("update_by")
    private String updateBy;

    /**
     * 创建时间
     */
    @ApiModelProperty(value = "创建时间")
    @TableField("create_time")
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    @ApiModelProperty(value = "更新时间")
    @TableField("update_time")
    private LocalDateTime updateTime;

}

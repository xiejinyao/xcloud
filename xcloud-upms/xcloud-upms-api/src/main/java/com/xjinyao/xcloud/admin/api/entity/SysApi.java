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
 * 系统接口表实体类
 *
 * @author 谢进伟
 * @since 2020-10-14
 */
@Data
@GenMetaModel
@TableName("sys_api")
@EqualsAndHashCode(callSuper = true)
@ApiModel(value = "SysApi对象", description = "系统接口表")
public class SysApi extends Model<SysApi> {
    /**
     * 主键id
     */
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "主键id")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 接口编码
     */
    @ApiModelProperty(value = "接口编码")
    private String code;
    /**
     * 接口名称
     */
    @ApiModelProperty(value = "接口名称")
    private String name;
    /**
     * 接口描述
     */
    @ApiModelProperty(value = "接口描述")
    private String notes;
    /**
     * 请求方法
     */
    @ApiModelProperty(value = "请求方法")
    private String method;
    /**
     * 类名
     */
    @ApiModelProperty(value = "类名")
    @TableField("class_name")
    private String className;
    /**
     * 方法名
     */
    @ApiModelProperty(value = "方法名")
    @TableField("method_name")
    private String methodName;
    /**
     * 请求路径
     */
    @ApiModelProperty(value = "请求路径")
    private String path;
    /**
     * 请求路径匹配模式
     */
    @ApiModelProperty(value = "请求路径匹配模式")
    private String pattern;
    /**
     * 响应类型
     */
    @ApiModelProperty(value = "响应类型")
    @TableField("content_type")
    private String contentType;
    /**
     * 服务ID
     */
    @ApiModelProperty(value = "服务ID")
    @TableField("service_id")
    private String serviceId;
    /**
     * API状态:0:禁用 1:启用
     */
    @ApiModelProperty(value = "API状态:0:禁用 1:启用")
    private Boolean status;
    /**
     * 是否认证
     */
    @ApiModelProperty(value = "是否认证")
    private Boolean auth;
    /**
     * 权限编码
     */
    @ApiModelProperty(value = "权限编码")
    @TableField("auth_code")
    private String authCode;
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

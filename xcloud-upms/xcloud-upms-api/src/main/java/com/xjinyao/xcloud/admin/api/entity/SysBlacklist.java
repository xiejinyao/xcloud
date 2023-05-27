package com.xjinyao.xcloud.admin.api.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
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
 * 系统黑名单表实体类
 *
 * @author 谢进伟
 * @since 2020-08-26
 */
@Data
@GenMetaModel
@TableName("sys_blacklist")
@EqualsAndHashCode(callSuper = true)
@ApiModel(value = "SysBlacklist对象", description = "系统黑名单表")
public class SysBlacklist extends Model<SysBlacklist> {

    /**
     * 主键id
     */
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "主键id")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * IP地址
     */
    @ApiModelProperty(value = "IP地址")
    private String ip;
    /**
     * 请求地址
     */
    @ApiModelProperty(value = "请求地址")
    @TableField("request_uri")
    private String requestUri;
    /**
     * 请求方法
     */
    @ApiModelProperty(value = "请求方法")
    @TableField("request_method")
    private String requestMethod;
    /**
     * 开始时间
     */
    @ApiModelProperty(value = "开始时间")
    @TableField("start_time")
    private String startTime;
    /**
     * 结束时间
     */
    @ApiModelProperty(value = "结束时间")
    @TableField("end_time")
    private String endTime;

    /**
     * 状态
     */
    @ApiModelProperty(value = "状态")
    private Boolean status;

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

package com.xjinyao.xcloud.admin.api.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.github.thinwonton.mybatis.metamodel.core.annotation.GenMetaModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 * 日志表
 * </p>
 *
 * @since 2019/2/1
 */
@Data
@GenMetaModel
public class SysLog implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 编号
     */
    @TableId(value = "id", type = IdType.AUTO)
    @ApiModelProperty(value = "日志编号")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;

    /**
     * 日志类型
     */
    @NotBlank(message = "日志类型不能为空")
    @ApiModelProperty(value = "日志类型")
    private String type;

    /**
     * 日志标题
     */
    @NotBlank(message = "日志标题不能为空")
    @ApiModelProperty(value = "日志标题")
    private String title;

    /**
     * 创建者
     */
    @ApiModelProperty(value = "创建人", accessMode = ApiModelProperty.AccessMode.READ_ONLY)
    @TableField(value = "create_by")
    private String createBy;

    /**
     * 创建时间
     */
    @ApiModelProperty(value = "创建时间", accessMode = ApiModelProperty.AccessMode.READ_ONLY)
    @TableField(value = "create_time")
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    @ApiModelProperty(value = "更新时间")
    @TableField(value = "update_time")
    private LocalDateTime updateTime;

    /**
     * 操作IP地址
     */
    @ApiModelProperty(value = "操作ip地址")
    @TableField(value = "remote_addr")
    private String remoteAddr;

    /**
     * 用户浏览器
     */
    @ApiModelProperty(value = "用户代理")
    @TableField(value = "user_agent")
    private String userAgent;

    /**
     * 请求URI
     */
    @ApiModelProperty(value = "请求uri")
    @TableField(value = "request_uri")
    private String requestUri;

    /**
     * 操作方式
     */
    @ApiModelProperty(value = "操作方式")
    private String method;

    /**
     * 操作提交的数据
     */
    @ApiModelProperty(value = "数据")
    private String params;

    /**
     * 操作提交的body数据
     */
    @ApiModelProperty(value = "body数据")
    private String body;

    /**
     * 执行时间
     */
    @ApiModelProperty(value = "方法执行时间")
    private Long time;

    /**
     * 异常信息
     */
    @ApiModelProperty(value = "异常信息")
    private String exception;

    /**
     * 服务ID
     */
    @ApiModelProperty(value = "应用标识")
    @TableField(value = "service_id")
    private String serviceId;

    /**
     * 删除标记
     */
    @TableLogic
    @TableField(value = "del_flag")
    private Boolean delFlag;

}

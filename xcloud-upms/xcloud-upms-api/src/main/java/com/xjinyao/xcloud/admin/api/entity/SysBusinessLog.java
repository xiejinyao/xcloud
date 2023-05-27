package com.xjinyao.xcloud.admin.api.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.github.thinwonton.mybatis.metamodel.core.annotation.GenMetaModel;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.Tolerate;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Date;

/**
 * 业务日志表
 *
 * @TableName sys_business_log
 */
@TableName(value = "sys_business_log")
@Data
@GenMetaModel
@Builder
public class SysBusinessLog implements Serializable {
    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
    /**
     * ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    /**
     * 项目ID
     */
    @TableField(value = "project_id")
    private String projectId;
    /**
     * 业务标志
     */
    @TableField(value = "type")
    private Integer type;
    /**
     * 关联各种表的id
     */
    @TableField(value = "pk_id")
    private String pkId;
    /**
     * 标题
     */
    @TableField(value = "title")
    private String title;
    /**
     * 操作内容
     */
    @TableField(value = "details")
    private String details;
    /**
     * 操作人Id
     */
    @TableField(value = "operation_user_id")
    private String operationUserId;
    /**
     * 操作人名称
     */
    @TableField(value = "operation_user_name")
    private String operationUserName;
    /**
     * 参数
     */
    @TableField(value = "params")
    private String params;
    /**
     * 结果
     */
    @TableField(value = "result")
    private String result;
    /**
     * 操作时间
     */
    @TableField(value = "operation_time")
    private LocalDateTime operationTime;
    /**
     * 创建人
     */
    @TableField(value = "create_user")
    private String createUser;
    /**
     * 创建时间
     */
    @TableField(value = "create_time")
    private LocalDateTime createTime;
    /**
     * 更新人
     */
    @TableField(value = "update_user")
    private String updateUser;
    /**
     * 更新时间
     */
    @TableField(value = "update_time")
    private LocalDateTime updateTime;
    /**
     * 是否删除
     */
    @TableField(value = "del_flag")
    private Boolean delFlag;
    /**
     * 版本
     */
    @TableField(value = "version")
    private Integer version;

    @Tolerate
    public SysBusinessLog() {
    }
}
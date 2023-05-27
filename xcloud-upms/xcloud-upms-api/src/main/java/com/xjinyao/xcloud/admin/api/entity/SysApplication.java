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
 * 应用管理
 *
 * @author 谢进伟
 * @date 2021-02-23 11:25:53
 */
@Data
@GenMetaModel
@TableName("sys_application")
@EqualsAndHashCode(callSuper = true)
@ApiModel(value = "应用管理")
public class SysApplication extends Model<SysApplication> {
    private static final long serialVersionUID = 1L;

    /**
     * 主键id
     */
    @TableId
    @ApiModelProperty(value = "主键id")
    @TableField(value = "id")
    private Long id;
    /**
     * 应用编码
     */
    @ApiModelProperty(value = "应用编码")
    @TableField(value = "code")
    private String code;
    /**
     * 应用名称
     */
    @ApiModelProperty(value = "应用名称")
    @TableField(value = "name")
    private String name;
    /**
     * appId
     */
    @ApiModelProperty(value = "appId")
    @TableField(value = "app_id")
    private String appId;
    /**
     * appSecret
     */
    @ApiModelProperty(value = "appSecret")
    @TableField(value = "app_secret")
    private String appSecret;
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
     * 是否可编辑啊
     */
    @ApiModelProperty(value = "是否可编辑啊")
    @TableField(value = "is_can_edit")
    private Boolean isCanEdit;
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

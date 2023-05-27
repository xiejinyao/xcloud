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

/**
 * 系统序列表
 *
 * @author 刘元林
 * @date 2021-03-30 18:42:29
 */
@Data
@GenMetaModel
@TableName("sys_sequence")
@EqualsAndHashCode(callSuper = true)
@ApiModel(value = "系统序列表")
public class SysSequence extends Model<SysSequence> {
    private static final long serialVersionUID = 1L;

    /**
     * 主键id
     */
    @TableId
    @ApiModelProperty(value = "主键id")
    @TableField(value = "id")
    private Integer id;
    /**
     * 序列名称
     */
    @ApiModelProperty(value = "序列名称")
    @TableField(value = "name")
    private String name;
    /**
     * 当前值
     */
    @ApiModelProperty(value = "当前值")
    @TableField(value = "currentValue")
    private Long currentvalue;
    /**
     * 递进步长
     */
    @ApiModelProperty(value = "递进步长")
    @TableField(value = "increment")
    private Long increment;
    /**
     * 版本
     */
    @ApiModelProperty(value = "版本")
    @TableField(value = "version")
    private Integer version;
    /**
     *
     */
    @ApiModelProperty(value = "")
    @TableField(value = "remark")
    private String remark;
}

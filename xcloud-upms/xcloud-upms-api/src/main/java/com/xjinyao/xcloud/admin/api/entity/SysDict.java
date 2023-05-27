package com.xjinyao.xcloud.admin.api.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import com.github.thinwonton.mybatis.metamodel.core.annotation.GenMetaModel;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * 字典表
 *
 * @date 2019/03/19
 */
@Data
@GenMetaModel
@ApiModel(value = "字典类型")
@EqualsAndHashCode(callSuper = true)
public class SysDict extends Model<SysDict> {

    private static final long serialVersionUID = 1L;

    /**
     * 编号
     */
    @TableId
    @ApiModelProperty(value = "字典编号")
    private Integer id;

    /**
     * 类型
     */
    @ApiModelProperty(value = "字典类型")
    private String type;

    /**
     * 描述
     */
    @ApiModelProperty(value = "字典描述")
    private String description;

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
     * 是否是系统内置
     */
    @TableField(value = "`system`")
    @ApiModelProperty(value = "是否系统内置")
    private String system;

    /**
     * 备注信息
     */
    @ApiModelProperty(value = "备注信息")
    private String remark;

    /**
     * 是否启用
     */
    @ApiModelProperty(value = "是否启用")
    @TableField(value = "enabled")
    private Boolean enabled;

    /**
     * 删除标记
     */
    @TableLogic
    @ApiModelProperty(value = "删除标记,1:已删除,0:正常")
    @TableField(value = "del_flag")
    private Boolean delFlag;

}

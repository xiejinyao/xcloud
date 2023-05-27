package com.xjinyao.xcloud.admin.api.entity;

import cn.hutool.core.util.BooleanUtil;
import cn.hutool.core.util.NumberUtil;
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
 * 字典项
 *
 * @date 2019/03/19
 */
@Data
@GenMetaModel
@ApiModel(value = "字典项")
@EqualsAndHashCode(callSuper = true)
public class SysDictItem extends Model<SysDictItem> {

    private static final long serialVersionUID = 1L;

    /**
     * 编号
     */
    @TableId
    @ApiModelProperty(value = "字典项id")
    private Integer id;

    /**
     * 所属字典类id
     */
    @ApiModelProperty(value = "所属字典类id")
    @TableField(value = "dict_id")
    private Integer dictId;

    /**
     * 枚举字段编码
     */
    @ApiModelProperty(value = "枚举字段编码")
    @TableField(value = "enum_code")
    private String enumCode;

    /**
     * 数据值
     */
    @ApiModelProperty(value = "数据值")
    @TableField(value = "value")
    private String value;

    /**
     * 标签名
     */
    @ApiModelProperty(value = "标签名")
    private String label;

    /**
     * 类型
     */
    @ApiModelProperty(value = "类型")
    private String type;

    /**
     * 排序（升序）
     */
    @ApiModelProperty(value = "排序值，默认升序")
    private Integer sort;

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
     * 备注信息
     */
    @ApiModelProperty(value = "备注信息")
    @TableField(value = "remark")
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

    public void setValue(String value) {
        this.value = value;
        if (NumberUtil.isInteger(this.value)) {
            this.setDictValue(Integer.parseInt(this.value));
        } else if (Boolean.TRUE.toString().equalsIgnoreCase(this.value) || Boolean.FALSE.toString().equalsIgnoreCase(this.value)) {
            this.setDictValue(BooleanUtil.toBoolean(this.value));
        } else {
            this.setDictValue(this.value);
        }
    }

    @TableField(exist = false)
    @ApiModelProperty(value = "实际类型数据字典值")
    private Object dictValue;

}

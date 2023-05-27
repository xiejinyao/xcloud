package com.xjinyao.xcloud.area.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import com.github.thinwonton.mybatis.metamodel.core.annotation.GenMetaModel;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 行政区域
 *
 * @author 谢进伟
 * @date 2020-05-05 11:35:14
 */
@Data
@GenMetaModel
@TableName("sys_area")
@EqualsAndHashCode(callSuper = true)
@ApiModel(value = "行政区域")
public class Area extends Model<Area> {
    protected static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    @TableId(value = "id", type = IdType.INPUT)
    @ApiModelProperty(value = "主键")
    protected String id;
    /**
     * 国家统计局编号
     */
    @ApiModelProperty(value = "国家统计局编号")
    @TableField(value = "code")
    protected String code;
    /**
     * 上级id
     */
    @ApiModelProperty(value = "上级id")
    @TableField(value = "parent_id")
    protected String parentId;
    /**
     * 上级国家统计局编号
     */
    @ApiModelProperty(value = "上级国家统计局编号")
    @TableField(value = "parent_code")
    protected Long parentCode;
    /**
     * 层级
     */
    @ApiModelProperty(value = "层级")
    @TableField(value = "level")
    protected Integer level;
    /**
     * 名称
     */
    @ApiModelProperty(value = "名称")
    @TableField(value = "name")
    protected String name;
    /**
     * 上级名称
     */
    @ApiModelProperty(value = "上级名称")
    @TableField(value = "pname")
    protected String pname;
    /**
     * 全拼
     */
    @ApiModelProperty(value = "全拼")
    @TableField(value = "pinyin")
    protected String pinyin;
    /**
     * 拼音缩写
     */
    @ApiModelProperty(value = "拼音缩写")
    @TableField(value = "pinyin_short")
    protected String pinyinShort;
    /**
     * 首写字母
     */
    @ApiModelProperty(value = "首写字母")
    @TableField(value = "first_char")
    protected String firstChar;
    /**
     * 经度
     */
    @ApiModelProperty(value = "经度")
    @TableField(value = "longitude")
    protected BigDecimal longitude;
    /**
     * 维度
     */
    @ApiModelProperty(value = "维度")
    @TableField(value = "latitude")
    protected BigDecimal latitude;
    /**
     * 邮政编码
     */
    @ApiModelProperty(value = "邮政编码")
    @TableField(value = "zip_code")
    protected String zipCode;
    /**
     * 创建时间
     */
    @ApiModelProperty(value = "创建时间", accessMode = ApiModelProperty.AccessMode.READ_ONLY)
    @TableField(value = "create_time")
    protected LocalDateTime createTime;
    /**
     * 创建人
     */
    @ApiModelProperty(value = "创建人", accessMode = ApiModelProperty.AccessMode.READ_ONLY)
    @TableField(value = "create_user_id")
    protected Integer createUserId;
    /**
     * 数据版本
     */
    @Version
    @ApiModelProperty(value = "数据版本", hidden = true)
    @TableField(value = "version")
    protected Integer version;
}

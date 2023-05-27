package com.xjinyao.xcloud.file.api.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.Version;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import com.xjinyao.xcloud.common.core.util.BeanUtils;
import com.xjinyao.xcloud.file.api.vo.SysFileVO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * 文件库
 *
 * @author 谢进伟
 * @date 2020-05-15 14:49:59
 */
@Data
@TableName("sys_file")
@EqualsAndHashCode(callSuper = true)
@ApiModel(value = "文件库")
public class SysFile extends Model<SysFile> {
	private static final long serialVersionUID = 1L;

	/**
	 * 主键
	 */
	@TableId("id")
	@ApiModelProperty(value = "主键")
	private Integer id;
	/**
	 * 业务编码
	 */
	@ApiModelProperty(value = "业务编码")
	@TableField(value = "business_code")
	private String businessCode;
	/**
	 * 原始名称
	 */
	@ApiModelProperty(value = "原始名称")
	@TableField(value = "original_name")
	private String originalName;
	/**
	 * 自定义名称
	 */
	@ApiModelProperty(value = "自定义名称")
	@TableField(value = "custom_name")
	private String customName;
	/**
	 * 相对路径
	 */
	@ApiModelProperty(value = "相对路径")
	@TableField(value = "relative_path")
	private String relativePath;
	/**
	 * 类型
	 */
	@ApiModelProperty(value = "类型")
	@TableField(value = "file_type")
	private String fileType;
	/**
	 * 存储类型
	 */
	@ApiModelProperty(value = "存储类型")
	@TableField(value = "store_type")
	private String storeType;
	/**
	 * url
	 */
	@ApiModelProperty(value = "url")
	@TableField(value = "url")
	private String url;
	/**
	 * 访问前缀
	 */
	@ApiModelProperty(value = "访问前缀")
	@TableField(value = "prefix")
	private String prefix;
	/**
	 * 大小
	 */
	@ApiModelProperty(value = "大小")
	@TableField(value = "size")
	private Double fileSize;
	/**
	 * 上传人
	 */
	@ApiModelProperty(value = "上传人")
	@TableField(value = "create_user_id")
	private Integer createUserId;
	/**
	 * 备注
	 */
	@ApiModelProperty(value = "备注")
	@TableField(value = "remark")
	private String remark;
	/**
	 * 上传时间
	 */
	@ApiModelProperty(value = "上传时间")
	@TableField(value = "create_time")
	private LocalDateTime createTime;
	/**
	 * 数据版本
	 */
	@Version
	@ApiModelProperty(value = "数据版本", hidden = true)
	@TableField(value = "version")
	private Integer version;

	/**
	 * 转换成SysFileVO对象
	 *
	 * @return
	 */
	public SysFileVO convertToSysFileVO() {
		SysFileVO vo = new SysFileVO();
		return BeanUtils.copyPropertiesAndGetTarget(this, vo);
	}
}

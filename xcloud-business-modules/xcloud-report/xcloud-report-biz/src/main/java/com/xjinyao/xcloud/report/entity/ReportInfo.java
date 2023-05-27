package com.xjinyao.xcloud.report.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 报表信息
 *
 * @TableName sys_report_info
 */
@TableName(value = "sys_report_info")
@Data
public class ReportInfo implements Serializable {
	@TableField(exist = false)
	private static final long serialVersionUID = 1L;
	/**
	 * 主键
	 */
	@TableId(value = "id", type = IdType.AUTO)
	private Integer id;
	/**
	 * 项目Id
	 */
	@TableField(value = "project_id")
	private String projectId;
	/**
	 * 报表类型
	 */
	@TableField(value = "type")
	private Integer type;
	/**
	 * 报表名称
	 */
	@TableField(value = "name")
	private String name;
	/**
	 * 文件名称
	 */
	@TableField(value = "file_name")
	private String fileName;
	/**
	 * 模板内容
	 */
	@TableField(value = "tpl_content")
	private String tplContent;
	/**
	 * 备注信息
	 */
	@TableField(value = "description")
	private String description;
	/**
	 * 预览参数声明配置
	 */
	@TableField(value = "preview_params_declaration_config")
	private String previewParamsDeclarationConfig;
	/**
	 * 搜索表单配置
	 */
	@TableField(value = "search_form_config")
	private String searchFormConfig;
	/**
	 * 是否是模板
	 */
	@TableField(value = "is_template")
	private Boolean isTemplate;
	/**
	 * 是否可见
	 */
	@TableField(value = "visible")
	private Boolean visible;
	/**
	 * 预览时立即加载数据
	 */
	@TableField(value = "preview_immediately_load")
	private Boolean previewImmediatelyLoad;
	/**
	 * 乐观锁
	 */
	@TableField(value = "version")
	private Integer version;
	/**
	 * 创建人
	 */
	@TableField(value = "create_user")
	private String createUser;
	/**
	 * 创建人姓名
	 */
	@TableField(value = "create_user_name")
	private String createUserName;
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
	 * 修改人姓名
	 */
	@TableField(value = "update_user_name")
	private String updateUserName;
	/**
	 * 更新时间
	 */
	@TableField(value = "update_time")
	private LocalDateTime updateTime;
}
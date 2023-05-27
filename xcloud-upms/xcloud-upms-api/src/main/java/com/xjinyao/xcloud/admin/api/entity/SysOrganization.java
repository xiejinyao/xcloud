package com.xjinyao.xcloud.admin.api.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.github.thinwonton.mybatis.metamodel.core.annotation.GenMetaModel;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.Tolerate;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 组织管理
 *
 * @TableName sys_organization
 */
@TableName(value = "sys_organization")
@Data
@GenMetaModel
@Builder
public class SysOrganization implements Serializable {
	@TableField(exist = false)
	private static final long serialVersionUID = 1L;
	/**
	 * 主键
	 */
	@TableId(value = "id", type = IdType.ASSIGN_UUID)
	private String id;
	/**
	 * 企业Id
	 */
	@TableField("enterprise_id")
	private String enterpriseId;
	/**
	 * 组织名称
	 */
	@TableField(value = "name")
	private String name;
	/**
	 * 组织全名称
	 */
	@TableField(value = "full_name")
	private String fullName;
	/**
	 * 排序
	 */
	@TableField(value = "sort")
	private Integer sort;
	/**
	 * 创建时间
	 */
	@TableField(value = "create_time")
	private LocalDateTime createTime;
	/**
	 * 修改时间
	 */
	@TableField(value = "update_time")
	private LocalDateTime updateTime;
	/**
	 * 是否删除  -1：已删除  0：正常
	 */
	@TableLogic
	@TableField(value = "del_flag")
	private Boolean delFlag;
	/**
	 * 父级组织id
	 */
	@TableField(value = "parent_id")
	private String parentId;
	/**
	 * 备注
	 */
	@TableField(value = "remark")
	private String remark;
	/**
	 * 编码
	 */
	@TableField(value = "code")
	private String code;
	/**
	 * 组织性质
	 */
	@TableField(value = "nature")
	private String nature;
	/**
	 * 组织坐标经纬度
	 */
	@TableField(value = "coordinates")
	private String coordinates;
	/**
	 * 所有父级组织id路径,各个id之间使用-隔开
	 */
	@TableField(value = "parent_id_path")
	private String parentIdPath;
	/**
	 * 是否可删除
	 */
	@TableField(value = "is_can_del")
	private Boolean isCanDel;
	/**
	 * 是否可选择
	 */
	@TableField(value = "is_can_select")
	private Boolean isCanSelect;

	@Tolerate
	public SysOrganization() {
	}
}
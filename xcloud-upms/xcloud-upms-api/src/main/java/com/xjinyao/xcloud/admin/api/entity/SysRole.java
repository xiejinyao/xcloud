package com.xjinyao.xcloud.admin.api.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import com.github.thinwonton.mybatis.metamodel.core.annotation.GenMetaModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;
import java.util.List;

/**
 * <p>
 * 角色表
 * </p>
 *
 * @since 2019/2/1
 */
@Data
@GenMetaModel
@EqualsAndHashCode(callSuper = true)
public class SysRole extends Model<SysRole> {

	private static final long serialVersionUID = 1L;

	@TableId(value = "role_id", type = IdType.AUTO)
	@ApiModelProperty(value = "角色编号")
	private Integer roleId;

	@ApiModelProperty(value = "上级角色")
	@TableField(value = "parent_role_id")
	private Integer parentRoleId;

	@ApiModelProperty(value = "上级角色id路径")
	@TableField(value = "parent_role_id_path")
	private String parentRoleIdPath;

	@NotBlank(message = "角色名称 不能为空")
	@ApiModelProperty(value = "角色名称")
	@TableField(value = "role_name")
	private String roleName;

	@NotBlank(message = "角色分组 不能为空")
	@ApiModelProperty(value = "角色分组")
	@TableField(value = "role_group")
	private String roleGroup;

	@NotBlank(message = "角色标识 不能为空")
	@ApiModelProperty(value = "角色标识")
	@TableField(value = "role_code")
	private String roleCode;

	@NotBlank(message = "角色描述 不能为空")
	@ApiModelProperty(value = "角色描述")
	@TableField(value = "role_desc")
	private String roleDesc;

	@ApiModelProperty(value = "是否可编辑")
	@TableField(value = "is_can_edit")
	private Boolean isCanEdit;

	@ApiModelProperty(value = "是否可删除")
	@TableField(value = "is_can_delete")
	private Boolean isCanDelete;

	@ApiModelProperty(value = "是否可选择")
	@TableField(value = "is_can_select")
	private Boolean canSelect;

	@ApiModelProperty(value = "数据权限")
	@TableField(value = "data_permission")
	private Integer dataPermission;

	@ApiModelProperty(value = "创建人")
	@TableField(value = "create_user_id")
	private Integer createUserId;

	@ApiModelProperty(value = "创建时间", accessMode = ApiModelProperty.AccessMode.READ_ONLY)
	@TableField(value = "create_time")
	private LocalDateTime createTime;

	@ApiModelProperty(value = "修改时间", accessMode = ApiModelProperty.AccessMode.READ_ONLY)
	@TableField(value = "update_time")
	private LocalDateTime updateTime;
	/**
	 * 删除标识（0-正常,1-删除）
	 */
	@TableLogic
	@TableField(value = "del_flag")
	private Boolean delFlag;

	@ApiModelProperty(value = "是否有下级")
	@TableField(exist = false)
	private Boolean hasChildren;

	@ApiModelProperty(value = "子节点")
	@TableField(exist = false)
	private List<SysRole> children;

}

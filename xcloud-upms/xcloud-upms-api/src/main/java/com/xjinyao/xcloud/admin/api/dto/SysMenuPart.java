package com.xjinyao.xcloud.admin.api.dto;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * 菜单部分字段更新DTO
 *
 * @author 谢进伟
 * @createDate 2023/4/11 14:15
 */
@Data
public class SysMenuPart {

	/**
	 * 菜单ID
	 */
	@NotNull(message = "菜单ID不能为空")
	@TableId(value = "menu_id", type = IdType.AUTO)
	@ApiModelProperty(value = "菜单id")
	private Long menuId;

	/**
	 * 菜单名称
	 */
	@ApiModelProperty(value = "菜单名称")
	private String name;

	/**
	 * 菜单权限标识
	 */
	@ApiModelProperty(value = "菜单权限标识")
	private String permission;

	/**
	 * 图标
	 */
	@ApiModelProperty(value = "菜单图标")
	private String icon;

	/**
	 * 前端URL
	 */
	@ApiModelProperty(value = "前端路由标识路径")
	private String path;

	/**
	 * 排序值
	 */
	@ApiModelProperty(value = "排序值")
	private Integer sort;

	/**
	 * 菜单类型 （0菜单 1按钮）
	 */
	@ApiModelProperty(value = "菜单类型")
	private String type;

	/**
	 * 路由缓冲
	 */
	@ApiModelProperty(value = "路由缓冲")
	private String keepAlive;

}

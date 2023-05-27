package com.xjinyao.xcloud.admin.api.enums;

import lombok.Getter;

/**
 * @author 谢进伟
 * @description 角色编码, 此枚举不要随意添加成员
 * @createDate 2021/5/21 14:57
 */
public enum RoleCodes {

	/**
	 * 超级管理员
	 */
	ROLE_ADMIN(1L);


	@Getter
	private Long roleId;

	RoleCodes(Long roleId) {
		this.roleId = roleId;
	}
}

package com.xjinyao.xcloud.admin.api.dto;

import com.xjinyao.xcloud.admin.api.enums.RoleDataPermissionDimensionEnum;
import com.xjinyao.xcloud.admin.api.enums.RoleDataPermissionTypeEnum;
import com.xjinyao.xcloud.admin.api.enums.UserDataPermissionDimensionEnum;
import lombok.Data;

import java.io.Serializable;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author 谢进伟
 * @description 数据权限
 * @createDate 2021/5/19 16:16
 */
@Data
public class DataPermission implements Serializable {

	/**
	 * 权限类型
	 */
	private RoleDataPermissionTypeEnum type;

	/**
	 * 角色权限标识值集合
	 */
	private List<RoleDataPermissionDimensionIdentifier> rolePermissions;

	/**
	 * 用户权限标识值集合
	 */
	private List<UserDataPermissionDimensionIdentifier> userPermissions;

	/**
	 * 获取标识符值
	 *
	 * @param roleDataPermissionDimension 角色权限维度数据
	 * @return {@link List}<{@link String}>
	 */
	public List<String> getIdentifierValues(RoleDataPermissionDimensionEnum roleDataPermissionDimension) {
		List<String> identifierValues = new ArrayList<>() {{
			this.add("DEFAULT_" + UUID.randomUUID());
		}};
		Optional.ofNullable(rolePermissions)
				.orElse(Collections.emptyList())
				.stream()
				.filter(o -> Objects.nonNull(o.getDimension()) && Objects.equals(o.getDimension(), roleDataPermissionDimension))
				.collect(Collectors.groupingBy(RoleDataPermissionDimensionIdentifier::getDimension))
				.getOrDefault(roleDataPermissionDimension, Collections.emptyList())
				.forEach(o -> identifierValues.addAll(o.getIdentifierValues()));

		return identifierValues;
	}

	/**
	 * 获取标识符值
	 *
	 * @param userDataPermissionDimension 用户数据权限维度
	 * @return {@link List}<{@link String}>
	 */
	public List<String> getIdentifierValues(UserDataPermissionDimensionEnum userDataPermissionDimension) {
		List<String> identifierValues = new ArrayList<>() {{
			this.add("DEFAULT_" + UUID.randomUUID());
		}};
		Optional.ofNullable(userPermissions)
				.orElse(Collections.emptyList())
				.stream()
				.filter(o -> Objects.nonNull(o.getDimension()) && Objects.equals(o.getDimension(), userDataPermissionDimension))
				.collect(Collectors.groupingBy(UserDataPermissionDimensionIdentifier::getDimension))
				.getOrDefault(userDataPermissionDimension, Collections.emptyList())
				.forEach(o -> identifierValues.addAll(o.getIdentifierValues()));
		return identifierValues;
	}
}

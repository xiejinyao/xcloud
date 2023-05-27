package com.xjinyao.xcloud.admin.api.dto;

import com.xjinyao.xcloud.admin.api.enums.UserDataPermissionDimensionEnum;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.Tolerate;

import java.io.Serializable;
import java.util.List;
import java.util.Optional;

/**
 * 用户数据权限维度数据
 *
 * @author 谢进伟
 * @createDate 2023/3/10 16:04
 */
@Data
@Builder
public class UserDataPermissionDimensionIdentifier implements Serializable {


	/**
	 * 维度
	 */
	private UserDataPermissionDimensionEnum dimension;

	/**
	 * 维度的枚举值
	 */
	private String dimensionValue;

	/**
	 * 数据标识值
	 */
	private List<String> identifierValues;

	@Tolerate
	public UserDataPermissionDimensionIdentifier() {
	}

	public void setDimension(UserDataPermissionDimensionEnum dimension) {
		this.dimension = dimension;
		Optional.ofNullable(dimension).ifPresent(d -> this.dimensionValue = d.getValue());
	}

	public void setDimensionValue(String dimensionValue) {
		this.dimensionValue = dimensionValue;
		Optional.ofNullable(dimensionValue).ifPresent(d -> this.dimension = UserDataPermissionDimensionEnum.ofByValue(d));
	}
}

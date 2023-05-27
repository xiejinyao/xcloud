package com.xjinyao.xcloud.admin.api.dto;

import com.xjinyao.xcloud.admin.api.enums.RoleDataPermissionDimensionEnum;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.Tolerate;

import java.io.Serializable;
import java.util.List;
import java.util.Optional;

/**
 * @author 谢进伟
 * @description 角色数据权限维度数据
 * @createDate 2021/5/19 17:25
 */
@Data
@Builder
public class RoleDataPermissionDimensionIdentifier implements Serializable {

    /**
     * 维度
     */
    private RoleDataPermissionDimensionEnum dimension;

    /**
     * 维度的枚举值
     */
    private String dimensionValue;

    /**
     * 数据标识值
     */
    private List<String> identifierValues;

    @Tolerate
    public RoleDataPermissionDimensionIdentifier() {
    }

    public void setDimension(RoleDataPermissionDimensionEnum dimension) {
        this.dimension = dimension;
        Optional.ofNullable(dimension).ifPresent(d -> this.dimensionValue = d.getValue());
    }

    public void setDimensionValue(String dimensionValue) {
        this.dimensionValue = dimensionValue;
        Optional.ofNullable(dimensionValue).ifPresent(d -> this.dimension = RoleDataPermissionDimensionEnum.ofByValue(d));
    }
}

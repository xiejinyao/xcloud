package com.xjinyao.xcloud.admin.api.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author 谢进伟
 * @description 菜单排序信息
 * @createDate 2020/8/31 14:50
 */
@Data
@ApiModel("菜单排序信息")
public class SortdMenu {

    @ApiModelProperty("菜单id")
    private Long id;
    @ApiModelProperty("上级id")
    private Long parentId;
    @ApiModelProperty("同级排序")
    private Integer sort;
}

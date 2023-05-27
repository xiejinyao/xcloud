package com.xjinyao.xcloud.admin.api.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * @author 谢进伟
 * @description 应用资源
 * @createDate 2021/4/19 14:40
 */
@Data
@ApiModel(value = "前端角色展示对象")
public class SysApplicationResourceVO implements Serializable {

    /**
     * 主键id
     */
    @ApiModelProperty(value = "主键id")
    private Long id;
    /**
     * 资源id
     */
    @ApiModelProperty(value = "资源id")
    private Long resourceId;
    /**
     * 应用id
     */
    @ApiModelProperty(value = "应用id")
    private Integer applicationId;
    /**
     * 资源类型
     */
    @ApiModelProperty(value = "资源类型")
    private Integer type;
    /**
     * 资源编码
     */
    @ApiModelProperty(value = "资源编码")
    private String code;
    /**
     * 资源名称
     */
    @ApiModelProperty(value = "资源名称")
    private String name;
    /**
     * 备注
     */
    @ApiModelProperty(value = "备注")
    private String remark;
}

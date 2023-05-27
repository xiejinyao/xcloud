package com.xjinyao.xcloud.admin.api.dto.organizationId;

import com.xjinyao.xcloud.common.swagger.params.UpdateParamSerializable;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * 针对表【sys_organization(组织管理)】修改DTO
 *
 * @author 谢进伟
 * @createDate 2022-11-09 08:49:28
 */
@Data
public class SysOrganizationUpdateDTO implements UpdateParamSerializable {

    private static final long serialVersionUID = 1L;

    /**
     * 组织id
     */
    @NotNull(message = "[组织id]不能为空")
    @ApiModelProperty("组织id")
    private Long id;
    /**
     * 企业Id
     */
    @ApiModelProperty("企业Id")
    private String enterpriseId;
    /**
     * 组织名称
     */
    @Size(max = 50, message = "组织名称长度不能超过50")
    @ApiModelProperty("组织名称")
    private String name;
    /**
     * 组织全名称
     */
    @ApiModelProperty(value = "组织全名称")
    private String fullName;
    /**
     * 排序
     */
    @ApiModelProperty("排序")
    private Integer sort;
    /**
     * 父级组织id
     */
    @ApiModelProperty("父级组织id")
    private Long parentId;
    /**
     * 备注
     */
    @Size(max = 500, message = "备注长度不能超过500")
    @ApiModelProperty("备注")
    private String remark;
    /**
     * 编码
     */
    @Size(max = 255, message = "编码长度不能超过255")
    @ApiModelProperty("编码")
    private String code;

    /**
     * 组织性质
     */
    @ApiModelProperty(value = "组织性质")
    private String nature;

    /**
     * 组织坐标经纬度
     */
    @ApiModelProperty(value = "组织坐标经纬度")
    private String coordinates;

    /**
     * 所有父级组织id路径,各个id之间使用-隔开
     */
    @Size(max = 500, message = "所有父级组织id路径,各个id之间使用-隔开长度不能超过500")
    @ApiModelProperty("所有父级组织id路径,各个id之间使用-隔开")
    private String parentIdPath;
    /**
     * 是否可删除
     */
    @ApiModelProperty("是否可删除")
    private Boolean isCanDel;
    /**
     * 是否可选择
     */
    @ApiModelProperty("是否可选择")
    private Boolean isCanSelect;
}

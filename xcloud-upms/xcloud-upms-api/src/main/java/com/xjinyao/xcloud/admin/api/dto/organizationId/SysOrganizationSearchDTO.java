package com.xjinyao.xcloud.admin.api.dto.organizationId;

import com.xjinyao.xcloud.common.swagger.params.SearchParamSerializable;
import com.xjinyao.xcloud.common.swagger.params.XRangeParam;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 针对表【sys_organization(组织管理)】搜索DTO
 *
 * @author 谢进伟
 * @createDate 2022-11-09 08:49:28
 */
@Data
public class SysOrganizationSearchDTO implements SearchParamSerializable {

    private static final long serialVersionUID = 1L;

    /**
     * 组织id
     */
    @ApiModelProperty("组织Id")
    private Long id;
    /**
     * 企业Id
     */
    @ApiModelProperty("企业Id")
    private String enterpriseId;
    /**
     * 组织名称
     */
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
     * 创建时间
     */
    @ApiModelProperty("创建时间")
    private XRangeParam<LocalDateTime> createTime;
    /**
     * 修改时间
     */
    @ApiModelProperty("修改时间")
    private XRangeParam<LocalDateTime> updateTime;
    /**
     * 父级组织id
     */
    @ApiModelProperty("父级组织id")
    private Long parentId;
    /**
     * 备注
     */
    @ApiModelProperty("备注")
    private String remark;
    /**
     * 编码
     */
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

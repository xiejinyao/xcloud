package com.xjinyao.xcloud.admin.api.vo;

import com.xjinyao.xcloud.admin.api.entity.SysOrganization;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @author 谢进伟
 * @description 组织显示视图
 * @createDate 2021/1/11 17:17
 */
@Data
@ApiModel(parent = SysOrganization.class)
public class SysOrganizationVO extends SysOrganization {

    @ApiModelProperty("上级组织Id集合")
    private List<String> parentIds;
}

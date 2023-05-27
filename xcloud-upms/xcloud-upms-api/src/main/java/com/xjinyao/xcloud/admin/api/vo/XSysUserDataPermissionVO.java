package com.xjinyao.xcloud.admin.api.vo;


import io.swagger.annotations.ApiModelProperty;
import lombok.Data;


import java.util.Date;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
* 针对表【sys_user_data_permission(角色数据权限)】VO
* @author 谢进伟
* @createDate 2023-03-10 15:53:15
*/
@Data
public class XSysUserDataPermissionVO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
    * 主键
    */
    @ApiModelProperty("主键")
    private Integer id;

    /**
    * 维度
    */
    @ApiModelProperty("维度")
    private String dimension;

    /**
    * 数据标示值
    */
    @ApiModelProperty("数据标示值")
    private String identifierValue;

    /**
    * 用户id
    */
    @ApiModelProperty("用户id")
    private Integer userId;

    /**
    * 备注
    */
    @ApiModelProperty("备注")
    private String remark;

    /**
    * 创建时间
    */
    @ApiModelProperty("创建时间")
    private LocalDateTime createTime;

    /**
    * 最后一次修改时间
    */
    @ApiModelProperty("最后一次修改时间")
    private LocalDateTime updateTime;

}

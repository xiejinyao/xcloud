package com.xjinyao.xcloud.admin.api.vo;


import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 针对表【sys_business_log(业务日志表)】VO
 *
 * @author 谢进伟
 * @createDate 2023-01-31 17:27:09
 */
@Data
public class XSysBusinessLogVO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * ID
     */
    @ApiModelProperty("ID")
    private Long id;

    /**
     * 项目ID
     */
    @ApiModelProperty("项目ID")
    private String projectId;

    /**
     * 业务标志
     */
    @ApiModelProperty("业务标志")
    private Integer type;

    /**
     * 关联各种表的id
     */
    @ApiModelProperty("关联各种表的id")
    private String pkId;

    /**
     * 标题
     */
    @ApiModelProperty("标题")
    private String title;

    /**
     * 操作内容
     */
    @ApiModelProperty("操作内容")
    private String details;

    /**
     * 操作人Id
     */
    @ApiModelProperty("操作人Id")
    private String operationUserId;

    @ApiModelProperty("操作人名称")
    private String operationUserName;
    /**
     * 参数
     */
    @ApiModelProperty("参数")
    private String params;

    /**
     * 结果
     */
    @ApiModelProperty("结果")
    private String result;

    /**
     * 操作时间
     */
    @ApiModelProperty("操作时间")
    private LocalDateTime operationTime;

    /**
     * 创建人
     */
    @ApiModelProperty("创建人")
    private String createUser;

    /**
     * 创建时间
     */
    @ApiModelProperty("创建时间")
    private LocalDateTime createTime;

    /**
     * 更新人
     */
    @ApiModelProperty("更新人")
    private String updateUser;

    /**
     * 更新时间
     */
    @ApiModelProperty("更新时间")
    private LocalDateTime updateTime;

    /**
     * 是否删除
     */
    @ApiModelProperty("是否删除")
    private Boolean delFlag;

    /**
     * 版本
     */
    @ApiModelProperty("版本")
    private Integer version;

}

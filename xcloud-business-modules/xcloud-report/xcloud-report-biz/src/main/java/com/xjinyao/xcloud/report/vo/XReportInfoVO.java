package com.xjinyao.xcloud.report.vo;


import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
* 针对表【sys_report_info(报表信息)】VO
* @author 谢进伟
* @createDate 2023-02-28 14:53:43
*/
@Data
public class XReportInfoVO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
    * 主键
    */
    @ApiModelProperty("主键")
    private Integer id;

    /**
    * 项目Id
    */
    @ApiModelProperty("项目Id")
    private String projectId;

    /**
    * 报表类型
    */
    @ApiModelProperty("报表类型")
    private Integer type;

    /**
    * 报表名称
    */
    @ApiModelProperty("报表名称")
    private String name;

    /**
    * 文件名称
    */
    @ApiModelProperty("文件名称")
    private String fileName;

    /**
    * 模板内容
    */
    @ApiModelProperty("模板内容")
    private String tplContent;

    /**
     * 预览参数声明配置
     */
    @ApiModelProperty("预览参数声明配置")
    private String previewParamsDeclarationConfig;

    /**
     * 搜索表单配置
     */
    @ApiModelProperty("搜索表单配置")
    private String searchFormConfig;

    /**
     * 是否是模板
     */
    @ApiModelProperty("是否是模板")
    private Boolean isTemplate;

    /**
     * 是否可见
     */
    @ApiModelProperty("是否可见")
    private Boolean visible;

    /**
     * 预览时立即加载数据
     */
    @ApiModelProperty("预览时立即加载数据")
    private Boolean previewImmediatelyLoad;

    /**
    * 备注信息
    */
    @ApiModelProperty("备注信息")
    private String description;

    /**
    * 乐观锁
    */
    @ApiModelProperty("乐观锁")
    private String version;

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
     * 创建用户名
     */
    @ApiModelProperty("创建用户名")
    private String createUserName;

    /**
     * 更新用户名
     */
    @ApiModelProperty("更新用户名")
    private String updateUserName;

    /**
     * 更新时间
     */
    @ApiModelProperty("更新时间")
    private LocalDateTime updateTime;

}

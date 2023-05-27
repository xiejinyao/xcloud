package com.xjinyao.xcloud.admin.api.dto.log;

import com.xjinyao.xcloud.common.swagger.params.UpdateParamSerializable;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;

/**
 * 针对表【sys_business_log(业务日志表)】修改DTO
 *
 * @author 谢进伟
 * @createDate 2023-01-31 17:27:09
 */
@Data
public class SysBusinessLogUpdateDTO implements UpdateParamSerializable {

    private static final long serialVersionUID = 1L;

    /**
     * ID
     */
    @NotNull(message = "[ID]不能为空")
    @ApiModelProperty("ID")
    private Long id;
    /**
     * 项目ID
     */
    @NotBlank(message = "[项目ID]不能为空")
    @Size(max = 32, message = "项目ID长度不能超过32")
    @ApiModelProperty("项目ID")
    private String projectId;
    /**
     * 业务标志
     */
    @NotNull(message = "[业务标志]不能为空")
    @ApiModelProperty("业务标志")
    private Integer type;
    /**
     * 关联各种表的id
     */
    @NotBlank(message = "[关联各种表的id]不能为空")
    @Size(max = 255, message = "关联各种表的id长度不能超过255")
    @ApiModelProperty("关联各种表的id")
    private String pkId;
    /**
     * 标题
     */
    @NotBlank(message = "[标题]不能为空")
    @Size(max = 255, message = "标题长度不能超过255")
    @ApiModelProperty("标题")
    private String title;
    /**
     * 操作内容
     */
    @NotBlank(message = "[操作内容]不能为空")
    @Size(max = 255, message = "操作内容长度不能超过255")
    @ApiModelProperty("操作内容")
    private String details;
    /**
     * 操作人Id
     */
    @NotBlank(message = "[操作人Id]不能为空")
    @Size(max = 255, message = "操作人Id长度不能超过255")
    @ApiModelProperty("操作人Id")
    private String operationUserId;
    /**
     * 参数
     */
    @Size(max = -1, message = "参数长度不能超过-1")
    @ApiModelProperty("参数")
    private String params;
    /**
     * 结果
     */
    @Size(max = -1, message = "结果长度不能超过-1")
    @ApiModelProperty("结果")
    private String result;
    /**
     * 操作时间
     */
    @NotNull(message = "[操作时间]不能为空")
    @ApiModelProperty("操作时间")
    private LocalDateTime operationTime;
    /**
     * 创建人
     */
    @NotBlank(message = "[创建人]不能为空")
    @Size(max = 255, message = "创建人长度不能超过255")
    @ApiModelProperty("创建人")
    private String createUser;
    /**
     * 更新人
     */
    @Size(max = 255, message = "更新人长度不能超过255")
    @ApiModelProperty("更新人")
    private String updateUser;
}

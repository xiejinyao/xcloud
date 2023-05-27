package com.xjinyao.xcloud.admin.api.dto.log;

import com.xjinyao.xcloud.admin.api.enums.BusinessLogTypeEnum;
import com.xjinyao.xcloud.common.swagger.params.AddParamSerializable;
import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.Tolerate;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;

/**
 * 针对表【sys_business_log(业务日志表)】新增DTO
 *
 * @author 谢进伟
 * @createDate 2023-01-31 17:27:09
 */
@Data
@Builder
public class SysBusinessLogAddDTO implements AddParamSerializable {

    private static final long serialVersionUID = 1L;

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
    private BusinessLogTypeEnum type;
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
    @ApiModelProperty("操作内容")
    private String details;
    /**
     * 操作人Id
     */
    @NotBlank(message = "[操作人Id]不能为空")
    @Size(max = 255, message = "操作人Id长度不能超过255")
    @ApiModelProperty("操作人Id")
    private String operationUserId;

    @NotBlank(message = "[操作人名称]不能为空")
    @Size(max = 255, message = "操作人名称长度不能超过255")
    @ApiModelProperty("操作人名称")
    private String operationUserName;
    /**
     * 操作时间
     */
    @NotNull(message = "[操作时间]不能为空")
    @ApiModelProperty("操作时间")
    @Builder.Default
    private LocalDateTime operationTime = LocalDateTime.now();
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
     * 创建人
     */
    @ApiModelProperty("创建人")
    private String createUser;

    /**
     * 创建时间
     */
    @ApiModelProperty("创建时间")
    @Builder.Default
    private LocalDateTime createTime=LocalDateTime.now();

    @Tolerate
    public SysBusinessLogAddDTO() {
    }
}

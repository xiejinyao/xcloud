package com.xjinyao.xcloud.report.dto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import javax.validation.constraints.NotNull;

import java.io.Serializable;
import java.time.LocalDateTime;

import java.util.Date;

import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.annotations.ApiModelProperty;
import com.xjinyao.xcloud.common.swagger.params.UpdateParamSerializable;
import lombok.Data;

/**
* 针对表【sys_report_info(报表信息)】修改DTO
* @author 谢进伟
* @createDate 2023-02-28 14:53:43
*/
@Data
public class ReportInfoUpdateDTO implements UpdateParamSerializable {

    private static final long serialVersionUID = 1L;

    /**
    * 主键
    */
    @NotNull(message="[主键]不能为空")
    @ApiModelProperty("主键")
    private Integer id;
    /**
    * 报表类型
    */
    @NotNull(message="[报表类型]不能为空")
    @ApiModelProperty("报表类型")
    private Integer type;
    /**
    * 报表名称
    */
    @NotBlank(message="[报表名称]不能为空")
    @Size(max= 255,message="报表名称长度不能超过255")
    @ApiModelProperty("报表名称")
    private String name;
    /**
    * 备注信息
    */
    @Size(max= 500,message="备注信息长度不能超过500")
    @ApiModelProperty("备注信息")
    private String description;
    /**
     * 预览时立即加载数据
     */
    @ApiModelProperty("预览时立即加载数据")
    private Boolean previewImmediatelyLoad;
    /**
     * 预览参数声明配置
     */
    @TableField(value = "preview_params_declaration_config")
    private String previewParamsDeclarationConfig;
}

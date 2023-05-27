package com.xjinyao.xcloud.report.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * @author 谢进伟
 * @createDate 2023/3/9 14:34
 */
@Data
public class ReportInfoUpdateSearchFormConfigDTO {


	private static final long serialVersionUID = 1L;

	@NotNull(message="[主键]不能为空")
	@ApiModelProperty("主键")
	private Integer id;

	@NotNull(message="[搜索表单配置]不能为空")
	@ApiModelProperty("搜索表单配置")
	private String searchFormConfig;
}

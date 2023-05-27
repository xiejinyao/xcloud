package com.xjinyao.xcloud.common.swagger.params;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * 范围参数，用来接收一些诸如时间、数值之类的范围型参数
 *
 * @author 谢进伟
 * @createDate 2022/9/2 22:56
 */
@Data
@ApiModel("范围参数")
public class XRangeParam<V> implements Serializable {

	/**
	 * 起始值
	 */
	@ApiModelProperty("起始值")
	private V begin;

	/**
	 * 结束值
	 */
	@ApiModelProperty("结束值")
	private V end;
}

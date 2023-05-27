package com.xjinyao.xcloud.admin.api.constants;

/**
 * 序列名称
 *
 * @author 谢进伟
 * @createDate 2023/3/13 09:31
 */
public enum SequenceNames {

	URGE_FEE_ORDER_SEQUENCE_NUM_CODE("催费订单序列"),
	;

	private String remark;

	SequenceNames(String remark) {
		this.remark = remark;
	}
}

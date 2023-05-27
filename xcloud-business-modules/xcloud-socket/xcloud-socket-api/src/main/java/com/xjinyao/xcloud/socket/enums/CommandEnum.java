package com.xjinyao.xcloud.socket.enums;

import lombok.Getter;

/**
 * @author 谢进伟
 * @description 命令枚举
 * @createDate 2020/6/29 19:23
 */
public enum CommandEnum {

	/**
	 * 系统任务创建通知命令
	 */
	SYS_TASK_CREATED_NOTICE_COMMAND("SYS_TASK_CREATED_NOTICE_COMMAND", "系统任务创建通知命令"),

	/**
	 * B2C支付回调
	 */
	SYS_B2C_PAYMENT_COMMAND("SYS_B2C_PAYMENT_COMMAND", "B2C支付回调命令"),

	/**
	 * 数据导入处理进度通知命令
	 */
	DATA_IMPORT_DISPOSE_PROGRESS_COMMAND("DATA_IMPORT_DISPOSE_PROGRESS_COMMAND", "数据导入处理进度通知命令"),
	;

	/**
	 * 命令
	 */
	@Getter
	private String command;

	/**
	 * 备注
	 */
	@Getter
	private String remark;

	CommandEnum(String command, String remark) {
		this.command = command;
		this.remark = remark;
	}
}

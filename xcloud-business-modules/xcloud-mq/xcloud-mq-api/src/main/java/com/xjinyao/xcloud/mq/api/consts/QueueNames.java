package com.xjinyao.xcloud.mq.api.consts;

/**
 * @author 谢进伟
 * @description 队列名常量配置
 * @createDate 2020/6/15 9:30
 */
public class QueueNames {

	/**
	 * 队列前缀
	 */
	public final static String PREFIX = "XCLOUD";

	/***************************************** common queue name declare begin ****************************************/

	/**
	 * 延迟消息转发队列
	 */
	public final static String DELAY_MESSAGES_TRANSFER_QUEUE = PREFIX + ".DELAY.MESSAGES.TRANSFER.QUEUE";

	/**
	 * 业务消息提醒队列
	 */
	public final static String BUSINESS_MESSAGE_REMINDER = PREFIX + ".BUSINESS.MESSAGE.REMINDER.QUEUE";

	/***************************************** common queue name declare end ******************************************/

	/***************************************** business queue name declare begin **************************************/

	// .......

	/***************************************** business queue name declare end ****************************************/
}

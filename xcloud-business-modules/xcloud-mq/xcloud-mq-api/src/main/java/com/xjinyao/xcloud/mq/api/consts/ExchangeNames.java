package com.xjinyao.xcloud.mq.api.consts;

/**
 * @author 谢进伟
 * @description 交换机名称枚举
 * @createDate 2020/11/4 16:33
 */
public class ExchangeNames {


	/**
	 * 交换机前缀
	 */
	public final static String PREFIX = "XCLOUD";

	/**
	 * 默认信道
	 */
	public final static String DEFAULT_EXCHANGE = PREFIX;

	/**
	 * 延迟队列信道
	 */
	public final static String DELAYED_MESSAGE_EXCHANGE = PREFIX + "-DELAYED-MESSAGE";

	/**
	 * 业务消息提醒队列交换机
	 */
	public final static String BUSINESS_MESSAGE_REMINDER_EXCHANGE = PREFIX + "-BUSINESS-MESSAGE-REMINDER";
	/**
	 * 数据导入任务交换机
	 */
	public final static String DATA_IMPORT_EXCHANGE = PREFIX + "-DATA-IMPORT";


}

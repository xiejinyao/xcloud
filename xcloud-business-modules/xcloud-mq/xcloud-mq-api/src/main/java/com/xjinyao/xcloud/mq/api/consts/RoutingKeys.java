package com.xjinyao.xcloud.mq.api.consts;

/**
 * @author 谢进伟
 * @description 交换机与队列之间的路由定义
 * @createDate 2020/11/12 17:11
 */
public class RoutingKeys {

	/**
	 * 路由前缀
	 */
	private static final String PREFIX = "xcloud";

	/**
	 * 路由后缀
	 */
	private static final String SUFFIX = "routing";

	/**
	 * 延迟消息转发队列 交换机绑定路由key
	 */
	public static final String DELAY_MESSAGES_TRANSFER_QUEUE_ROUTING_KEY = PREFIX + ".delay.messages.transfer." + SUFFIX;

}

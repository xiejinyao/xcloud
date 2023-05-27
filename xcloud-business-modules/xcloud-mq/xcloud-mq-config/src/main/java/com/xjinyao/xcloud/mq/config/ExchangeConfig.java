package com.xjinyao.xcloud.mq.config;

import com.xjinyao.xcloud.mq.api.consts.ExchangeNames;
import org.springframework.amqp.core.CustomExchange;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.core.FanoutExchange;
import org.springframework.context.annotation.Bean;

import java.util.Collections;

/**
 * @author 谢进伟
 * @description 交换机配置类
 * @createDate 2020/11/6 13:56
 */
public class ExchangeConfig {


	private static final String X_DELAYED_TYPE = "x-delayed-type";

	private static final String TYPE = "x-delayed-message";

	/**
	 * 默认信道配置
	 *
	 * @return
	 */
	@Bean(ExchangeNames.DEFAULT_EXCHANGE)
	public DirectExchange defaultExchange() {
		return new DirectExchange(ExchangeNames.DEFAULT_EXCHANGE, true, false);
	}

	/**
	 * 延迟队列信道配置
	 *
	 * @return
	 */
	@Bean(ExchangeNames.DELAYED_MESSAGE_EXCHANGE)
	public CustomExchange delayedExchange() {
		return new CustomExchange(ExchangeNames.DELAYED_MESSAGE_EXCHANGE, TYPE, true, false,
				Collections.singletonMap(X_DELAYED_TYPE, ExchangeTypes.DIRECT));
	}

	/**
	 * 业务消息提醒队列交换机
	 *
	 * @return
	 */
	@Bean(ExchangeNames.BUSINESS_MESSAGE_REMINDER_EXCHANGE)
	public FanoutExchange businessMessageReminderExchange() {
		return new FanoutExchange(ExchangeNames.BUSINESS_MESSAGE_REMINDER_EXCHANGE, true, false);
	}

	/**
	 * 数据导入交换机
	 *
	 * @return
	 */
	@Bean(ExchangeNames.DATA_IMPORT_EXCHANGE)
	public DirectExchange dataImportExchange() {
		return new DirectExchange(ExchangeNames.DATA_IMPORT_EXCHANGE, true, false);
	}
}

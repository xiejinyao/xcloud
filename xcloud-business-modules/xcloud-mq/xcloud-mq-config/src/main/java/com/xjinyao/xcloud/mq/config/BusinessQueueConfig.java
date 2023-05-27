package com.xjinyao.xcloud.mq.config;

import com.xjinyao.xcloud.mq.api.consts.ExchangeNames;
import com.xjinyao.xcloud.mq.api.consts.QueueNames;
import com.xjinyao.xcloud.mq.api.consts.RoutingKeys;
import org.springframework.amqp.core.*;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;

/**
 * @author 谢进伟
 * @description 业务队列配置, 系统所有的队列需在该配置内进行注册配置
 * @createDate 2020/6/15 10:41
 */
public class BusinessQueueConfig {

	/**
	 * 业务消息提醒队列 队列
	 *
	 * @return
	 */
	@Bean(QueueNames.BUSINESS_MESSAGE_REMINDER)
	public Queue businessMessageReminderQueue() {
		return new Queue(QueueNames.BUSINESS_MESSAGE_REMINDER, true, false, false);
	}

	/**
	 * 将 业务消息提醒队列 绑定到交换机
	 *
	 * @return
	 */
	@Bean
	public Binding businessMessageReminderQueueBinding(
			@Qualifier(ExchangeNames.BUSINESS_MESSAGE_REMINDER_EXCHANGE) FanoutExchange businessMessageReminderExchange,
			@Qualifier(QueueNames.BUSINESS_MESSAGE_REMINDER) Queue businessMessageReminderQueue) {
		return BindingBuilder.bind(businessMessageReminderQueue)
				.to(businessMessageReminderExchange);
	}

}

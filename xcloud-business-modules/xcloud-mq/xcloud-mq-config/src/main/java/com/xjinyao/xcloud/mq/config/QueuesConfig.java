package com.xjinyao.xcloud.mq.config;

import com.xjinyao.xcloud.mq.api.consts.ExchangeNames;
import com.xjinyao.xcloud.mq.api.consts.QueueNames;
import com.xjinyao.xcloud.mq.api.consts.RoutingKeys;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.CustomExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;

/**
 * @author 谢进伟
 * @description 通用队列配置
 * @createDate 2020/6/15 9:37
 */
public class QueuesConfig {

	/**
	 * 死信转发队列
	 *
	 * @return
	 */
	@Bean(QueueNames.DELAY_MESSAGES_TRANSFER_QUEUE)
	public Queue delayMessagesTransferQueue() {
		return new Queue(QueueNames.DELAY_MESSAGES_TRANSFER_QUEUE, true, false,
				false);
	}

	/**
	 * 将死信转发队列绑定到默认信道配置
	 *
	 * @return
	 */
	@Bean
	public Binding delayMessagesTransferQueueBinding(@Qualifier(ExchangeNames.DELAYED_MESSAGE_EXCHANGE)
														 CustomExchange delayedExchange,
													 @Qualifier(QueueNames.DELAY_MESSAGES_TRANSFER_QUEUE)
													 Queue repeatTradeQueue) {
		return BindingBuilder.bind(repeatTradeQueue)
				.to(delayedExchange)
				.with(RoutingKeys.DELAY_MESSAGES_TRANSFER_QUEUE_ROUTING_KEY)
				.noargs();
	}
}

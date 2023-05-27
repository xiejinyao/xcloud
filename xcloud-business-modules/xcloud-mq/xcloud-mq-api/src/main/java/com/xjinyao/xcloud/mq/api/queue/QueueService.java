package com.xjinyao.xcloud.mq.api.queue;

import com.xjinyao.xcloud.mq.api.consts.ExchangeNames;
import com.xjinyao.xcloud.mq.api.consts.RoutingKeys;
import com.xjinyao.xcloud.mq.api.message.DelayedMessage;
import lombok.AllArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

/**
 * @author 谢进伟
 * @description 队列服务
 * @createDate 2020/6/15 9:35
 */
@AllArgsConstructor
public class QueueService {

	private RabbitTemplate rabbitTemplate;

	/**
	 * 将消息发送到默认交换机中，并转发到所有通过 routingKey 与该交换机绑定的队列
	 *
	 * @param routingKey 队列名称
	 * @param msg        消息内容
	 * @return
	 */
	public Boolean sendToDefaultExchange(String routingKey, Object msg) {
		return sendToExchange(ExchangeNames.DEFAULT_EXCHANGE, routingKey, msg);
	}

	/**
	 * 将消息发送到指定交换机中，并转发到所有通过 routingKey 与该交换机绑定的队列
	 *
	 * @param routingKey 队列名称
	 * @param msg        消息内容
	 * @return
	 */
	public Boolean sendToExchange(String exchange, String routingKey, Object msg) {
		try {
			if (routingKey == null) {
				routingKey = "";
			}
			rabbitTemplate.convertAndSend(exchange, routingKey, msg);
			return true;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * 延迟将消息发送到指定交换机中，并转发到所有通过 routingKey 与该交换机绑定的队列
	 *
	 * @param routingKey 队列名称
	 * @param msg        消息内容
	 * @param times      延迟时间（毫秒）
	 * @return
	 */
	public <T> Boolean lazySendToExchange(String exchange, String routingKey, T msg, int times) {
		try {
			DelayedMessage delayedMessage = new DelayedMessage(exchange, routingKey, msg, times);
			rabbitTemplate.convertAndSend(ExchangeNames.DELAYED_MESSAGE_EXCHANGE,
					RoutingKeys.DELAY_MESSAGES_TRANSFER_QUEUE_ROUTING_KEY,
					delayedMessage, message -> {
						message.getMessageProperties().setDelay(delayedMessage.getTimes());
						return message;
					});
			return true;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

}

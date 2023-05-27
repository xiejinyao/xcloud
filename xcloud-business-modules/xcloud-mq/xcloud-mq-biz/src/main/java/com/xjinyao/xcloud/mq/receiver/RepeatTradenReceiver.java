package com.xjinyao.xcloud.mq.receiver;


import com.xjinyao.xcloud.mq.api.consts.QueueNames;
import com.xjinyao.xcloud.mq.api.message.DelayedMessage;
import com.xjinyao.xcloud.mq.api.queue.QueueService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

/**
 * 死信接收处理消费者,负责转发死信队列中的消息
 */
@Slf4j
@Component
@AllArgsConstructor
@RabbitListener(queues = {QueueNames.DELAY_MESSAGES_TRANSFER_QUEUE}, ackMode = "NONE", concurrency = "10")
public class RepeatTradenReceiver {

	private QueueService queueService;

	@RabbitHandler
	public void process(DelayedMessage msg) {
		try {
			log.info("received delay messages:{}", msg);
			queueService.sendToExchange(msg.getExchange(), msg.getRoutingKey(), msg.getContent());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}

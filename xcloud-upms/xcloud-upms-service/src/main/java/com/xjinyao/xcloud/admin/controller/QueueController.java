package com.xjinyao.xcloud.admin.controller;

import com.xjinyao.xcloud.common.core.util.R;
import com.xjinyao.xcloud.mq.api.queue.QueueService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author 谢进伟
 * @createDate 2023/3/23 21:03
 */
@AllArgsConstructor
@RestController
@RequestMapping("/")
public class QueueController {

	private final QueueService queueService;

	@PostMapping("/lazySendToExchange")
	public R lazySendToExchange(String exchange, String routingKey, String msg, int times) {
		queueService.lazySendToExchange(exchange, routingKey, msg, times);
		return R.ok();
	}
}

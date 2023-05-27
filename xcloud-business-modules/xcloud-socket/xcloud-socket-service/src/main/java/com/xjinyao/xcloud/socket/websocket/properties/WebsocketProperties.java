package com.xjinyao.xcloud.socket.websocket.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * @author 谢进伟
 * @description 配置
 * @createDate 2020/6/27 16:45
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "websocket")
public class WebsocketProperties {

	/**
	 * 上下文，客户端连接时将会作为清理地址中的请求前缀
	 */
	private String context = "/websocket-service";

	/**
	 * 是否启用
	 */
	private Boolean enable = true;
	/**
	 * 全局使用的订阅前缀（客户端订阅路径上会体现出来）
	 */
	private String applicationDestinationPrefixes = "/app";
	/**
	 * 点对点使用的订阅前缀（客户端订阅路径上会体现出来），不设置的话，默认也是/user/
	 */
	private String userDestinationPrefix = "/user/";

	/**
	 * STOMP 消息的最大大小 默认：128 * 1024
	 */
	private Integer messageSizeLimit = 128 * 1024;
	/**
	 * 配置时间限制（以毫秒为单位），用于在使用 SockJS 回退选项时将消息发送到 WebSocket 会话或写入 HTTP 响应时允许的最大时间量。
	 */
	private Integer sendTimeLimit = 15 * 1000;
	/**
	 * 配置将消息发送到 WebSocket 会话时要缓冲的最大数据量，或使用 SockJS 回退选项时的 HTTP 响应
	 */
	private Integer sendBufferSizeLimit = 512 * 1024;
}

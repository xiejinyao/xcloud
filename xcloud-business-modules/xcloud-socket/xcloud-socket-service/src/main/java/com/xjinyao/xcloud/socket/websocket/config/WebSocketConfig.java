package com.xjinyao.xcloud.socket.websocket.config;

import com.xjinyao.xcloud.socket.websocket.constants.WebSocketConstants;
import com.xjinyao.xcloud.socket.websocket.interceptor.AuthChannelInterceptor;
import com.xjinyao.xcloud.socket.websocket.properties.WebsocketProperties;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketTransportRegistration;

/**
 * @author 谢进伟
 * @description websocket配置, 参考文档：https://docs.spring.io/spring-framework/docs/4.2.7.RELEASE/spring-framework-reference/htmlsingle/#websocket
 * @createDate 2021/12/29 08:49
 */
@Configuration
@AllArgsConstructor
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

	private final AuthChannelInterceptor authChannelInterceptor;
	private final WebsocketProperties websocketProperties;

	@Override
	public void configureMessageBroker(MessageBrokerRegistry config) {
		//订阅广播 Broker（消息代理）名称
		config.enableSimpleBroker(WebSocketConstants.BROKER_DESTINATION_PREFIX);
		//全局使用的订阅前缀（客户端订阅路径上会体现出来）
		config.setApplicationDestinationPrefixes(websocketProperties.getApplicationDestinationPrefixes());
		//点对点使用的订阅前缀（客户端订阅路径上会体现出来），不设置的话，默认也是/user/
		config.setUserDestinationPrefix(websocketProperties.getUserDestinationPrefix());
	}

	@Override
	public void registerStompEndpoints(StompEndpointRegistry registry) {
		//允许客户端使用websocket，允许跨域
		registry.addEndpoint(websocketProperties.getContext())
				.setAllowedOriginPatterns("*");
		//允许客户端使用socketJs方式访问允许跨域
		registry.addEndpoint(websocketProperties.getContext())
				.setAllowedOriginPatterns("*")
				.withSockJS();
	}

	@Override
	public void configureClientInboundChannel(ChannelRegistration registration) {
		registration.interceptors(this.authChannelInterceptor);
	}

	@Override
	public void configureWebSocketTransport(WebSocketTransportRegistration registration) {
		registration.setMessageSizeLimit(websocketProperties.getMessageSizeLimit())//STOMP 消息的最大大小
				.setSendTimeLimit(websocketProperties.getSendTimeLimit()) //配置时间限制（以毫秒为单位），用于在使用 SockJS 回退选项时将消息发送到 WebSocket 会话或写入 HTTP 响应时允许的最大时间量。
				.setSendBufferSizeLimit(websocketProperties.getSendBufferSizeLimit());//在向客户端发送消息时可以缓冲多少数据
	}
}

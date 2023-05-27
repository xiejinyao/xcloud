package com.xjinyao.xcloud.socket.socketio.config;

import com.corundumstudio.socketio.SocketIOServer;
import com.corundumstudio.socketio.annotation.SpringAnnotationScanner;
import com.xjinyao.xcloud.socket.socketio.properties.SocketIoProperties;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.BooleanUtils;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author 谢进伟
 * @description SocketIO配置
 * @createDate 2020/6/27 14:27
 */
@Slf4j
@Configuration
@ConditionalOnProperty(prefix = "socket.io", value = "enable", havingValue = "true")
public class SocketIoConfig {

	@Bean
	public SpringAnnotationScanner springAnnotationScanner(SocketIOServer socketServer) {
		return new SpringAnnotationScanner(socketServer);
	}

	@Bean
	public SocketIOServer socketService(SocketIoProperties socketIoProperties) {
		Boolean enable = socketIoProperties.getEnable();
		String hostName = socketIoProperties.getHostName();
		int port = socketIoProperties.getPort();
		int upgradeTimeout = socketIoProperties.getUpgradeTimeout();
		int pingInterval = socketIoProperties.getPingInterval();
		Integer pingTimeout = socketIoProperties.getPingTimeout();
		if (!BooleanUtils.toBoolean(enable)) {
			log.info("client websocket service is disabled !");
			return null;
		}

		com.corundumstudio.socketio.Configuration config = new com.corundumstudio.socketio.Configuration();
		config.setContext(socketIoProperties.getContext());
		// host
		config.setHostname(hostName);
		//端口
		config.setPort(port);
		// 设置监听端口
		config.setPort(port);
		// 协议升级超时时间（毫秒），默认10000。HTTP握手升级为ws协议超时时间
		config.setUpgradeTimeout(upgradeTimeout);
		// Ping消息间隔（毫秒），默认25000。客户端向服务器发送一条心跳消息间隔
		config.setPingInterval(pingInterval);
		// Ping消息超时时间（毫秒），默认60000，这个时间间隔内没有接收到心跳消息就会发送超时事件
		config.setPingTimeout(pingTimeout);

		return new SocketIOServer(config);
	}
}

package com.xjinyao.xcloud.socket.socketio.runner;

import com.corundumstudio.socketio.SocketIOServer;
import com.xjinyao.xcloud.socket.socketio.properties.SocketIoProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 * @author 谢进伟
 * @description SocketIO服务启动/停止
 * @createDate 2020/6/27 17:20
 */
@Slf4j
@Order(1)
@Component
public class SocketIOServerRunner implements CommandLineRunner, DisposableBean {

	private final SocketIOServer server;
	private final SocketIoProperties socketIoProperties;

	public SocketIOServerRunner(@Autowired(required = false) SocketIOServer server,
                                SocketIoProperties socketIoProperties) {
		this.server = server;
		this.socketIoProperties = socketIoProperties;
	}

	@Override
	public void run(String... args) {
		if (socketIoProperties.getEnable()) {
			server.start();
			log.info("socketIO service run in: http://" + socketIoProperties.getHostName() +
					":" + socketIoProperties.getPort());

			Runtime.getRuntime().addShutdownHook(new Thread(() -> {
				try {
					server.stop();
				} catch (Exception e) {
				}
			}));
		} else {
			log.info("socketIO service is disabled !");
		}
	}

	@Override
	public void destroy() {
		if (socketIoProperties.getEnable()) {
			server.stop();
			log.info("socketIO closed!");
		}
	}
}

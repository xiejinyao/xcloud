package com.xjinyao.xcloud.socket.socketio.handler;

import cn.hutool.core.collection.CollectionUtil;
import com.corundumstudio.socketio.AckRequest;
import com.corundumstudio.socketio.HandshakeData;
import com.corundumstudio.socketio.SocketIOClient;
import com.corundumstudio.socketio.SocketIOServer;
import com.corundumstudio.socketio.annotation.OnConnect;
import com.corundumstudio.socketio.annotation.OnDisconnect;
import com.corundumstudio.socketio.annotation.OnEvent;
import com.xjinyao.xcloud.common.core.redis.constant.CacheConstants;
import com.xjinyao.xcloud.common.core.redis.service.RedisService;
import com.xjinyao.xcloud.common.core.util.StringUtils;
import com.xjinyao.xcloud.socket.constant.MessageTypeConstant;
import com.xjinyao.xcloud.socket.message.BroadcastMessageInfo;
import com.xjinyao.xcloud.socket.message.UnicastMessageInfo;
import com.xjinyao.xcloud.socket.message.command.BroadcastCommand;
import com.xjinyao.xcloud.socket.message.command.UnicastCommand;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.UUID;

/**
 * @author 谢进伟
 * @description 消息处理器
 * @createDate 2020/6/27 17:01
 */
@Slf4j
@Component
public class MessageEventHandler {

	private final String CLIENT_CACHE_KEY_SEPARATOR = "_sessionId:";

	private final SocketIOServer socketIOServer;

	private final RedisService redisService;

	@Getter
	private Hashtable<String, SocketIOClient> clients = new Hashtable<>();


	public MessageEventHandler(@Autowired(required = false) SocketIOServer socketIOServer, RedisService redisService) {
		this.socketIOServer = socketIOServer;
		this.redisService = redisService;
	}

	/**
	 * 当客户端发起连接时调用
	 *
	 * @param client 客户端对象
	 */
	@OnConnect
	public void onConnect(SocketIOClient client) {
		if (client != null) {
			String clientId = getClientId(client);
			if (StringUtils.isAbsoluteNotNull(clientId)) {
				UUID sessionId = client.getSessionId();
				log.info("客户端:" + clientId + " 接入服务器(sessionId:" + sessionId + ")!");
				clients.put(getClientsCacheKey(clientId, sessionId), client);
				if (StringUtils.isNotBlank(clientId)) {
					redisService.hset(CacheConstants.SOCKET_IO_CLIENT_ID_LIST, clientId, sessionId);
				}
			} else {
				//拒绝没有客户端Id的请求接入
				client.disconnect();
			}
		}
	}

	/**
	 * 客户端断开连接时调用，刷新客户端信息
	 *
	 * @param client 客户端对象
	 */
	@OnDisconnect
	public void onDisconnect(SocketIOClient client) {
		if (client != null) {
			String clientId = getClientId(client);
			UUID sessionId = client.getSessionId();
			log.info("客户端:" + clientId + " 与服务器断开连接(sessionId:" + sessionId + ")!");
			clients.remove(getClientsCacheKey(clientId, sessionId));
			if (StringUtils.isNotBlank(clientId)) {
				redisService.hdel(CacheConstants.SOCKET_IO_CLIENT_ID_LIST, clientId);
			}
		}
	}

	/**
	 * 注册广播事件
	 *
	 * @param client     客户端对象
	 * @param ackRequest 确认对象
	 * @param msg        消息对象
	 */
	@OnEvent(value = MessageTypeConstant.BROADCAST)
	public void registerBroadcastEventListener(SocketIOClient client, AckRequest ackRequest, BroadcastMessageInfo msg) {
		if (client != null) {
			log.info("客户端：" + client.getSessionId() + " 群发消息：" + msg);
			socketIOServer.getBroadcastOperations().sendEvent(MessageTypeConstant.BROADCAST, msg);
			ackRequest.sendAckData("success", msg);
		}
	}

	/**
	 * 注册单播事件
	 *
	 * @param client     客户端对象
	 * @param ackRequest 确认对象
	 * @param msg        消息对象
	 */
	@OnEvent(value = MessageTypeConstant.UNICAST)
	public void registerUnicastEventListener(SocketIOClient client, AckRequest ackRequest, UnicastMessageInfo msg) {
		if (client != null) {
			String to = msg.getTo();
			String from = msg.getFrom();
			if (StringUtils.isNotBlank(to) && StringUtils.isNotBlank(from)) {
				String fromName = msg.getFromName();
				log.info(from + (StringUtils.isNotBlank(fromName) ? ("(" + fromName + ")") : "") + " 定向发送消息到： " +
						to + " 消息内容：" + msg.getContent());
				List<SocketIOClient> socketIOClients = getClientByClientId(to);
				if (CollectionUtil.isNotEmpty(socketIOClients)) {
					socketIOClients.forEach(socketIOClient -> {
						if (socketIOClient != null) {
							socketIOClient.sendEvent(MessageTypeConstant.UNICAST, msg);
						}
						ackRequest.sendAckData("success", msg);
					});
				}
			}
		}
	}

	/**
	 * 注册广播命令事件
	 *
	 * @param client     客户端对象
	 * @param ackRequest 确认对象
	 * @param msg        消息对象
	 */
	@OnEvent(value = MessageTypeConstant.BROADCAST_COMMAND)
	public void registerBroadcastCommandEventListener(SocketIOClient client, AckRequest ackRequest, BroadcastCommand msg) {
		if (client != null) {
			log.info("客户端：" + client.getSessionId() + " 群发消息：" + msg);
			socketIOServer.getBroadcastOperations().sendEvent(MessageTypeConstant.BROADCAST_COMMAND, msg);
			ackRequest.sendAckData("success", msg);
		}
	}

	/**
	 * 注册单播命令事件
	 *
	 * @param client     客户端对象
	 * @param ackRequest 确认对象
	 * @param msg        消息对象
	 */
	@OnEvent(value = MessageTypeConstant.UNICAST_COMMAND)
	public void registerUnicastCommandEventListener(SocketIOClient client, AckRequest ackRequest, UnicastCommand msg) {
		if (client != null) {
			String to = msg.getTo();
			if (StringUtils.isNotBlank(to)) {
				log.info(" 定向发送命令消息到： " + to + " 消息内容：" + msg);
				List<SocketIOClient> socketIOClients = getClientByClientId(to);
				if (CollectionUtil.isNotEmpty(socketIOClients)) {
					socketIOClients.forEach(socketIOClient -> {
						if (socketIOClient != null) {
							socketIOClient.sendEvent(MessageTypeConstant.UNICAST_COMMAND, msg);
						}
						ackRequest.sendAckData("success", msg);
					});
				}
			}
		}
	}

	/**
	 * 获取客户端id
	 *
	 * @param client 客户端对象
	 * @return
	 */
	private String getClientId(SocketIOClient client) {
		HandshakeData handshakeData = client.getHandshakeData();
		return handshakeData.getSingleUrlParam("clientId");
	}

	/**
	 * 获取client 缓存key
	 *
	 * @param clientId  客户端id
	 * @param sessionId 会话id
	 * @return
	 */
	private String getClientsCacheKey(String clientId, UUID sessionId) {
		return clientId + CLIENT_CACHE_KEY_SEPARATOR + sessionId;
	}

	/**
	 * 根据客户端id获取连接对象
	 *
	 * @param clientId
	 * @return
	 */
	public List<SocketIOClient> getClientByClientId(String clientId) {
		List<SocketIOClient> socketIOClients = new ArrayList<>();
		clients.keySet().forEach(key -> {
			if (key.startsWith(clientId + CLIENT_CACHE_KEY_SEPARATOR)) {
				SocketIOClient socketIOClient = clients.get(key);
				if (socketIOClient != null) {
					socketIOClients.add(socketIOClient);
				}
			}
		});
		return socketIOClients;
	}
}

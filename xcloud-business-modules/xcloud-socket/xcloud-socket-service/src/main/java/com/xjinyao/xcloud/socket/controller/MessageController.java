package com.xjinyao.xcloud.socket.controller;

import cn.hutool.core.collection.CollectionUtil;
import com.corundumstudio.socketio.SocketIOClient;
import com.corundumstudio.socketio.SocketIOServer;
import com.xjinyao.xcloud.common.core.util.R;
import com.xjinyao.xcloud.common.security.annotation.Inner;
import com.xjinyao.xcloud.socket.constant.MessageTypeConstant;
import com.xjinyao.xcloud.socket.message.BroadcastMessageInfo;
import com.xjinyao.xcloud.socket.message.UnicastMessageInfo;
import com.xjinyao.xcloud.socket.message.command.BroadcastCommand;
import com.xjinyao.xcloud.socket.message.command.UnicastCommand;
import com.xjinyao.xcloud.socket.socketio.handler.MessageEventHandler;
import com.xjinyao.xcloud.socket.socketio.properties.SocketIoProperties;
import com.xjinyao.xcloud.socket.websocket.properties.WebsocketProperties;
import com.xjinyao.xcloud.socket.websocket.stream.service.WebsocketMessageDistributeService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.annotations.ApiIgnore;

import java.util.List;

/**
 * @author 谢进伟
 * @description 消息控制器
 * @createDate 2020/6/27 17:50
 */
@RestController
@RequestMapping("/")
@Api(value = "/", tags = "消息控制器")
public class MessageController {

	private final SocketIOServer socketIOServer;
	private final WebsocketMessageDistributeService websocketMessageDistributeService;
	private final MessageEventHandler messageEventHandler;
	private final SocketIoProperties socketIoProperties;
	private final WebsocketProperties websocketProperties;

	public MessageController(@Autowired(required = false) SocketIOServer socketIOServer,
							 @Autowired(required = false) WebsocketMessageDistributeService websocketMessageDistributeService,
							 @Autowired(required = false) MessageEventHandler messageEventHandler,
							 SocketIoProperties socketIoProperties,
							 WebsocketProperties websocketProperties) {
		this.socketIOServer = socketIOServer;
		this.websocketMessageDistributeService = websocketMessageDistributeService;
		this.messageEventHandler = messageEventHandler;
		this.socketIoProperties = socketIoProperties;
		this.websocketProperties = websocketProperties;
	}

	/**
	 * 广播消息
	 *
	 * @param msg 消息对象
	 * @return
	 */
	@PostMapping("broadcast")
	@ApiOperation(value = "广播消息", notes = "广播消息")
	public R<Boolean> broadcast(@RequestBody BroadcastMessageInfo msg) {
		if (socketIoProperties.getEnable() && socketIOServer != null) {
			socketIOServer.getBroadcastOperations().sendEvent(MessageTypeConstant.BROADCAST, msg);
			return R.ok(Boolean.TRUE, "广播消息成功!");
		} else if (websocketProperties.getEnable() && websocketMessageDistributeService != null) {
			if (websocketMessageDistributeService.sendBroadcastMessage(msg)) {
				return R.ok(Boolean.TRUE, "广播消息成功!");
			}
		}
		return R.failed(Boolean.FALSE, "广播消息失败!");
	}

	/**
	 * 单播消息
	 *
	 * @param msg 消息对象
	 */
	@PostMapping("unicast")
	@ApiOperation(value = "单播消息", notes = "单播消息")
	public R<Boolean> unicast(@RequestBody UnicastMessageInfo msg) {
		if (socketIoProperties.getEnable() && socketIOServer != null) {
			List<SocketIOClient> socketIOClients = messageEventHandler.getClientByClientId(msg.getTo());
			if (CollectionUtil.isNotEmpty(socketIOClients)) {
				socketIOClients.forEach(socketIOClient -> {
					if (socketIOClient != null) {
						synchronized (socketIOClient) {
							socketIOClient.sendEvent(MessageTypeConstant.UNICAST, msg);
						}
					}
				});
				return R.ok(Boolean.TRUE, "单播消息成功!");
			}
		} else if (websocketProperties.getEnable() && websocketMessageDistributeService != null) {
			if (websocketMessageDistributeService.sendUnicastMessage(msg)) {
				return R.ok(Boolean.TRUE, "单播消息成功!");
			}
		}

		return R.failed(Boolean.FALSE, "单播消息失败!");
	}

	/**
	 * 广播命令
	 *
	 * @param msg 消息对象
	 */
	@PostMapping("broadcastCommand")
	@ApiOperation(value = "广播命令", notes = "广播命令")
	public R<Boolean> broadcastCommand(@RequestBody BroadcastCommand msg) {
		if (socketIoProperties.getEnable() && socketIOServer != null) {
			socketIOServer.getBroadcastOperations().sendEvent(MessageTypeConstant.BROADCAST_COMMAND, msg);
			return R.failed(Boolean.TRUE, "广播命令成功!");
		} else if (websocketProperties.getEnable() && websocketMessageDistributeService != null) {
			if (websocketMessageDistributeService.sendBroadcastCommandMessage(msg)) {
				return R.ok(Boolean.TRUE, "广播命令成功!");
			}
		}
		return R.failed(Boolean.FALSE, "广播命令失败!");
	}


	/**
	 * 单播命令
	 *
	 * @param msg 消息对象
	 */
	@PostMapping("unicastCommand")
	@ApiOperation(value = "单播命令", notes = "单播命令")
	public R<Boolean> unicastCommand(@RequestBody UnicastCommand msg) {
		if (socketIoProperties.getEnable() && socketIOServer != null) {
			List<SocketIOClient> socketIOClients = messageEventHandler.getClientByClientId(msg.getTo());
			if (CollectionUtil.isNotEmpty(socketIOClients)) {
				socketIOClients.forEach(socketIOClient -> {
					if (socketIOClient != null) {
						synchronized (socketIOClient) {
							socketIOClient.sendEvent(MessageTypeConstant.UNICAST_COMMAND, msg);
						}
					}
				});
				return R.ok(Boolean.TRUE, "发送成功!");
			}
		} else if (websocketProperties.getEnable() && websocketMessageDistributeService != null) {
			if (websocketMessageDistributeService.sendUnicastCommandMessage(msg)) {
				return R.ok(Boolean.TRUE, "发送成功!");
			}
		}
		return R.failed(Boolean.FALSE, "发送失败!");
	}


	/**
	 * 广播消息
	 *
	 * @param msg 消息对象
	 * @return
	 */
	@Inner
	@ApiIgnore
	@PostMapping("/inner/broadcast")
	@ApiOperation(value = "广播消息", notes = "广播消息", hidden = true)
	public R<Boolean> broadcastForInner(@RequestBody BroadcastMessageInfo msg) {
		return broadcast(msg);
	}

	/**
	 * 单播消息
	 *
	 * @param msg 消息对象
	 */
	@Inner
	@ApiIgnore
	@PostMapping("/inner/unicast")
	@ApiOperation(value = "单播消息", notes = "单播消息", hidden = true)
	public R<Boolean> unicastForInner(@RequestBody UnicastMessageInfo msg) {
		return unicast(msg);
	}

	/**
	 * 广播命令
	 *
	 * @param msg 消息对象
	 */
	@Inner
	@ApiIgnore
	@PostMapping("/inner/broadcastCommand")
	@ApiOperation(value = "广播命令", notes = "广播命令", hidden = true)
	public R<Boolean> broadcastCommandForInner(@RequestBody BroadcastCommand msg) {
		return broadcastCommand(msg);
	}

	/**
	 * 单播命令
	 *
	 * @param msg 消息对象
	 */
	@Inner
	@ApiIgnore
	@PostMapping("/inner/unicastCommand")
	@ApiOperation(value = "单播命令", notes = "单播命令")
	public R<Boolean> unicastCommandForInner(@RequestBody UnicastCommand msg) {
		return unicastCommand(msg);
	}
}

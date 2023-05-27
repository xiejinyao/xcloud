package com.xjinyao.xcloud.socket.websocket.interceptor;

import com.xjinyao.xcloud.common.core.util.StringUtils;
import com.xjinyao.xcloud.common.security.service.CustomRemoteTokenServices;
import com.xjinyao.xcloud.common.security.service.CustomUser;
import com.xjinyao.xcloud.common.security.util.SecurityUtils;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.stereotype.Component;

import java.security.Principal;

/**
 * @author 谢进伟
 * @description socket鉴权，此拦截器主要用来处理客户端Id，将客户端id绑定为当前socket连接的用户名，以便后续的消息定向推送（单播消息）
 * @createDate 2021/12/29 17:06
 */
@Slf4j
@Component
@AllArgsConstructor
@Order(Ordered.HIGHEST_PRECEDENCE + 99)
public class AuthChannelInterceptor implements ChannelInterceptor {

	private final CustomRemoteTokenServices remoteTokenServices;

	@Override
	public Message<?> preSend(Message<?> message, MessageChannel channel) {
		StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);
		//判断是否首次连接
		if (accessor != null && StompCommand.CONNECT.equals(accessor.getCommand())) {
			//登录鉴权
			String authorization = accessor.getFirstNativeHeader("Authorization");
			CustomUser user = SecurityUtils.getUser(this.remoteTokenServices.loadAuthentication(authorization,
					true));
			if (user != null) {
				//设置当前登录用户为clientId
				String clientId = accessor.getFirstNativeHeader("clientId");
				if (StringUtils.isNotBlank(clientId)) {
					showConnectInfo(user, clientId);
					Principal principal = () -> clientId;
					accessor.setUser(principal);
				} else {
					//无clientId参数，拒绝连接
					return null;
				}
			} else {
				//没有授权信息呢，决绝连接
				return null;
			}
		}
		//不是首次连接
		return message;
	}

	private void showConnectInfo(CustomUser user, String clientId) {
		try {
			log.info("用户：{} 正在使用客户端Id：{} 连接至Socket服务...", user.getUsername(), clientId);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}

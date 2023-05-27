package com.xjinyao.xcloud.socket.socketio.properties;

import com.xjinyao.xcloud.common.core.util.IPUtil;
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
@ConfigurationProperties(prefix = "socket.io")
public class SocketIoProperties {

    /**
     * 上下文，客户端连接时将会作为清理地址中的请求前缀
     */
    private String context = "/socket.io";

    /**
     * 是否启用
     */
    private Boolean enable = true;
    /**
     * 绑定host名称
     */
    private String hostName = IPUtil.getLocalIpAddress();
    /**
     * 绑定端口
     */
    private Integer port = 9096;
    /**
     * 协议升级超时时间（毫秒）
     */
    private Integer upgradeTimeout = 10000;
    /**
     * Ping消息间隔（毫秒）
     */
    private Integer pingTimeout = 60000;
    /**
     * Ping消息超时时间（毫秒）
     */
    private Integer pingInterval = 25000;
}

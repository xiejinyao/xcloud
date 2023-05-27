package com.jinyao.xdp.lock.redisson.properties.mode.base;

import lombok.Data;
import org.apache.commons.lang.StringUtils;
import org.redisson.config.BaseConfig;
import org.redisson.config.SslProvider;

import java.net.MalformedURLException;
import java.net.URL;

/**
 * 基础配置 {@link BaseConfig}
 * @author 谢进伟
 * @createDate 2022/8/26 08:47
 */
@Data
public class XBaseConfigProperties {

    /**
     * 如果池连接在一段超时时间内未被使用，并且当前连接数量大于最小空闲连接池大小，那么它将被关闭并从池中删除。值以毫秒为单位
     */
    protected Integer idleConnectionTimeout = 10000;

    /**
     * 连接任何Redis服务器超时。值以毫秒为单位。
     */
    protected Integer connectTimeout = 10000;

    /**
     * Redis服务器响应超时。当Redis命令成功发送时开始倒计时。值以毫秒为单位。
     */
    protected Integer timeout = 3000;

    /**
     * 如果 Redis 命令在 retryAttempts 后无法发送到 Redis 服务器，则会抛出错误。但如果它发送成功，那么超时将开始。
     */
    protected Integer retryAttempts = 3;

    /**
     * 以毫秒为单位的时间间隔，之后将执行另一次尝试发送 Redis 命令。
     */
    protected Integer retryInterval = 1500;

    /**
     * Redis鉴权密码。如果不需要，应该为空
     */
    protected String password;

    /**
     * Redis 服务器身份验证的用户名。需要 Redis 6.0+
     */
    protected String username;

    /**
     * 订阅每个Redis连接限制
     */
    protected Integer subscriptionsPerConnection = 5;

    /**
     * 客户端连接的名称
     */
    protected String clientName;

    /**
     * 在握手期间启用 SSL 端点识别，从而防止中间人攻击。
     */
    protected Boolean sslEnableEndpointIdentification = true;

    /**
     * 定义用于处理 SSL 连接的 SSL 提供程序（JDK 或 OPENSSL）。 OPENSSL 被认为是更快的实现，并且需要在类路径中添加 netty-tcnative-boringssl-static。
     */
    protected SslProvider sslProvider = SslProvider.JDK;

    /**
     * 定义 SSL 信任库的路径。它存储用于识别 SSL 连接的服务器端的证书。每次创建新连接时都会读取 SSL 信任库，并且可以动态重新加载。
     */
    protected String sslTruststoreUrl;

    /**
     * 定义 SSL 信任库的密码
     */
    protected String sslTruststorePassword;

    /**
     * 定义 SSL 密钥库的路径。它存储与其公钥对应的私钥和证书。如果 SSL 连接的服务器端需要客户端身份验证，则使用此选项。
     * SSL 密钥库在每次创建新连接时都会被读取，并且可以动态重新加载。
     */
    protected String sslKeystoreUrl;

    /**
     * 定义 SSL 密钥库的密码
     */
    protected String sslKeystorePassword;

    /**
     * 定义允许的 SSL 协议数组。 示例值：TLSv1.3、TLSv1.2、TLSv1.1、TLSv1
     */
    protected String[] sslProtocols;

    /**
     * 每个连接到 Redis 的 PING 命令发送间隔。以毫秒为单位定义。设置 0 禁用。
     */
    protected Integer pingConnectionInterval = 30000;

    /**
     * 启用 TCP keepAlive 进行连接。
     */
    protected Boolean keepAlive = false;

    /**
     * 启用 TCP noDelay 进行连接。
     */
    protected Boolean tcpNoDelay = true;

    public void copyPropertiesToConfig(BaseConfig config) {
        config.setIdleConnectionTimeout(this.getIdleConnectionTimeout())
                .setConnectTimeout(this.getConnectTimeout())
                .setTimeout(this.getTimeout())
                .setRetryAttempts(this.getRetryAttempts())
                .setRetryInterval(this.getRetryInterval())
                .setPassword(this.getPassword())
                .setUsername(this.getUsername())
                .setSubscriptionsPerConnection(this.getSubscriptionsPerConnection())
                .setClientName(this.getClientName())
                .setSslEnableEndpointIdentification(this.getSslEnableEndpointIdentification())
                .setSslProvider(this.getSslProvider())
                .setSslTruststore(parseUrl(this.getSslTruststoreUrl()))
                .setSslTruststorePassword(this.getSslTruststorePassword())
                .setSslKeystore(parseUrl(this.getSslKeystoreUrl()))
                .setSslKeystorePassword(this.getSslKeystorePassword())
                .setSslProtocols(this.getSslProtocols())
                .setPingConnectionInterval(this.getPingConnectionInterval())
                .setKeepAlive(this.getKeepAlive())
                .setTcpNoDelay(this.getTcpNoDelay());
    }

    private URL parseUrl(String urlStr) {
        if (StringUtils.isNotBlank(urlStr)) {
            try {
                return new URL(urlStr);
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
        }
        return null;
    }


}

package com.jinyao.xdp.lock.redisson.properties;

import com.jinyao.xdp.lock.redisson.enums.XCodecEnum;
import com.jinyao.xdp.lock.redisson.enums.XEventLoopGroupEnum;
import com.jinyao.xdp.lock.redisson.enums.XRedissonMode;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.kqueue.KQueueEventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import lombok.Data;
import org.redisson.client.codec.ByteArrayCodec;
import org.redisson.client.codec.Codec;
import org.redisson.client.codec.LongCodec;
import org.redisson.client.codec.StringCodec;
import org.redisson.codec.*;
import org.redisson.config.Config;
import org.redisson.config.TransportMode;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.concurrent.TimeUnit;

import static com.jinyao.xdp.lock.redisson.properties.XRedissonConfigProperties.ENABLED;
import static com.jinyao.xdp.lock.redisson.properties.XRedissonConfigProperties.REDISSON_PROPERTIES_PREFIX;

/**
 * Redis 锁配配置资源，属性配置参考：{@link Config}
 *
 * @author 谢进伟
 * @createDate 2022/8/25 17:55
 */
@Data
@ConfigurationProperties(prefix = REDISSON_PROPERTIES_PREFIX)
@ConditionalOnProperty(prefix = REDISSON_PROPERTIES_PREFIX, name = ENABLED, havingValue = "true")
public class XRedissonConfigProperties {

	public static final String REDISSON_PROPERTIES_PREFIX = "xdp.lock.redis.redisson";
	public static final String ENABLED = "enabled";
	public static final String REDISSON_MODE = "model";

	/**
	 * 是否启用
	 */
	private Boolean enabled = false;

	/**
	 * 模式
	 */
	private XRedissonMode model = XRedissonMode.SINGLE_INSTANCE;

	/**
	 * RTopic 对象的所有侦听器、RRemoteService 的调用处理程序、RTopic 对象和 RExecutorService 任务共享的线程数量。
	 */
	private Integer threads = 16;

	/**
	 * Redisson 使用的所有内部 redis 客户端之间共享的线程数量。 Redis 响应解码和命令发送中使用的 Netty 线程
	 */
	private Integer nettyThreads = 32;

	/**
	 * Redis 数据编解码器。在读取和写入 Redis 数据期间使用
	 */
	private XCodecEnum codec;

	/**
	 * 启用Redisson Reference特性的配置选项
	 */
	private Boolean referenceEnabled = true;

	/**
	 * 传输模式
	 */
	private TransportMode transportMode = TransportMode.NIO;

	/**
	 * 使用外部 EventLoopGroup。 EventLoopGroup 通过自己的线程处理所有与 Redis 服务器绑定的 Netty 连接。默认情况下，
	 * 每个 Redisson 客户端都会创建自己的 EventLoopGroup。因此，如果在同一个 JVM 中有多个 Redisson 实例，那么在它们之间共享
	 * 一个 EventLoopGroup 会很有用
	 */
	private XEventLoopGroupEnum eventLoopGroup;

	/**
	 * RLock的看门狗超时时长，以毫秒为单位。此参数仅在获取 RLock 对象时使用，但没有使用 leaseTimeout 参数。如果看门狗没有将其延长到下一个
	 * lockWatchdogTimeout 时间间隔，则锁定将在 lockWatchdogTimeout 之后过期。这可以防止由于 Redisson 客户端崩溃或无法以正确方式释放
	 * 锁的任何其他原因导致的无限锁定锁。
	 */
	private Long lockWatchdogTimeout = 30 * 1000L;

	/**
	 * 定义是否在获取锁后检查同步的slave数量和实际的slave数量。
	 */
	private Boolean checkLockSyncedSlaves = true;

	/**
	 * 以毫秒为单位的可靠主题看门狗超时。如果看门狗没有将其延长到下一个超时时间间隔，则可靠主题订阅者将在超时后过期。这可以防止由于 Redisson
	 * 客户端崩溃或订阅者无法再消费消息时的任何其他原因而导致主题中存储的消息无限增长。
	 */
	private Long reliableTopicWatchdogTimeout = TimeUnit.MINUTES.toMillis(10);

	/**
	 * 定义是保持 PubSub 消息按到达顺序处理还是同时处理消息。此设置仅适用于每个频道的 PubSub 消息。
	 */
	private Boolean keepPubSubOrder = true;

	/**
	 * 定义是否在 Redis 端使用 Lua-script 缓存。大多数 Redisson 方法都是基于 Lua 脚本的，打开此设置可以提高此类方法的执行速度并节省网络流量。
	 */
	private Boolean useScriptCache = false;

	/**
	 * 定义过期条目清理过程的最小延迟（以秒为单位）。应用于 JCache、RSetCache、RClusteredSetCache、RMapCache、RListMultimapCache、
	 * RSetMultimapCache、RLocalCachedMapCache、RClusteredLocalCachedMapCache 对象。
	 */
	private Integer minCleanUpDelay = 5;

	/**
	 * 定义过期条目清理过程的最大延迟（以秒为单位）。应用于 JCache、RSetCache、RClusteredSetCache、RMapCache、RListMultimapCache、
	 * RSetMultimapCache、RLocalCachedMapCache、RClusteredLocalCachedMapCache 对象。
	 */
	private Integer maxCleanUpDelay = 30 * 60;

	/**
	 * 定义过期条目清理过程中每个操作删除的过期键数量。应用于 JCache、RSetCache、RClusteredSetCache、RMapCache、RListMultimapCache、
	 * RSetMultimapCache、RLocalCachedMapCache、RClusteredLocalCachedMapCache 对象。
	 */
	private Integer cleanUpKeysAmount = 100;

	/**
	 * 定义是否向 Codec 提供 Thread ContextClassLoader。使用 Thread.getContextClassLoader() 可能会解决 Redis 响应解码期间出现的
	 * ClassNotFoundException 错误。如果在 Tomcat 和已部署的应用程序中都使用了 Redisson，则可能会出现此错误。
	 */
	private Boolean useThreadClassLoader = true;

	public void copyPropertiesToConfig(Config config) {

		Codec codec = null;
		XCodecEnum codecEnum = this.getCodec();
		if (codecEnum != null) {
			switch (codecEnum) {
				case MARSHALLING_CODEC:
					codec = new MarshallingCodec();
					break;
				case JSON_JACKSON_CODEC:
					codec = new JsonJacksonCodec();
					break;
				case KRYO5_CODEC:
					codec = new Kryo5Codec();
					break;
				case AVRO_JACKSON_CODEC:
					codec = new AvroJacksonCodec(Thread.currentThread().getContextClassLoader());
					break;
				case SMILE_JACKSON_CODEC:
					codec = new SmileJacksonCodec();
					break;
				case CBOR_JACKSON_CODEC:
					codec = new CborJacksonCodec();
					break;
				case MSG_PACK_JACKSON_CODEC:
					codec = new MsgPackJacksonCodec();
					break;
				case ION_JACKSON_CODEC:
					codec = new IonJacksonCodec();
					break;
				case SERIALIZATION_CODEC:
					codec = new SerializationCodec();
					break;
				case L_Z_4_CODEC:
					codec = new LZ4Codec();
					break;
				case SNAPPY_CODEC_V2:
					codec = new SnappyCodecV2();
					break;
				case STRING_CODEC:
					codec = new StringCodec();
					break;
				case LONG_CODEC:
					codec = new LongCodec();
					break;
				case BYTE_ARRAY_CODEC:
					codec = new ByteArrayCodec();
					break;
			}
		}

		EventLoopGroup eventLoopGroup = null;
		XEventLoopGroupEnum eventLoopGroupEnum = this.getEventLoopGroup();
		if (eventLoopGroupEnum != null) {
			switch (eventLoopGroupEnum) {
				case EPOLL_EVENT_LOOP_GROUP:
					eventLoopGroup = new EpollEventLoopGroup();
					break;
				case K_QUEUE_EVENT_LOOP_GROUP:
					eventLoopGroup = new KQueueEventLoopGroup();
					break;
				case NIO_EVENT_LOOP_GROUP:
					eventLoopGroup = new NioEventLoopGroup();
					break;
			}
		}


		config.setThreads(this.getThreads())
				.setNettyThreads(this.getNettyThreads())
				.setCodec(codec)
				.setTransportMode(this.getTransportMode())
				.setEventLoopGroup(eventLoopGroup)
				.setLockWatchdogTimeout(this.getLockWatchdogTimeout())
				.setCheckLockSyncedSlaves(this.getCheckLockSyncedSlaves())
				.setReliableTopicWatchdogTimeout(this.getReliableTopicWatchdogTimeout())
				.setKeepPubSubOrder(this.getKeepPubSubOrder())
				.setUseScriptCache(this.getUseScriptCache())
				.setMinCleanUpDelay(this.getMinCleanUpDelay())
				.setMaxCleanUpDelay(this.getMaxCleanUpDelay())
				.setCleanUpKeysAmount(this.getCleanUpKeysAmount())
				.setUseThreadClassLoader(this.getUseThreadClassLoader())
				.setReferenceEnabled(this.getReferenceEnabled());
	}
}

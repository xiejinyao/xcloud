package com.xjinyao.xcloud.job.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.concurrent.TimeUnit;

/**
 * @author 谢进伟
 * @createDate 2023/2/10 10:21
 */
@Data
@ConfigurationProperties(prefix = "job.executor.http")
public class HttpJobHandlerProperties {

    /**
     * 新连接的默认读取超时。值为 0 表示没有超时，否则值必须介于 1 和 Integer.MAX_VALUE 转换为毫秒时。
     */
    private Long readTimeout = 10L;

    /**
     * 完整呼叫的默认超时。值为 0 表示没有超时，否则值必须介于 1 和 Integer.MAX_VALUE 转换为毫秒时。
     */
    private TimeUnit readTimeoutUnit = TimeUnit.MILLISECONDS;

    /**
     * 新连接的默认写入超时。值为 0 表示没有超时，否则值必须介于 1 和 Integer.MAX_VALUE 转换为毫秒时。
     */
    private Long writeTimeout = 10L;

    /**
     * 写入超时单位
     */
    private TimeUnit writeTimeoutUnit = TimeUnit.MILLISECONDS;

    /**
     * 新连接的默认连接超时。值为 0 表示没有超时，否则值必须介于 1 和 Integer.MAX_VALUE 转换为毫秒时。
     */
    private Long connectTimeout = 10L;

    /**
     * 写入超时单位
     */
    private TimeUnit connectTimeoutUnit = TimeUnit.MILLISECONDS;

    /**
     * 调用超时
     */
    private Long callTimeout = 0L;

    /**
     * 调用超时单位
     */
    private TimeUnit callTimeoutUnit = TimeUnit.MILLISECONDS;

}

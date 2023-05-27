package com.xjinyao.xcloud.xxl.job.admin.api.dto;

import lombok.Builder;
import lombok.Data;
import lombok.experimental.Tolerate;
import org.springframework.http.HttpMethod;

import java.util.Collections;
import java.util.Map;

/**
 * http 处理器参数
 *
 * @author 谢进伟
 * @createDate 2023/2/10 10:05
 */
@Data
@Builder
public class HttpJobHandlerParams {

    /**
     * 方法
     */
    private HttpMethod method;

    /**
     * url
     */
    private String url;

    /**
     * url参数
     */
    @Builder.Default
    private Map<String, Object> urlParams = Collections.emptyMap();

    /**
     * 头信息
     */
    @Builder.Default
    private Map<String, String> headers = Collections.emptyMap();

    /**
     * 请求体
     */
    private String body;

    /**
     * 读取超时,单位：毫秒
     */
    @Builder.Default
    private Long readTimeout = 10L;

    /**
     * 写超时,单位：毫秒
     */
    @Builder.Default
    private Long writeTimeout = 10L;

    /**
     * 连接超时,单位：毫秒
     */
    @Builder.Default
    private Long connectTimeout = 10L;

    /**
     * 调用超时,单位：毫秒
     */
    @Builder.Default
    private Long callTimeout = 0L;

    @Tolerate
    public HttpJobHandlerParams() {

    }

}

package com.xjinyao.xcloud.job.config;

import com.xjinyao.xcloud.job.properties.HttpJobHandlerProperties;
import com.xxl.job.core.context.XxlJobHelper;
import okhttp3.*;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.Nullable;
import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.util.List;

/**
 * @author 谢进伟
 * @createDate 2023/2/10 10:31
 */
@Configuration
@EnableConfigurationProperties({
        HttpJobHandlerProperties.class
})
public class JobExecutorConfiguration {

    @Bean
    public OkHttpClient okHttpClient(HttpJobHandlerProperties properties) {
        return new OkHttpClient.Builder()
                .readTimeout(properties.getReadTimeout(), properties.getReadTimeoutUnit())
                .writeTimeout(properties.getWriteTimeout(), properties.getWriteTimeoutUnit())
                .callTimeout(properties.getCallTimeout(), properties.getCallTimeoutUnit())
                .connectTimeout(properties.getConnectTimeout(), properties.getConnectTimeoutUnit())
                .addInterceptor(chain -> {
                    Request request = chain.request();
                    XxlJobHelper.log("http request url is {}", request.url());
                    XxlJobHelper.log("http request method is {}", request.method());
                    XxlJobHelper.log("http request headers is {}", request.headers());
                    XxlJobHelper.log("http request body is {}", request.body());
                    return chain.proceed(request);
                })
                .eventListener(new EventListener() {
                    /**
                     * Invoked as soon as a call is enqueued or executed by a client. In case of thread or stream
                     * limits, this call may be executed well before processing the request is able to begin.
                     *
                     * <p>This will be invoked only once for a single {@link Call}. Retries of different routes
                     * or redirects will be handled within the boundaries of a single callStart and {@link
                     * #callEnd}/{@link #callFailed} pair.
                     *
                     * @param call
                     */
                    @Override
                    public void callStart(Call call) {
                        super.callStart(call);
                        Request request = call.request();
                        XxlJobHelper.log("callStart for url:{}", request.url());
                    }

                    /**
                     * Invoked just prior to a DNS lookup. See {@link Dns#lookup(String)}.
                     *
                     * <p>This can be invoked more than 1 time for a single {@link Call}. For example, if the response
                     * to the {@link Call#request()} is a redirect to a different host.
                     *
                     * <p>If the {@link Call} is able to reuse an existing pooled connection, this method will not be
                     * invoked. See {@link ConnectionPool}.
                     *
                     * @param call
                     * @param domainName
                     */
                    @Override
                    public void dnsStart(Call call, String domainName) {
                        super.dnsStart(call, domainName);
                        Request request = call.request();
                        XxlJobHelper.log("dnsStart for url:{}", request.url());
                    }

                    /**
                     * Invoked immediately after a DNS lookup.
                     *
                     * <p>This method is invoked after {@link #dnsStart}.
                     *
                     * @param call
                     * @param domainName
                     * @param inetAddressList
                     */
                    @Override
                    public void dnsEnd(Call call, String domainName, List<InetAddress> inetAddressList) {
                        super.dnsEnd(call, domainName, inetAddressList);
                        Request request = call.request();
                        XxlJobHelper.log("dnsEnd => domainName is {} inetAddressList is {} for url:{}", domainName, inetAddressList, request.url());
                    }

                    /**
                     * Invoked just prior to initiating a socket connection.
                     *
                     * <p>This method will be invoked if no existing connection in the {@link ConnectionPool} can be
                     * reused.
                     *
                     * <p>This can be invoked more than 1 time for a single {@link Call}. For example, if the response
                     * to the {@link Call#request()} is a redirect to a different address, or a connection is retried.
                     *
                     * @param call
                     * @param inetSocketAddress
                     * @param proxy
                     */
                    @Override
                    public void connectStart(Call call, InetSocketAddress inetSocketAddress, Proxy proxy) {
                        super.connectStart(call, inetSocketAddress, proxy);
                    }

                    /**
                     * Invoked just prior to initiating a TLS connection.
                     *
                     * <p>This method is invoked if the following conditions are met:
                     * <ul>
                     * <li>The {@link Call#request()} requires TLS.</li>
                     * <li>No existing connection from the {@link ConnectionPool} can be reused.</li>
                     * </ul>
                     *
                     * <p>This can be invoked more than 1 time for a single {@link Call}. For example, if the response
                     * to the {@link Call#request()} is a redirect to a different address, or a connection is retried.
                     *
                     * @param call
                     */
                    @Override
                    public void secureConnectStart(Call call) {
                        super.secureConnectStart(call);
                    }

                    /**
                     * Invoked immediately after a TLS connection was attempted.
                     *
                     * <p>This method is invoked after {@link #secureConnectStart}.
                     *
                     * @param call
                     * @param handshake
                     */
                    @Override
                    public void secureConnectEnd(Call call, @Nullable Handshake handshake) {
                        super.secureConnectEnd(call, handshake);
                    }

                    /**
                     * Invoked immediately after a socket connection was attempted.
                     *
                     * <p>If the {@code call} uses HTTPS, this will be invoked after
                     * {@link #secureConnectEnd(Call, Handshake)}, otherwise it will invoked after
                     * {@link #connectStart(Call, InetSocketAddress, Proxy)}.
                     *
                     * @param call
                     * @param inetSocketAddress
                     * @param proxy
                     * @param protocol
                     */
                    @Override
                    public void connectEnd(Call call, InetSocketAddress inetSocketAddress, Proxy proxy, @Nullable Protocol protocol) {
                        super.connectEnd(call, inetSocketAddress, proxy, protocol);
                    }

                    /**
                     * Invoked when a connection attempt fails. This failure is not terminal if further routes are
                     * available and failure recovery is enabled.
                     *
                     * <p>If the {@code call} uses HTTPS, this will be invoked after {@link #secureConnectEnd(Call,
                     * Handshake)}, otherwise it will invoked after {@link #connectStart(Call, InetSocketAddress,
                     * Proxy)}.
                     *
                     * @param call
                     * @param inetSocketAddress
                     * @param proxy
                     * @param protocol
                     * @param ioe
                     */
                    @Override
                    public void connectFailed(Call call, InetSocketAddress inetSocketAddress, Proxy proxy, @Nullable Protocol protocol, IOException ioe) {
                        super.connectFailed(call, inetSocketAddress, proxy, protocol, ioe);
                    }

                    /**
                     * Invoked after a connection has been acquired for the {@code call}.
                     *
                     * <p>This can be invoked more than 1 time for a single {@link Call}. For example, if the response
                     * to the {@link Call#request()} is a redirect to a different address.
                     *
                     * @param call
                     * @param connection
                     */
                    @Override
                    public void connectionAcquired(Call call, Connection connection) {
                        super.connectionAcquired(call, connection);
                    }

                    /**
                     * Invoked after a connection has been released for the {@code call}.
                     *
                     * <p>This method is always invoked after {@link #connectionAcquired(Call, Connection)}.
                     *
                     * <p>This can be invoked more than 1 time for a single {@link Call}. For example, if the response
                     * to the {@link Call#request()} is a redirect to a different address.
                     *
                     * @param call
                     * @param connection
                     */
                    @Override
                    public void connectionReleased(Call call, Connection connection) {
                        super.connectionReleased(call, connection);
                    }

                    /**
                     * Invoked just prior to sending request headers.
                     *
                     * <p>The connection is implicit, and will generally relate to the last
                     * {@link #connectionAcquired(Call, Connection)} event.
                     *
                     * <p>This can be invoked more than 1 time for a single {@link Call}. For example, if the response
                     * to the {@link Call#request()} is a redirect to a different address.
                     *
                     * @param call
                     */
                    @Override
                    public void requestHeadersStart(Call call) {
                        super.requestHeadersStart(call);
                    }

                    /**
                     * Invoked immediately after sending request headers.
                     *
                     * <p>This method is always invoked after {@link #requestHeadersStart(Call)}.
                     *
                     * @param call
                     * @param request the request sent over the network. It is an error to access the body of this
                     *                request.
                     */
                    @Override
                    public void requestHeadersEnd(Call call, Request request) {
                        super.requestHeadersEnd(call, request);
                    }

                    /**
                     * Invoked just prior to sending a request body.  Will only be invoked for request allowing and
                     * having a request body to send.
                     *
                     * <p>The connection is implicit, and will generally relate to the last
                     * {@link #connectionAcquired(Call, Connection)} event.
                     *
                     * <p>This can be invoked more than 1 time for a single {@link Call}. For example, if the response
                     * to the {@link Call#request()} is a redirect to a different address.
                     *
                     * @param call
                     */
                    @Override
                    public void requestBodyStart(Call call) {
                        super.requestBodyStart(call);
                    }

                    /**
                     * Invoked immediately after sending a request body.
                     *
                     * <p>This method is always invoked after {@link #requestBodyStart(Call)}.
                     *
                     * @param call
                     * @param byteCount
                     */
                    @Override
                    public void requestBodyEnd(Call call, long byteCount) {
                        super.requestBodyEnd(call, byteCount);
                    }

                    /**
                     * Invoked when a request fails to be written.
                     *
                     * <p>This method is invoked after {@link #requestHeadersStart} or {@link #requestBodyStart}. Note
                     * that request failures do not necessarily fail the entire call.
                     *
                     * @param call
                     * @param ioe
                     */
                    @Override
                    public void requestFailed(Call call, IOException ioe) {
                        super.requestFailed(call, ioe);
                    }

                    /**
                     * Invoked just prior to receiving response headers.
                     *
                     * <p>The connection is implicit, and will generally relate to the last
                     * {@link #connectionAcquired(Call, Connection)} event.
                     *
                     * <p>This can be invoked more than 1 time for a single {@link Call}. For example, if the response
                     * to the {@link Call#request()} is a redirect to a different address.
                     *
                     * @param call
                     */
                    @Override
                    public void responseHeadersStart(Call call) {
                        super.responseHeadersStart(call);
                    }

                    /**
                     * Invoked immediately after receiving response headers.
                     *
                     * <p>This method is always invoked after {@link #responseHeadersStart}.
                     *
                     * @param call
                     * @param response the response received over the network. It is an error to access the body of
                     *                 this response.
                     */
                    @Override
                    public void responseHeadersEnd(Call call, Response response) {
                        super.responseHeadersEnd(call, response);
                    }

                    /**
                     * Invoked just prior to receiving the response body.
                     *
                     * <p>The connection is implicit, and will generally relate to the last
                     * {@link #connectionAcquired(Call, Connection)} event.
                     *
                     * <p>This will usually be invoked only 1 time for a single {@link Call},
                     * exceptions are a limited set of cases including failure recovery.
                     *
                     * @param call
                     */
                    @Override
                    public void responseBodyStart(Call call) {
                        super.responseBodyStart(call);
                    }

                    /**
                     * Invoked immediately after receiving a response body and completing reading it.
                     *
                     * <p>Will only be invoked for requests having a response body e.g. won't be invoked for a
                     * websocket upgrade.
                     *
                     * <p>This method is always invoked after {@link #requestBodyStart(Call)}.
                     *
                     * @param call
                     * @param byteCount
                     */
                    @Override
                    public void responseBodyEnd(Call call, long byteCount) {
                        super.responseBodyEnd(call, byteCount);
                    }

                    /**
                     * Invoked when a response fails to be read.
                     *
                     * <p>This method is invoked after {@link #responseHeadersStart} or {@link #responseBodyStart}.
                     * Note that response failures do not necessarily fail the entire call.
                     *
                     * @param call
                     * @param ioe
                     */
                    @Override
                    public void responseFailed(Call call, IOException ioe) {
                        super.responseFailed(call, ioe);
                    }

                    /**
                     * Invoked immediately after a call has completely ended.  This includes delayed consumption
                     * of response body by the caller.
                     *
                     * <p>This method is always invoked after {@link #callStart(Call)}.
                     *
                     * @param call
                     */
                    @Override
                    public void callEnd(Call call) {
                        super.callEnd(call);
                    }

                    /**
                     * Invoked when a call fails permanently.
                     *
                     * <p>This method is always invoked after {@link #callStart(Call)}.
                     *
                     * @param call
                     * @param ioe
                     */
                    @Override
                    public void callFailed(Call call, IOException ioe) {
                        super.callFailed(call, ioe);
                    }
                })
                .build();
    }
}

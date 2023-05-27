package com.xjinyao.xcloud.job.executor;

import com.alibaba.fastjson.JSON;
import com.xjinyao.xcloud.common.core.util.StringUtils;
import com.xjinyao.xcloud.xxl.job.admin.api.constants.JobExecutors;
import com.xjinyao.xcloud.xxl.job.admin.api.dto.HttpJobHandlerParams;
import com.xxl.job.core.context.XxlJobHelper;
import com.xxl.job.core.handler.annotation.XxlJob;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.apache.commons.collections4.MapUtils;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;


/**
 * HTTP 通用执行器
 *
 * @author 谢进伟
 * @createDate 2023/2/10 10:03
 */
@Component
@Slf4j
@AllArgsConstructor
public class HttpJobExecutor {

    private static final MediaType MEDIA_TYPE_APPLICATION_JSON = MediaType.get("application/json; charset=utf-8");

    public final OkHttpClient client;

    /**
     * 处理跨平台Http任务
     *
     * @throws Exception 异常
     */
    @XxlJob(JobExecutors.HTTP_JOB_EXECUTOR)
    public void handler() throws Exception {
        String paramStr = XxlJobHelper.getJobParam();
        if (StringUtils.isBlank(paramStr)) {
            XxlJobHelper.log("param[" + paramStr + "] invalid.");
            XxlJobHelper.handleFail();
            return;
        }
        HttpJobHandlerParams params = JSON.parseObject(paramStr, HttpJobHandlerParams.class);

        if (params == null) {
            XxlJobHelper.log("param format [" + paramStr + "] invalid.");
            XxlJobHelper.handleFail();
            return;
        }

        String url = params.getUrl();
        if (StringUtils.isBlank(url)) {
            XxlJobHelper.log("request url [" + url + "] invalid.");
            XxlJobHelper.handleFail();
            return;
        }

        OkHttpClient.Builder httpClientBuilder = client.newBuilder();
        if (params.getReadTimeout() != null) {
            httpClientBuilder.readTimeout(params.getReadTimeout(), TimeUnit.MILLISECONDS);
        }
        if (params.getWriteTimeout() != null) {
            httpClientBuilder.writeTimeout(params.getWriteTimeout(), TimeUnit.MILLISECONDS);
        }
        if (params.getCallTimeout() != null) {
            httpClientBuilder.callTimeout(params.getCallTimeout(), TimeUnit.MILLISECONDS);
        }
        if (params.getConnectTimeout() != null) {
            httpClientBuilder.connectTimeout(params.getConnectTimeout(), TimeUnit.MILLISECONDS);
        }

        Map<String, Object> urlParams = params.getUrlParams();
        if (MapUtils.isNotEmpty(urlParams)) {
            List<String> urlParamList = new ArrayList<>();
            urlParams.forEach((k, v) -> urlParamList.add(k + "=" + v));
            String suffix = StringUtils.join(urlParamList, "&");
            url += (url.contains("?") ? "&" : "?") + suffix;
        }

        Request.Builder request = new Request.Builder()
                .url(url)
                .headers(Headers.of(params.getHeaders()));

        String bodyContent = StringUtils.defaultString(params.getBody(), StringUtils.EMPTY);
        RequestBody body = RequestBody.create(MEDIA_TYPE_APPLICATION_JSON, bodyContent);
        switch (params.getMethod()) {
            case GET:
                request.get();
                break;
            case POST:
                request.post(body);
                break;
            case PUT:
                request.put(body);
                break;
            case DELETE:
                if (StringUtils.isNotBlank(params.getBody())) {
                    request.delete();
                } else {
                    request.delete(body);
                }
                break;
            case HEAD:
                request.head();
                break;
            case PATCH:
                request.patch(body);
                break;
            default:
                XxlJobHelper.log("request method [" + params.getMethod() + "] invalid.");
                XxlJobHelper.handleFail();
                return;
        }
        try (Response response = httpClientBuilder.build().newCall(request.build()).execute()) {
            XxlJobHelper.log("http response code is {}", response.code());
            XxlJobHelper.log("http response message is {}", response.message());
            XxlJobHelper.log("http response headers is {}", response.headers());
            XxlJobHelper.log("http response body is {}", response.body());
            if (response.isSuccessful()) {
                XxlJobHelper.log("http job execute success!");
                XxlJobHelper.handleSuccess();
            } else {
                XxlJobHelper.log("http job execute fail!");
                XxlJobHelper.handleFail();
            }
        } catch (Exception e) {
            XxlJobHelper.log("An exception occurred in the request!");
            XxlJobHelper.log(e);
            XxlJobHelper.handleFail();
        }
    }
}

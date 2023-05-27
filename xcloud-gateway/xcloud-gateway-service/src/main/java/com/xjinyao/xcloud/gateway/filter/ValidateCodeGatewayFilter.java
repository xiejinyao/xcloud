package com.xjinyao.xcloud.gateway.filter;

import cn.hutool.core.util.StrUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.xjinyao.xcloud.common.core.constant.SecurityConstants;
import com.xjinyao.xcloud.common.core.exception.ValidateCodeException;
import com.xjinyao.xcloud.common.core.redis.constant.CacheConstants;
import com.xjinyao.xcloud.common.core.util.R;
import com.xjinyao.xcloud.common.core.util.WebUtils;
import com.xjinyao.xcloud.gateway.properties.GatewayConfigProperties;
import com.xjinyao.xcloud.interactive.captcha.core.service.CaptchaService;
import com.xjinyao.xcloud.interactive.captcha.core.util.ResponseModel;
import com.xjinyao.xcloud.interactive.captcha.core.vo.CaptchaVO;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

/**
 * @date 2018/7/4 验证码处理
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class ValidateCodeGatewayFilter extends AbstractGatewayFilterFactory {

    private final GatewayConfigProperties configProperties;
    private final ObjectMapper objectMapper;
    private final RedisTemplate redisTemplate;
    private final CaptchaService captchaService;

    private final String GRANT_TYPE = "grant_type";
    private final String CODE = "code";
    private final String RANDOMSTR = "randomStr";
    private final String MOBILE = "mobile";
    private final String CAPTCHA_VERIFICATION = "captchaVerification";

    @Override
    public GatewayFilter apply(Object config) {
        return (exchange, chain) -> {
            ServerHttpRequest request = exchange.getRequest();

            // 不是登录请求，直接向下执行
            if (!StrUtil.containsAnyIgnoreCase(request.getURI().getPath(), SecurityConstants.OAUTH_TOKEN_URL)) {
                return chain.filter(exchange);
            }

            // 刷新token，直接向下执行
            String grantType;
            String code;
            String randomStr;
            String captchaVerification;
            if (HttpMethod.POST.equals(request.getMethod())) {
                HttpHeaders requestHeaders = request.getHeaders();
                grantType = requestHeaders.getFirst(GRANT_TYPE);
                code = requestHeaders.getFirst(CODE);
                captchaVerification = requestHeaders.getFirst(CAPTCHA_VERIFICATION);
                randomStr = requestHeaders.getFirst(RANDOMSTR);
                if (StrUtil.isBlank(randomStr)) {
                    randomStr = requestHeaders.getFirst(MOBILE);
                }
            } else if (HttpMethod.GET.equals(request.getMethod())) {
                grantType = request.getQueryParams().getFirst(GRANT_TYPE);
                code = request.getQueryParams().getFirst(CODE);
                captchaVerification = request.getQueryParams().getFirst(CAPTCHA_VERIFICATION);
                randomStr = request.getQueryParams().getFirst(RANDOMSTR);
                if (StrUtil.isBlank(randomStr)) {
                    randomStr = request.getQueryParams().getFirst(MOBILE);
                }
            } else {
                return chain.filter(exchange);
            }
            if (StrUtil.equals(SecurityConstants.REFRESH_TOKEN, grantType)) {
                return chain.filter(exchange);
            }

            // 终端设置不校验， 直接向下执行
            try {
                String[] clientInfos = WebUtils.getClientId(request);
                if (configProperties.getIgnoreClients().contains(clientInfos[0])) {
                    return chain.filter(exchange);
                }

                // 校验验证码
                checkCode(code, captchaVerification, randomStr);
            } catch (Exception e) {
                ServerHttpResponse response = exchange.getResponse();
                response.setStatusCode(HttpStatus.PRECONDITION_REQUIRED);
                response.getHeaders().setContentType(MediaType.APPLICATION_JSON);

                final String errMsg = e.getMessage();
                return response.writeWith(Mono.create(monoSink -> {
                    try {
                        byte[] bytes = objectMapper.writeValueAsBytes(R.failed(errMsg));
                        DataBuffer dataBuffer = response.bufferFactory().wrap(bytes);

                        monoSink.success(dataBuffer);
                    } catch (JsonProcessingException jsonProcessingException) {
                        log.error("对象输出异常", jsonProcessingException);
                        monoSink.error(jsonProcessingException);
                    }
                }));
            }

            return chain.filter(exchange);
        };
    }

    /**
     * 检查code
     *
     * @param code                验证码
     * @param captchaVerification 拼图验证信息
     * @param randomStr           随机值
     */
    @SneakyThrows
    private void checkCode(String code, String captchaVerification, String randomStr) {
        if (StrUtil.isBlank(code)) {
            throw new ValidateCodeException("验证码不能为空");
        }

        if (StringUtils.isNoneBlank(captchaVerification)) {
            CaptchaVO captchaVO = new CaptchaVO();
            captchaVO.setCaptchaVerification(captchaVerification);
            ResponseModel response = captchaService.verification(captchaVO);
            if (!response.isSuccess()) {
                throw new ValidateCodeException(response.getRepMsg());
            }
        }

        // 当只有人机行为验证时，code的值为前端验证之后的返回的二次验证参数captchaVerification信息;当没有人机行为验证时code为简单的验证码
        CaptchaVO captchaVO = new CaptchaVO();
        captchaVO.setCaptchaVerification(code);
        ResponseModel response = captchaService.verification(captchaVO);

        if (!response.isSuccess()) {
            String key = CacheConstants.DEFAULT_CODE_KEY + randomStr;
            if (!redisTemplate.hasKey(key)) {
                throw new ValidateCodeException("验证码不合法");
            }

            Object codeObj = redisTemplate.opsForValue().get(key);

            if (codeObj == null) {
                throw new ValidateCodeException("验证码不合法");
            }

            String saveCode = codeObj.toString();
            if (StrUtil.isBlank(saveCode)) {
                redisTemplate.delete(key);
                throw new ValidateCodeException("验证码不合法");
            }

            if (!StrUtil.equals(saveCode, code)) {
                redisTemplate.delete(key);
                throw new ValidateCodeException("验证码错误");
            }

            redisTemplate.delete(key);
        }
    }

}

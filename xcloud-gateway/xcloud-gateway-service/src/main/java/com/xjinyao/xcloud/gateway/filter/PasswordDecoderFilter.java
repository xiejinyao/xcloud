package com.xjinyao.xcloud.gateway.filter;

import cn.hutool.core.codec.Base64;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.Mode;
import cn.hutool.crypto.Padding;
import cn.hutool.crypto.symmetric.AES;
import cn.hutool.http.HttpUtil;
import com.xjinyao.xcloud.common.core.constant.SecurityConstants;
import com.xjinyao.xcloud.gateway.properties.GatewayConfigProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpMethod;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Mono;

import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

/**
 * @date 2019/2/1 密码解密工具类
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class PasswordDecoderFilter extends AbstractGatewayFilterFactory {

    private static final String PASSWORD = "password";

    private static final String KEY_ALGORITHM = "AES";

    private final GatewayConfigProperties configProperties;

    private static String decryptAES(String data, String pass) {
        AES aes = new AES(Mode.CBC, Padding.NoPadding, new SecretKeySpec(pass.getBytes(), KEY_ALGORITHM),
                new IvParameterSpec(pass.getBytes()));
        byte[] result = aes.decrypt(Base64.decode(data.getBytes(StandardCharsets.UTF_8)));
        return new String(result, StandardCharsets.UTF_8);
    }

    @Override
    public GatewayFilter apply(Object config) {
        return (exchange, chain) -> {
            ServerHttpRequest request = exchange.getRequest();

            // 不是登录请求，直接向下执行
            if (!StrUtil.containsAnyIgnoreCase(request.getURI().getPath(), SecurityConstants.OAUTH_TOKEN_URL)) {
                return chain.filter(exchange);
            }

            URI uri = exchange.getRequest().getURI();
            Map<String, String> paramMap;
            String password;
            if (HttpMethod.GET.equals(request.getMethod())) {
                String queryParam = uri.getRawQuery();
                paramMap = HttpUtil.decodeParamMap(queryParam, StandardCharsets.UTF_8);
                password = paramMap.get(PASSWORD);
            } else if (HttpMethod.POST.equals(request.getMethod())) {
                paramMap = new HashMap<>();
                password = request.getHeaders().getFirst(PASSWORD);
            } else {
                return chain.filter(exchange);
            }
            if (StrUtil.isNotBlank(password)) {
                try {
                    password = decryptAES(password, configProperties.getEncodeKey());
                } catch (Exception e) {
                    log.error("密码解密失败:{}", password);
                    return Mono.error(e);
                }
                paramMap.put(PASSWORD, password.trim());
            }

            URI newUri = UriComponentsBuilder.fromUri(uri).replaceQuery(HttpUtil.toParams(paramMap)).build(true)
                    .toUri();

            ServerHttpRequest newRequest = exchange.getRequest().mutate().uri(newUri).build();
            return chain.filter(exchange.mutate().request(newRequest).build());
        };
    }

}

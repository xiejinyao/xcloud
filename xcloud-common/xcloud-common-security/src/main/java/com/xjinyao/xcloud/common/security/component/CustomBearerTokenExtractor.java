package com.xjinyao.xcloud.common.security.component;

import com.xjinyao.xcloud.common.security.properties.PermitAllUrlProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.provider.authentication.BearerTokenExtractor;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.PathMatcher;

import javax.servlet.http.HttpServletRequest;

/**
 * 改造 {@link BearerTokenExtractor} 对公开权限的请求不进行校验
 *
 * @date 2020.05.15
 */
@Component
@RequiredArgsConstructor
public class CustomBearerTokenExtractor extends BearerTokenExtractor {

    private final PathMatcher pathMatcher = new AntPathMatcher();

    private final PermitAllUrlProperties urlProperties;

    @Override
    public Authentication extract(HttpServletRequest request) {
        boolean match = urlProperties.getUrls().stream()
                .anyMatch(url -> pathMatcher.match(url, request.getRequestURI()));

        return match ? null : super.extract(request);
    }

}

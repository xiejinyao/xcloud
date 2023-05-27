package com.xjinyao.xcloud.common.security.component;

import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import com.xjinyao.xcloud.admin.api.dto.DataPermission;
import com.xjinyao.xcloud.common.core.constant.SecurityConstants;
import com.xjinyao.xcloud.common.security.service.CustomUser;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.oauth2.provider.token.UserAuthenticationConverter;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.concurrent.atomic.AtomicReference;

/**
 * @date 2019-03-07
 * <p>
 * 根据checktoken 的结果转化用户信息
 */
public class CustomUserAuthenticationConverter implements UserAuthenticationConverter, AuthenticationConverter {

    private static final String N_A = "N/A";
    public static final String DEFAULT_TOKEN_CONVERTER_TYPE = "XCLOUD";

    @Override
    public String getType() {
        return DEFAULT_TOKEN_CONVERTER_TYPE;
    }

    /**
     * Extract information about the user to be used in an access token (i.e. for resource
     * servers).
     *
     * @param authentication an authentication representing a user
     * @return a map of key values representing the unique information about the user
     */
    @Override
    public Map<String, ?> convertUserAuthentication(Authentication authentication) {
        Map<String, Object> response = new LinkedHashMap<>();
        response.put(USERNAME, authentication.getName());
        if (authentication.getAuthorities() != null && !authentication.getAuthorities().isEmpty()) {
            response.put(AUTHORITIES, AuthorityUtils.authorityListToSet(authentication.getAuthorities()));
        }
        return response;
    }

    /**
     * Inverse of {@link #convertUserAuthentication(Authentication)}. Extracts an
     * Authentication from a map.
     *
     * @param map a map of user information
     * @return an Authentication representing the user or null if there is none
     */
    @Override
    public Authentication extractAuthentication(Map<String, ?> map) {
        if (map.containsKey(USERNAME)) {
            Collection<? extends GrantedAuthority> authorities = getAuthorities(map);

            String username = (String) map.get(SecurityConstants.DETAILS_USERNAME);
            Integer id = (Integer) map.get(SecurityConstants.DETAILS_USER_ID);
            String organizationId = StrUtil.utf8Str(map.get(SecurityConstants.DETAILS_ORGANIZATION_ID));
            String organizationCode = StrUtil.utf8Str(map.get(SecurityConstants.DETAILS_ORGANIZATION_CODE));
            Object extendedParametersObj = map.get(SecurityConstants.EXTENDED_PARAMETERS);
            Map<String, Object> extendedParameters = new HashMap<>();
            if (extendedParametersObj instanceof Map) {
                extendedParameters = (Map<String, Object>) extendedParametersObj;
            }

            AtomicReference<DataPermission> dataPermission = new AtomicReference<>(null);
            Optional.ofNullable(map.get(SecurityConstants.DATA_PERMISSION))
                    .ifPresent(dataPermissionObj -> dataPermission.set(JSON.toJavaObject(JSON.parseObject(
                            JSON.toJSONString(dataPermissionObj)), DataPermission.class)));

            CustomUser user = new CustomUser(id,
                    organizationId,
                    organizationCode,
                    username,
                    N_A,
                    true,
                    true,
                    true,
                    true,
                    authorities,
                    dataPermission.get(),
                    extendedParameters);
            return new UsernamePasswordAuthenticationToken(user, N_A, authorities);
        }
        return null;
    }

    private Collection<? extends GrantedAuthority> getAuthorities(Map<String, ?> map) {
        Object authorities = map.get(AUTHORITIES);
        if (authorities instanceof String) {
            return AuthorityUtils.commaSeparatedStringToAuthorityList((String) authorities);
        }
        if (authorities instanceof Collection) {
            return AuthorityUtils.commaSeparatedStringToAuthorityList(
                    StringUtils.collectionToCommaDelimitedString((Collection<?>) authorities));
        }
        return AuthorityUtils.NO_AUTHORITIES;
    }

}

package com.xjinyao.xcloud.common.security.service;

import com.xjinyao.xcloud.common.core.redis.constant.CacheConstants;
import lombok.SneakyThrows;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.oauth2.provider.ClientDetails;
import org.springframework.security.oauth2.provider.client.JdbcClientDetailsService;

import javax.sql.DataSource;

/**
 * @date 2019/2/1
 * <p>
 * see JdbcClientDetailsService
 */
public class CustomClientDetailsService extends JdbcClientDetailsService {

    public CustomClientDetailsService(DataSource dataSource) {
        super(dataSource);
    }

    /**
     * 重写原生方法支持redis缓存
     *
     * @param clientId
     * @return
     */
    @Override
    @SneakyThrows
    @Cacheable(value = CacheConstants.CLIENT_DETAILS_KEY, key = "#clientId", unless = "#result == null")
    public ClientDetails loadClientByClientId(String clientId) {
        return super.loadClientByClientId(clientId);
    }

}

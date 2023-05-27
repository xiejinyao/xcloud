package com.xjinyao.xcloud.admin.service;

import com.xjinyao.xcloud.common.core.redis.constant.CacheConstants;
import org.springframework.cache.annotation.CacheEvict;

/**
 * @author mengjiajie
 * @description
 * @createDate 2023/3/8 11:22
 */
public interface CacheEvictService {

    @CacheEvict(value = CacheConstants.ORGANIZATION_BY_PARENT_ID,allEntries = true)
    default void evictOrganization(){

    }

}

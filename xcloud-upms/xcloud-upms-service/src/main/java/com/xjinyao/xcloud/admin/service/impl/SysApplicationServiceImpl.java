package com.xjinyao.xcloud.admin.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xjinyao.xcloud.admin.api.entity.SysApplication;
import com.xjinyao.xcloud.admin.mapper.SysApplicationMapper;
import com.xjinyao.xcloud.admin.service.ISysApplicationService;
import com.xjinyao.xcloud.common.core.redis.constant.CacheConstants;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;

/**
 * 应用管理
 *
 * @author 谢进伟
 * @date 2021-02-23 11:25:53
 */
@Service
public class SysApplicationServiceImpl extends ServiceImpl<SysApplicationMapper, SysApplication> implements ISysApplicationService {

    @Override
    @CacheEvict(value = CacheConstants.APPLICATION_RESOURCES, key = "#applicationCode")
    public void cacheEvictByApplicationCode(String applicationCode) {
        //此方法用来清理应用根据编码的缓存详情信息
    }
}

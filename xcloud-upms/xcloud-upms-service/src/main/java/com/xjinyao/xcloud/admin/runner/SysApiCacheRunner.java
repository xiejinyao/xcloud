package com.xjinyao.xcloud.admin.runner;

import cn.hutool.core.collection.CollectionUtil;
import com.xjinyao.xcloud.admin.api.entity.SysApi;
import com.xjinyao.xcloud.admin.service.ISysApiService;
import com.xjinyao.xcloud.core.rule.po.ApiStatusList;
import com.xjinyao.xcloud.core.rule.service.IRuleCacheService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * @author 谢进伟
 * @description 初始化数据库中配置的接口信息到缓存中
 * @createDate 2020/11/17 15:28
 */
@Slf4j
@Component
@AllArgsConstructor
public class SysApiCacheRunner implements ApplicationRunner {

    private final ISysApiService sysApiService;

    private final IRuleCacheService ruleCacheService;

    @Override
    public void run(ApplicationArguments args) {
        List<SysApi> list = sysApiService.list();
        if (CollectionUtil.isNotEmpty(list)) {
            List<ApiStatusList> apis = new ArrayList<>();
            list.forEach(api -> {
                Boolean status = api.getStatus();
                status = status == null ? false : true;
                apis.add(new ApiStatusList(api.getServiceId(), HttpMethod.valueOf(api.getMethod()), api.getPattern(),
                        status));
            });
            ruleCacheService.cacheApis(apis);
        }
    }
}

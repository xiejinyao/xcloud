package com.xjinyao.xcloud.admin.runner;

import cn.hutool.core.collection.CollectionUtil;
import com.xjinyao.xcloud.admin.api.entity.SysRoute;
import com.xjinyao.xcloud.admin.service.ISysRouteService;
import com.xjinyao.xcloud.core.rule.po.RouterStatusList;
import com.xjinyao.xcloud.core.rule.service.IRuleCacheService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * @author 谢进伟
 * @description 初始化数据库中配置的路由信息到缓存中
 * @createDate 2020/11/17 16:54
 */
@Slf4j
@Component
@AllArgsConstructor
public class SysRouteCacheRunner implements ApplicationRunner {

    private final ISysRouteService sysRouteService;

    private final IRuleCacheService ruleCacheService;

    @Override
    public void run(ApplicationArguments args) {
        List<SysRoute> list = sysRouteService.list();
        if (CollectionUtil.isNotEmpty(list)) {
            List<RouterStatusList> routers = new ArrayList<>();
            list.forEach(route -> {
                Boolean status = route.getStatus();
                status = status == null ? false : status;
                routers.add(new RouterStatusList(route.getServiceId(), status));
            });
            ruleCacheService.cacheRoutes(routers);
        }
    }
}

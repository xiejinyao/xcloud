package com.xjinyao.xcloud.admin.runner;

import com.xjinyao.xcloud.admin.service.ISysResourceService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

/**
 * @author 谢进伟
 * @description 资源缓存
 * @createDate 2021/2/25 15:36
 */
@Slf4j
@Component
@AllArgsConstructor
public class SysResourceCacheRunner implements ApplicationRunner {

    private ISysResourceService resourceService;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        resourceService.lambdaQuery();
    }
}

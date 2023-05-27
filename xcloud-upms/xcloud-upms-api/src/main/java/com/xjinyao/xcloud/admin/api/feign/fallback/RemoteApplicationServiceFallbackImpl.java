package com.xjinyao.xcloud.admin.api.feign.fallback;

import com.xjinyao.xcloud.admin.api.entity.SysApplication;
import com.xjinyao.xcloud.admin.api.entity.SysResource;
import com.xjinyao.xcloud.admin.api.feign.RemoteApplicationService;
import com.xjinyao.xcloud.common.core.util.R;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.util.Collections;
import java.util.List;

/**
 * @date 2019/2/1
 */
@Slf4j
public class RemoteApplicationServiceFallbackImpl implements RemoteApplicationService {

    @Setter
    private Throwable cause;

    @Override
    public R<List<SysResource>> getApplicationResources(String applicationCode, String from) {
        log.error("feign 查询应用资源信息失败:{}", applicationCode, cause);
        return R.failed(Collections.emptyList());
    }

    @Override
    public R<SysApplication> getByApplicationCode(String applicationCode, String from) {
        log.error("feign 查询应用信息失败:{}", applicationCode, cause);
        return R.failed(null);
    }

    @Override
    public R<Boolean> save(SysApplication application, String from) {
        log.error("feign 新增OpenApi应用失败:{}", application);
        return R.failed(Boolean.FALSE);
    }

    @Override
    public R<Boolean> update(SysApplication application, String from) {
        log.error("feign 更新OpenApi应用失败:{}", application);
        return R.failed(Boolean.FALSE);
    }

    @Override
    public R<Boolean> delete(String applicationCode, String from) {
        log.error("feign 删除OpenApi应用失败:{}", applicationCode);
        return R.failed(Boolean.FALSE);
    }
}

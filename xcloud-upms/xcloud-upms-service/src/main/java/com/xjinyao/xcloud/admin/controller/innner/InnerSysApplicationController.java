package com.xjinyao.xcloud.admin.controller.innner;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.xjinyao.xcloud.admin.api.constants.ControllerMapping;
import com.xjinyao.xcloud.admin.api.entity.SysApplication;
import com.xjinyao.xcloud.admin.api.entity.SysApplicationResource;
import com.xjinyao.xcloud.admin.api.entity.SysApplication_;
import com.xjinyao.xcloud.admin.api.entity.SysResource;
import com.xjinyao.xcloud.admin.service.ISysApplicationResourceService;
import com.xjinyao.xcloud.admin.service.ISysApplicationService;
import com.xjinyao.xcloud.admin.service.ISysResourceService;
import com.xjinyao.xcloud.common.core.redis.constant.CacheConstants;
import com.xjinyao.xcloud.common.core.util.R;
import com.xjinyao.xcloud.common.log.annotation.SysLog;
import com.xjinyao.xcloud.common.security.annotation.Inner;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 内部接口：应用管理
 *
 * @author 谢进伟
 * @createDate 2022/11/16 11:06
 */
@ApiIgnore
@RestController
@AllArgsConstructor
@RequestMapping(ControllerMapping.SYS_APPLICATION_CONTROLLER_MAPPING)
public class InnerSysApplicationController {

    private final ISysApplicationService applicationService;
    private final ISysApplicationResourceService applicationResourceService;
    private final ISysResourceService resourceService;


    @Inner
    @ApiOperation(value = "通过应用id查询应用的所有资源", notes = "通过应用id查询应用的所有资源", hidden = true)
    @GetMapping("/resources/{applicationCode}")
    @Cacheable(value = CacheConstants.APPLICATION_RESOURCES, key = "#applicationCode")
    public R<List<SysResource>> getResourcesForInner(@PathVariable("applicationCode") String applicationCode) {
        SysApplication application = this.applicationService.lambdaQuery()
                .select(SysApplication::getId, SysApplication::getAppId, SysApplication::getAppSecret)
                .eq(SysApplication::getCode, applicationCode)
                .oneOpt()
                .orElse(null);
        if (application != null) {
            List<SysApplicationResource> applicationResources = this.applicationResourceService.lambdaQuery()
                    .eq(SysApplicationResource::getApplicationId, application.getId())
                    .list();
            if (CollectionUtils.isNotEmpty(applicationResources)) {
                List<Long> resourceIdList = applicationResources.parallelStream()
                        .map(SysApplicationResource::getResourceId)
                        .filter(Objects::nonNull)
                        .collect(Collectors.toList());
                if (CollectionUtils.isNotEmpty(resourceIdList)) {
                    return R.ok(resourceService.lambdaQuery()
                                    .in(SysResource::getId, resourceIdList)
                                    .list())
                            .addExtendData(SysApplication_.appId.getProperty(), application.getAppId())
                            .addExtendData(SysApplication_.appSecret.getProperty(), application.getAppSecret());
                }
            }
        }
        final R<List<SysResource>> result = R.ok(Collections.emptyList());
        return result;
    }

    /**
     * 通过内部接口获取OpenApi应用信息
     *
     * @param applicationCode 应用编码
     * @return
     */
    @Inner
    @SysLog("通过内部接口获取OpenApi应用信息")
    @GetMapping("/get/{applicationCode}")
    @ApiOperation(value = "通过内部接口获取OpenApi应用信息", notes = "通过内部接口获取OpenApi应用信息", hidden = true)
    public R<SysApplication> getByApplicationCode(@PathVariable("applicationCode") String applicationCode) {
        return R.ok(applicationService.lambdaQuery()
                .eq(SysApplication::getCode, applicationCode)
                .oneOpt()
                .orElse(null));
    }

    /**
     * 通过内部接口新增OpenApi应用信息
     *
     * @param application 应用信息
     * @return
     */
    @Inner
    @PostMapping("/save")
    @SysLog("通过内部接口新增OpenApi应用信息")
    @ApiOperation(value = "通过内部接口新增OpenApi应用信息", notes = "通过内部接口新增OpenApi应用信息", hidden = true)
    public R<Boolean> saveForInner(@RequestBody SysApplication application) {
        return R.ok(this.applicationService.save(application));
    }

    /**
     * 通过内部接口新增OpenApi应用信息
     *
     * @param application 应用信息
     * @return
     */
    @Inner
    @PutMapping("/update")
    @SysLog("通过内部接口新增OpenApi应用信息")
    @ApiOperation(value = "通过内部接口新增OpenApi应用信息", notes = "通过内部接口新增OpenApi应用信息", hidden = true)
    public R<Boolean> updateForInner(@RequestBody SysApplication application) {
        return R.ok(this.applicationService.lambdaUpdate()
                .eq(SysApplication::getCode, application.getCode())
                .set(SysApplication::getName, application.getName())
                .set(SysApplication::getAppId, application.getAppId())
                .set(SysApplication::getAppSecret, application.getAppSecret())
                .set(SysApplication::getRemark, application.getRemark())
                .update());
    }

    /**
     * 通过内部接口新增OpenApi应用信息
     *
     * @param applicationCode 应用编码
     * @return
     */
    @Inner
    @DeleteMapping("/delete/{applicationCode}")
    @SysLog("通过内部接口新增OpenApi应用信息")
    @ApiOperation(value = "通过内部接口新增OpenApi应用信息", notes = "通过内部接口新增OpenApi应用信息", hidden = true)
    public R<Boolean> deleteForInner(@PathVariable("applicationCode") String applicationCode) {
        this.applicationService.remove(Wrappers.<SysApplication>lambdaQuery().eq(SysApplication::getCode, applicationCode));
        return R.ok(Boolean.TRUE);
    }
}

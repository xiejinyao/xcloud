package com.xjinyao.xcloud.admin.controller;

import com.xjinyao.xcloud.admin.api.entity.SysApplicationResource;
import com.xjinyao.xcloud.admin.service.ISysApplicationResourceService;
import com.xjinyao.xcloud.common.core.util.R;
import com.xjinyao.xcloud.common.log.annotation.SysLog;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;

/**
 * 应用资源管理
 *
 * @author 谢进伟
 * @date 2021-02-23 11:58:04
 */
@RestController
@AllArgsConstructor
@RequestMapping("/application/resource")
@Api(value = "application/resource", tags = "应用资源管理管理")
public class SysApplicationResourceController {

    private final ISysApplicationResourceService sysApplicationResourceService;

    /**
     * 新增应用资源管理
     *
     * @param applicationResource 应用资源管理
     * @return R
     */
    @ApiOperation(value = "新增应用资源管理", notes = "新增应用资源管理(权限标识:'admin_application_resource_add')")
    @SysLog("新增应用资源管理")
    @PostMapping
    @PreAuthorize("@pms.hasPermission('admin_application_resource_add')")
    public R<Boolean> save(@RequestBody SysApplicationResource applicationResource) {
        boolean result = sysApplicationResourceService.save(applicationResource);
        if (result) {
            sysApplicationResourceService.clearApplicationResourceCache(Collections.singletonList(applicationResource
                    .getApplicationId()));
        }
        return R.ok(result);
    }

    /**
     * 通过id删除应用资源管理
     *
     * @param id id
     * @return R
     */
    @ApiOperation(value = "通过id删除应用资源管理", notes = "通过id删除应用资源管理(权限标识:'admin_application_resource_del')")
    @SysLog("通过id删除应用资源管理")
    @DeleteMapping("/{id}")
    @PreAuthorize("@pms.hasPermission('admin_application_resource_del')")
    public R<Boolean> removeById(@PathVariable Long id) {
        SysApplicationResource applicationResource = sysApplicationResourceService.getById(id);
        if (applicationResource == null) {
            return R.failed("记录不存在，无法删除!");
        }
        boolean result = sysApplicationResourceService.removeById(id);
        if (result) {
            sysApplicationResourceService.clearApplicationResourceCache(Collections.singletonList(applicationResource
                    .getApplicationId()));
        }
        return R.ok(result);
    }

    /**
     * 批量新增应用资源管理
     *
     * @param sysApplicationResource 应用资源管理
     * @return R
     */
    @ApiOperation(value = "批量更新应用资源管理", notes = "批量更新应用资源管理(权限标识:'admin_application_resource_batch_update')")
    @SysLog("批量更新应用资源管理")
    @PostMapping("batchUpdate")
    @PreAuthorize("@pms.hasPermission('admin_application_resource_batch_update')")
    public R<Boolean> batchUpdate(@RequestBody List<SysApplicationResource> sysApplicationResource) {
        return R.ok(sysApplicationResourceService.batchSaveOrUpdate(sysApplicationResource));
    }
}

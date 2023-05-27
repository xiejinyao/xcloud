package com.xjinyao.xcloud.admin.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xjinyao.xcloud.admin.api.entity.SysApplication;
import com.xjinyao.xcloud.admin.api.entity.SysApplicationResource;
import com.xjinyao.xcloud.admin.api.entity.SysResource;
import com.xjinyao.xcloud.admin.api.entity.SysResource_;
import com.xjinyao.xcloud.admin.api.vo.SysApplicationResourceVO;
import com.xjinyao.xcloud.admin.service.ISysApplicationResourceService;
import com.xjinyao.xcloud.admin.service.ISysApplicationService;
import com.xjinyao.xcloud.admin.service.ISysResourceService;
import com.xjinyao.xcloud.common.core.redis.constant.CacheConstants;
import com.xjinyao.xcloud.common.core.util.R;
import com.xjinyao.xcloud.common.log.annotation.SysLog;
import com.xjinyao.xcloud.common.mybatis.wrappers.HightQueryWrapper;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.AllArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 应用管理
 *
 * @author 谢进伟
 * @date 2021-02-23 11:25:53
 */
@RestController
@AllArgsConstructor
@RequestMapping("/application")
@Api(value = "/application", tags = "应用管理管理")
public class SysApplicationController {

    private final ISysApplicationService applicationService;
    private final ISysApplicationResourceService applicationResourceService;
    private final ISysResourceService resourceService;

    /**
     * 分页查询
     *
     * @param page           分页对象
     * @param sysApplication 应用管理
     * @return
     */
    @ApiOperation(value = "分页查询", notes = "分页查询")
    @GetMapping("/page")
    public R<Page<SysApplication>> getSysApplicationPage(Page page, SysApplication sysApplication, HttpServletRequest request) {
        return R.ok(applicationService.page(page, HightQueryWrapper.wrapper(sysApplication, request.getParameterMap())));
    }


    /**
     * 通过id查询应用管理
     *
     * @param id id
     * @return R
     */
    @ApiOperation(value = "通过id查询", notes = "通过id查询")
    @GetMapping("/{id}")
    public R<SysApplication> getById(@PathVariable("id") Long id) {
        return R.ok(applicationService.getById(id));
    }

    /**
     * 通过应用id查询应用的所有资源
     *
     * @param applicationId 应用id
     * @return R
     */
    @ApiOperation(value = "通过应用id查询应用的所有资源", notes = "通过应用id查询应用的所有资源")
    @GetMapping("/resources/{applicationId}")
    public R<List<SysApplicationResourceVO>> getResources(@ApiParam("应用Id")
                                                          @PathVariable("applicationId") Long applicationId,
                                                          SysResource resource,
                                                          HttpServletRequest request) {
        List<SysApplicationResource> applicationResources = this.applicationResourceService.lambdaQuery()
                .eq(SysApplicationResource::getApplicationId, applicationId)
                .list();
        if (CollectionUtils.isNotEmpty(applicationResources)) {
            List<Long> resourceIdList = applicationResources.parallelStream()
                    .filter(ar -> ar.getResourceId() != null)
                    .map(SysApplicationResource::getResourceId)
                    .collect(Collectors.toList());
            if (CollectionUtils.isNotEmpty(resourceIdList)) {
                List<SysResource> list = resourceService.list(HightQueryWrapper.wrapper(resource, request.getParameterMap())
                        .in(SysResource_.id.getColumn(), resourceIdList));
                List<SysApplicationResourceVO> resourceVOList = new ArrayList<>();
                if (CollectionUtils.isNotEmpty(list)) {
                    Map<Long, List<SysResource>> sysResourceMap = list.parallelStream()
                            .collect(Collectors.groupingBy(SysResource::getId));
                    applicationResources.forEach(ar -> {
                        List<SysResource> sysResources = sysResourceMap.get(ar.getResourceId());
                        if (CollectionUtils.isNotEmpty(sysResources)) {
                            sysResources.forEach(sr -> resourceVOList.add(new SysApplicationResourceVO() {{
                                this.setId(ar.getId());
                                this.setResourceId(ar.getResourceId());
                                this.setApplicationId(ar.getApplicationId());
                                this.setType(sr.getType());
                                this.setCode(sr.getCode());
                                this.setName(sr.getName());
                                this.setRemark(sr.getRemark());
                            }}));
                        }
                    });
                }
                return R.ok(resourceVOList);
            }
        }
        return R.ok(Collections.emptyList());
    }

    /**
     * 新增应用管理
     *
     * @param sysApplication 应用管理
     * @return R
     */
    @ApiOperation(value = "新增应用管理", notes = "新增应用管理(权限标识:'admin_application_add')")
    @SysLog("新增应用管理")
    @PostMapping
    @PreAuthorize("@pms.hasPermission('admin_application_add')")
    public R<Boolean> save(@RequestBody SysApplication sysApplication) {
        return R.ok(applicationService.save(sysApplication));
    }

    /**
     * 修改应用管理
     *
     * @param sysApplication 应用管理
     * @return R
     */
    @ApiOperation(value = "修改应用管理", notes = "修改应用管理(权限标识:'admin_application_edit')")
    @SysLog("修改应用管理")
    @PutMapping
    @PreAuthorize("@pms.hasPermission('admin_application_edit')")
    @Cacheable(value = CacheConstants.APPLICATION_RESOURCES, key = "#sysApplication.code")
    public R<Boolean> updateById(@RequestBody SysApplication sysApplication) {
        SysApplication application = applicationService.getById(sysApplication.getId());
        boolean result = applicationService.updateById(sysApplication);
        if (result && application != null && !StringUtils.isNotBlank(application.getCode())) {
            applicationService.cacheEvictByApplicationCode(application.getCode());
        }
        return R.ok(result);
    }

    /**
     * 通过id删除应用管理
     *
     * @param id id
     * @return R
     */
    @ApiOperation(value = "通过id删除应用管理", notes = "通过id删除应用管理(权限标识:'admin_application_del')")
    @SysLog("通过id删除应用管理")
    @DeleteMapping("/{id}")
    @PreAuthorize("@pms.hasPermission('admin_application_del')")
    public R<Boolean> removeById(@PathVariable Long id) {
        final SysApplication application = applicationService.getById(id);
        if (application == null) {
            return R.failed(Boolean.FALSE, "记录未找到,无法进行删除操作!");
        }
        boolean result = applicationService.removeById(id);
        if (result && !StringUtils.isNotBlank(application.getCode())) {
            applicationService.cacheEvictByApplicationCode(application.getCode());
        }
        return R.ok(result);
    }
}

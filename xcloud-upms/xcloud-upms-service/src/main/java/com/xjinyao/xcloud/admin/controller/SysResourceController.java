package com.xjinyao.xcloud.admin.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xjinyao.xcloud.admin.api.entity.SysResource;
import com.xjinyao.xcloud.admin.service.ISysResourceService;
import com.xjinyao.xcloud.common.core.util.R;
import com.xjinyao.xcloud.common.log.annotation.SysLog;
import com.xjinyao.xcloud.common.mybatis.wrappers.HightQueryWrapper;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

/**
 * 资源管理
 *
 * @author 谢进伟
 * @date 2021-02-23 12:40:36
 */
@RestController
@AllArgsConstructor
@RequestMapping("/resource")
@Api(value = "resource", tags = "资源管理管理")
public class SysResourceController {

    private final ISysResourceService sysResourceService;

    /**
     * 分页查询
     *
     * @param page        分页对象
     * @param sysResource 资源管理
     * @return
     */
    @ApiOperation(value = "分页查询", notes = "分页查询")
    @GetMapping("/page")
    public R<Page<SysResource>> getSysResourcePage(Page page, SysResource sysResource, HttpServletRequest request) {
        return R.ok(sysResourceService.page(page, HightQueryWrapper.wrapper(sysResource, request.getParameterMap())));
    }


    /**
     * 通过id查询资源管理
     *
     * @param id id
     * @return R
     */
    @ApiOperation(value = "通过id查询", notes = "通过id查询")
    @GetMapping("/{id}")
    public R<SysResource> getById(@PathVariable("id") Long id) {
        return R.ok(sysResourceService.getById(id));
    }

    /**
     * 新增资源管理
     *
     * @param sysResource 资源管理
     * @return R
     */
    @ApiOperation(value = "新增资源管理", notes = "新增资源管理(权限标识:'admin_resource_add')")
    @SysLog("新增资源管理")
    @PostMapping
    @PreAuthorize("@pms.hasPermission('admin_resource_add')")
    public R<Boolean> save(@RequestBody SysResource sysResource) {
        return R.ok(sysResourceService.save(sysResource));
    }

    /**
     * 修改资源管理
     *
     * @param sysResource 资源管理
     * @return R
     */
    @ApiOperation(value = "修改资源管理", notes = "修改资源管理(权限标识:'admin_resource_edit')")
    @SysLog("修改资源管理")
    @PutMapping
    @PreAuthorize("@pms.hasPermission('admin_resource_edit')")
    public R<Boolean> updateById(@RequestBody SysResource sysResource) {
        return R.ok(sysResourceService.updateById(sysResource));
    }

    /**
     * 通过id删除资源管理
     *
     * @param id id
     * @return R
     */
    @ApiOperation(value = "通过id删除资源管理", notes = "通过id删除资源管理(权限标识:'admin_resource_del')")
    @SysLog("通过id删除资源管理")
    @DeleteMapping("/{id}")
    @PreAuthorize("@pms.hasPermission('admin_resource_del')")
    public R<Boolean> removeById(@PathVariable Long id) {
        return R.ok(sysResourceService.removeById(id));
    }

}

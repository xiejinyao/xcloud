package com.xjinyao.xcloud.admin.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xjinyao.xcloud.admin.api.entity.SysOauthClientDetails;
import com.xjinyao.xcloud.admin.service.SysOauthClientDetailsService;
import com.xjinyao.xcloud.common.core.util.R;
import com.xjinyao.xcloud.common.log.annotation.SysLog;
import com.xjinyao.xcloud.common.mybatis.wrappers.HightQueryWrapper;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

/**
 * <p>
 * 前端控制器
 * </p>
 *
 * @since 2018-05-15
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/client")
@Api(value = "client", tags = "客户端管理模块")
public class OauthClientDetailsController {

    private final SysOauthClientDetailsService sysOauthClientDetailsService;

    /**
     * 通过ID查询
     *
     * @param id ID
     * @return SysOauthClientDetails
     */
    @GetMapping("/{id}")
    @ApiOperation(value = "通过ID查询", notes = "通过ID查询")
    public R<SysOauthClientDetails> getById(@PathVariable Integer id) {
        return R.ok(sysOauthClientDetailsService.getById(id));
    }

    /**
     * 简单分页查询
     *
     * @param page                  分页对象
     * @param sysOauthClientDetails 系统终端
     * @return
     */
    @GetMapping("/page")
    @ApiOperation(value = "分页查询终端信息", notes = "分页查询终端信息")
    public R<Page<SysOauthClientDetails>> getOauthClientDetailsPage(Page page, SysOauthClientDetails sysOauthClientDetails, HttpServletRequest request) {
        return R.ok(sysOauthClientDetailsService.page(page, HightQueryWrapper.wrapper(sysOauthClientDetails, request.getParameterMap())));
    }

    /**
     * 添加
     *
     * @param sysOauthClientDetails 实体
     * @return success/false
     */
    @SysLog("添加终端")
    @PostMapping
    @PreAuthorize("@pms.hasPermission('sys_client_add')")
    @ApiOperation(value = "添加终端", notes = "添加终端")
    public R<Boolean> add(@Valid @RequestBody SysOauthClientDetails sysOauthClientDetails) {
        return R.ok(sysOauthClientDetailsService.save(sysOauthClientDetails));
    }

    /**
     * 删除
     *
     * @param id ID
     * @return success/false
     */
    @SysLog("删除终端")
    @DeleteMapping("/{id}")
    @PreAuthorize("@pms.hasPermission('sys_client_del')")
    @ApiOperation(value = "删除终端", notes = "删除终端")
    public R<Boolean> removeById(@PathVariable String id) {
        return R.ok(sysOauthClientDetailsService.removeClientDetailsById(id));
    }

    /**
     * 编辑
     *
     * @param sysOauthClientDetails 实体
     * @return success/false
     */
    @SysLog("编辑终端")
    @PutMapping
    @PreAuthorize("@pms.hasPermission('sys_client_edit')")
    @ApiOperation(value = "编辑终端", notes = "编辑终端")
    public R<Boolean> update(@Valid @RequestBody SysOauthClientDetails sysOauthClientDetails) {
        return R.ok(sysOauthClientDetailsService.updateClientDetailsById(sysOauthClientDetails));
    }

}

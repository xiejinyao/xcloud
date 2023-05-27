package com.xjinyao.xcloud.admin.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xjinyao.xcloud.admin.api.entity.SysLog;
import com.xjinyao.xcloud.admin.api.entity.SysLog_;
import com.xjinyao.xcloud.admin.service.SysLogService;
import com.xjinyao.xcloud.common.core.util.R;
import com.xjinyao.xcloud.common.mybatis.wrappers.HightQueryWrapper;
import com.xjinyao.xcloud.common.security.annotation.Inner;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

/**
 * <p>
 * 日志表 前端控制器
 * </p>
 *
 * @since 2019/2/1
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/log")
@Api(value = "log", tags = "日志管理模块")
public class LogController {

    private final SysLogService sysLogService;

    /**
     * 简单分页查询
     *
     * @param page   分页对象
     * @param sysLog 系统日志
     * @return
     */
    @GetMapping("/page")
    @ApiOperation(value = "分页查询日志信息", notes = "分页查询日志信息")
    public R<Page<SysLog>> getLogPage(Page page, SysLog sysLog, HttpServletRequest request) {
        return R.ok(sysLogService.page(page, HightQueryWrapper.wrapper(sysLog, request.getParameterMap()).orderByDesc(SysLog_.createTime.getColumn())));
    }

    /**
     * 删除日志
     *
     * @param id ID
     * @return success/false
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("@pms.hasPermission('sys_log_del')")
    @ApiOperation(value = "删除日志", notes = "删除日志")
    public R<Boolean> removeById(@PathVariable Long id) {
        return R.ok(sysLogService.removeById(id));
    }

    /**
     * 插入日志
     *
     * @param sysLog 日志实体
     * @return success/false
     */
    @Inner
    @PostMapping
    @ApiOperation(value = "插入日志", notes = "插入日志", hidden = true)
    public R<Boolean> save(@Valid @RequestBody SysLog sysLog) {
        return R.ok(sysLogService.save(sysLog));
    }

}

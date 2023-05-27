package com.xjinyao.xcloud.admin.controller;


import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xjinyao.xcloud.admin.api.entity.SysBlacklist;
import com.xjinyao.xcloud.admin.api.entity.SysBlacklist_;
import com.xjinyao.xcloud.admin.service.ISysBlacklistService;
import com.xjinyao.xcloud.common.core.util.R;
import com.xjinyao.xcloud.common.log.annotation.SysLog;
import com.xjinyao.xcloud.common.mybatis.wrappers.HightQueryWrapper;
import com.xjinyao.xcloud.core.rule.po.BlackList;
import com.xjinyao.xcloud.core.rule.service.IRuleCacheService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.Arrays;
import java.util.List;

/**
 * 系统黑名单表 前端控制器
 *
 * @author 谢进伟
 * @date 2020-11-10 10:38:03
 */
@RestController
@AllArgsConstructor
@RequestMapping("/blacklist")
@Api(tags = "黑名单管理")
public class SysBlacklistController {

    private final ISysBlacklistService sysBlacklistService;

    private final IRuleCacheService ruleCacheService;

    /**
     * 黑名单分页
     *
     * @param page 　分页参数
     * @return R
     */
    @SysLog("黑名单分页")
    @GetMapping("/page")
    @ApiOperation(value = "黑名单分页", notes = "黑名单分页")
    public R<Page<SysBlacklist>> page(Page page, SysBlacklist blacklist, HttpServletRequest request) {
        return R.ok(sysBlacklistService.page(page, HightQueryWrapper.wrapper(blacklist,
                request.getParameterMap()).orderByDesc(SysBlacklist_.createTime.getColumn())));
    }

    /**
     * 黑名单设置
     *
     * @param sysBlacklist SysBlacklist对象
     * @return R
     */
    @SysLog("黑名单设置")
    @PostMapping("/add")
    @ApiOperation(value = "黑名单设置", notes = "新增黑名单设置")
    @PreAuthorize("@pms.hasPermission('admin_sys_blacklist_add')")
    public R<String> add(@Valid @RequestBody SysBlacklist sysBlacklist) {
        return saveOrUpdate(sysBlacklist);
    }

    /**
     * 黑名单设置
     *
     * @param sysBlacklist SysBlacklist对象
     * @return R
     */
    @SysLog("黑名单设置")
    @PutMapping("/update")
    @ApiOperation(value = "黑名单设置", notes = "修改黑名单设置")
    @PreAuthorize("@pms.hasPermission('admin_sys_blacklist_edit')")
    public R<String> update(@Valid @RequestBody SysBlacklist sysBlacklist) {
        return saveOrUpdate(sysBlacklist);
    }

    /**
     * 黑名单信息
     *
     * @param id 　id
     * @return R
     */
    @SysLog("黑名单信息")
    @GetMapping("/getByIds")
    @ApiOperation(value = "黑名单信息", notes = "黑名单信息,根据ID查询")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", required = true, value = "主键ID", paramType = "form"),
    })
    public R<SysBlacklist> info(@RequestParam String id) {
        return R.ok(sysBlacklistService.getById(id));
    }

    /**
     * 黑名单删除
     *
     * @param ids 　多个id采用逗号分隔
     * @return R
     */
    @SysLog("黑名单删除")
    @DeleteMapping("/del")
    @ApiOperation(value = "黑名单删除", notes = "黑名单删除")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "ids", required = true, value = "多个用,号隔开", paramType = "form")
    })
    @PreAuthorize("@pms.hasPermission('admin_sys_blacklist_del')")
    @Transactional(rollbackFor = Exception.class)
    public R<String> del(@RequestParam String ids) {
        BlackList blackList = new BlackList();
        //处理缓存----start
        List<String> idList = Arrays.asList(StringUtils.split(ids, ","));
        for (Object id : idList) {
            if (id == null) {
                continue;
            }
            SysBlacklist blacklistCurr = sysBlacklistService.getById(Long.parseLong(id.toString()));
            BeanUtils.copyProperties(blacklistCurr, blackList);
            ruleCacheService.deleteBlackList(blackList);
        }
        //处理缓存----end
        if (sysBlacklistService.removeByIds(idList)) {
            return R.ok("删除成功");
        }
        return R.failed("删除失败");
    }

    /**
     * 黑名单状态
     *
     * @param ids    　多个id采用逗号分隔
     * @param status 　状态：启用、禁用
     * @return R
     */
    @SysLog("黑名单状态")
    @PutMapping("/set-status")
    @ApiOperation(value = "黑名单状态", notes = "黑名单状态,状态包括：启用、禁用")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "ids", required = true, value = "多个用,号隔开", paramType = "form"),
            @ApiImplicitParam(name = "status", required = true, value = "状态", paramType = "form")
    })
    @PreAuthorize("@pms.hasPermission('admin_sys_blacklist_update_status')")
    public R<String> setStatus(@RequestParam String ids, @RequestParam Boolean status) {
        if (sysBlacklistService.status(ids, status)) {
            return R.ok("批量修改成功");
        }
        return R.failed("操作失败");
    }

    private R<String> saveOrUpdate(@RequestBody @Valid SysBlacklist sysBlacklist) {
        BlackList blackList = new BlackList();
        //删除缓存
        if (sysBlacklist.getId() != null) {
            SysBlacklist b = sysBlacklistService.getById(sysBlacklist.getId());
            BeanUtils.copyProperties(b, blackList);
            ruleCacheService.deleteBlackList(blackList);
        }
        //删除缓存
        if (sysBlacklistService.saveOrUpdate(sysBlacklist)) {
            SysBlacklist blacklistCurr = sysBlacklistService.getById(sysBlacklist.getId());
            BeanUtils.copyProperties(blacklistCurr, blackList);
            ruleCacheService.cacheBlackList(blackList);
            return R.ok("操作成功");
        }
        return R.failed("操作失败");
    }
}


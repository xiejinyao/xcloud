package com.xjinyao.xcloud.admin.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xjinyao.xcloud.admin.api.entity.SysTasks;
import com.xjinyao.xcloud.admin.api.entity.SysTasks_;
import com.xjinyao.xcloud.admin.service.ISysTasksService;
import com.xjinyao.xcloud.common.core.util.R;
import com.xjinyao.xcloud.common.log.annotation.SysLog;
import com.xjinyao.xcloud.common.mybatis.wrappers.HightQueryWrapper;
import com.xjinyao.xcloud.common.security.service.CustomUser;
import com.xjinyao.xcloud.common.security.util.SecurityUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * 任务队列
 *
 * @author 谢进伟
 * @date 2021-03-18 11:06:30
 */
@Slf4j
@Api(value = "任务队列", tags = "任务队列")
@RestController
@RequestMapping("sysTasks")
@AllArgsConstructor
public class SysTasksController {

    private final ISysTasksService sysTasksService;

    /**
     * 分页查询
     *
     * @param page     分页对象
     * @param sysTasks 任务队列
     * @return
     */
    @ApiOperation(value = "分页查询", notes = "分页查询")
    @GetMapping("/page")
    public R<Page<SysTasks>> getSysTasksPage(Page page, SysTasks sysTasks, HttpServletRequest request) {
        QueryWrapper<SysTasks> queryWrapper = HightQueryWrapper.wrapper(sysTasks, request.getParameterMap());
        CustomUser user = SecurityUtils.getUser();
        log.info("user is {}", user);
        if (user != null) {
            queryWrapper.eq(SysTasks_.adminUserId.getColumn(), String.valueOf(user.getId()));
        }
        queryWrapper.orderByDesc(SysTasks_.id.getColumn());
        return R.ok(sysTasksService.page(page, HightQueryWrapper.wrapper(sysTasks, request.getParameterMap())));
    }


    /**
     * 通过id查询任务队列
     *
     * @param id id
     * @return R
     */
    @ApiOperation(value = "通过id查询", notes = "通过id查询")
    @GetMapping("/{id}")
    public R<SysTasks> getById(@PathVariable("id") Integer id) {
        return R.ok(sysTasksService.getById(id));
    }

    /**
     * 通过id删除任务队列
     *
     * @param id id
     * @return R
     */
    @ApiOperation(value = "通过id删除任务队列", notes = "通过id删除任务队列(权限标识:'admin_sys_tasks_del')")
    @SysLog("通过id删除任务队列")
    @DeleteMapping("/{id}")
    @PreAuthorize("@pms.hasPermission('admin_sys_tasks_del')")
    public R<Boolean> removeById(@PathVariable Integer id) {
        return R.ok(sysTasksService.removeById(id));
    }

    /**
     * 通过id批量删除任务队列
     *
     * @param idList id集合
     * @return
     */
    @ApiOperation(value = "批量删除任务队列", notes = "通过id批量删除除任务队列(权限标识:'admin_sys_tasks_batch_del')")
    @SysLog("批量删除任务队列")
    @DeleteMapping("/batch")
    public R<Boolean> batchDelete(@RequestBody List<Integer> idList) {
        if (CollectionUtils.isEmpty(idList)) {
            return R.failed(Boolean.FALSE, "参数错误!");
        }
        return R.ok(sysTasksService.removeByIds(idList));
    }
}


package com.xjinyao.xcloud.admin.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xjinyao.xcloud.admin.api.entity.SysSequence;
import com.xjinyao.xcloud.admin.service.ISysSequenceService;
import com.xjinyao.xcloud.common.core.util.R;
import com.xjinyao.xcloud.common.log.annotation.SysLog;
import com.xjinyao.xcloud.common.mybatis.wrappers.HightQueryWrapper;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * 系统序列表
 *
 * @author 刘元林
 * @date 2021-03-30 18:42:29
 */
@RestController
@AllArgsConstructor
@RequestMapping("/sequence")
@Api(value = "/sequence", tags = "系统序列表管理")
public class SysSequenceController {

    private final ISysSequenceService sysSequenceService;

    /**
     * 分页查询
     *
     * @param page        分页对象
     * @param sysSequence 系统序列表
     * @return
     */
    @ApiOperation(value = "分页查询", notes = "分页查询")
    @GetMapping("/page")
    public R<Page<SysSequence>> getSysSequencePage(Page page, SysSequence sysSequence, HttpServletRequest request) {
        return R.ok(sysSequenceService.page(page, HightQueryWrapper.wrapper(sysSequence, request.getParameterMap())));
    }


    /**
     * 通过id查询系统序列表
     *
     * @param id id
     * @return R
     */
    @ApiOperation(value = "通过id查询", notes = "通过id查询")
    @GetMapping("/{id}")
    public R<SysSequence> getById(@PathVariable("id") String id) {
        return R.ok(sysSequenceService.getById(id));
    }

    /**
     * 新增系统序列表
     *
     * @param sysSequence 系统序列表
     * @return R
     */
    @ApiOperation(value = "新增系统序列表", notes = "新增系统序列表(权限标识:'admin_sequence_add')")
    @SysLog("新增系统序列表")
    @PostMapping
    @PreAuthorize("@pms.hasPermission('admin_sequence_add')")
    public synchronized R<Boolean> save(@RequestBody SysSequence sysSequence) {
        return R.ok(sysSequenceService.save(sysSequence));
    }

    /**
     * 修改系统序列表
     *
     * @param sysSequence 系统序列表
     * @return R
     */
    @ApiOperation(value = "修改系统序列表", notes = "修改系统序列表(权限标识:'admin_sequence_edit')")
    @SysLog("修改系统序列表")
    @PutMapping
    @PreAuthorize("@pms.hasPermission('admin_sequence_edit')")
    public synchronized R<Boolean> updateById(@RequestBody SysSequence sysSequence) {
        return R.ok(sysSequenceService.updateById(sysSequence));
    }

    /**
     * 通过id删除系统序列表
     *
     * @param id id
     * @return R
     */
    @ApiOperation(value = "通过id删除系统序列表", notes = "通过id删除系统序列表(权限标识:'admin_sequence_del')")
    @SysLog("通过id删除系统序列表")
    @DeleteMapping("/{id}")
    @PreAuthorize("@pms.hasPermission('admin_sequence_del')")
    public synchronized R<Boolean> removeById(@PathVariable String id) {
        return R.ok(sysSequenceService.removeById(id));
    }

    /**
     * 通过id批量删除系统序列表
     *
     * @param idList id集合
     * @return
     */
    @ApiOperation(value = "批量删除系统序列表", notes = "通过id批量删除除系统序列表(权限标识:'admin_sequence_batch_del')")
    @SysLog("批量删除系统序列表")
    @DeleteMapping("/batch")
    public synchronized R<Boolean> batchDelete(@RequestBody List<Integer> idList) {
        if (CollectionUtils.isEmpty(idList)) {
            return R.failed(Boolean.FALSE, "参数错误!");
        }
        return R.ok(sysSequenceService.removeByIds(idList));
    }


}

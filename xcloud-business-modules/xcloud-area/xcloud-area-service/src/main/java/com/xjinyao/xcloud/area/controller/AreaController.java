package com.xjinyao.xcloud.area.controller;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.NumberUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xjinyao.xcloud.area.entity.Area;
import com.xjinyao.xcloud.area.entity.AreaLevel5;
import com.xjinyao.xcloud.area.enums.AreaLevelEnum;
import com.xjinyao.xcloud.area.service.AreaLevel5Service;
import com.xjinyao.xcloud.area.service.AreaService;
import com.xjinyao.xcloud.common.core.redis.constant.CacheConstants;
import com.xjinyao.xcloud.common.core.util.R;
import com.xjinyao.xcloud.common.log.annotation.SysLog;
import com.xjinyao.xcloud.common.mybatis.wrappers.HightQueryWrapper;
import com.xjinyao.xcloud.common.security.annotation.Inner;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.AllArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;


/**
 * 行政区域
 *
 * @author 谢进伟
 * @date 2020-05-05 11:35:14
 */
@RestController
@AllArgsConstructor
@RequestMapping("/")
@Api(value = "area", tags = "行政区域管理")
public class AreaController {

    private final AreaService areaService;
    private final AreaLevel5Service areaLevel5Service;

    /**
     * 根据级别获取行政区域数据
     *
     * @param level 级别
     * @return
     */
    @ApiOperation(value = "根据级别获取行政区域数据", notes = "根据级别获取行政区域数据")
    @GetMapping("/level/{level}")
    @Cacheable(value = CacheConstants.AREA_DETAILS, key = "'level' + #level", unless = "#level != null && #result.data.size() > 0")
    public R<List<? extends Area>> getAreaByLevel(@ApiParam(name = "level", value = "级别", required = true) @PathVariable Integer level) {
        if (AreaLevelEnum.VILLAGE.getValue().equals(level)) {
            return R.ok(areaLevel5Service.lambdaQuery().eq(AreaLevel5::getLevel, level).list());
        } else {
            return R.ok(areaService.lambdaQuery().eq(Area::getLevel, level).list());
        }
    }

    /**
     * 根据上级id获取行政区域数据
     *
     * @param parentId 上级id
     * @return
     */
    @ApiOperation(value = "根据上级id获取行政区域数据", notes = "根据上级id获取行政区域数据")
    @GetMapping("/parentId/{parentId}")
    @Cacheable(value = CacheConstants.AREA_DETAILS, key = "'parentId_' + #parentId", unless = "#parentId != null && #result.data.size() > 0")
    public R<List<? extends Area>> getAreaByParentId(@ApiParam(name = "parentId", value = "上级id") @PathVariable(required = false) String parentId) {
        if (StringUtils.isBlank(parentId)) {
            parentId = "0";
        }
        Area byId = areaService.getById(parentId);
        if (AreaLevelEnum.TOWN.getValue().equals(byId.getLevel())) {
            return R.ok(areaLevel5Service.lambdaQuery().eq(AreaLevel5::getParentId, parentId).list());
        } else {
            return R.ok(areaService.lambdaQuery().eq(Area::getParentId, parentId).list());
        }
    }

    /**
     * 分页查询
     *
     * @param page    分页对象
     * @param areaDTO 行政区域
     * @return
     */
    @ApiOperation(value = "分页查询", notes = "分页查询")
    @GetMapping("/page")
    public R<IPage<Area>> getAreaPage(Page page, AreaLevel5 areaDTO, HttpServletRequest request) {
        if (AreaLevelEnum.VILLAGE.getValue().equals(areaDTO.getLevel())) {
            return R.ok(areaLevel5Service.page(page, HightQueryWrapper.wrapper(areaDTO, request.getParameterMap())));
        } else {
            return R.ok(areaService.page(page, HightQueryWrapper.wrapper(areaDTO, request.getParameterMap())));
        }
    }

    /**
     * 通过id查询行政区域
     *
     * @param id id
     * @return R
     */
    @ApiOperation(value = "通过id查询", notes = "通过id查询")
    @GetMapping("/{id}")
    @Cacheable(value = CacheConstants.AREA_DETAILS, key = "'id_' + #id", unless = "#result != null")
    public Area getById(@PathVariable("id") String id) {
        Area area = areaService.getById(id);
        if (area == null) {
            area = areaLevel5Service.getById(id);
        }
        return area;
    }

    /**
     * 通过id查询行政区域
     *
     * @param id id
     * @return area
     */
    @Inner
    @ApiOperation(value = "通过id查询行政区域", notes = "通过id查询行政区域", hidden = true)
    @GetMapping("/inner/areaId")
    @Cacheable(value = CacheConstants.AREA_DETAILS, key = "'id_' + #id", unless = "#result != null")
    public Area getAreaById(String id) {
        return getById(id);
    }

    /**
     * 通过区域等级查询行政区域
     *
     * @param level id
     * @return area
     */
    @Inner
    @ApiOperation(value = "通过区域等级查询行政区域", notes = "通过区域等级查询行政区域", hidden = true)
    @GetMapping("/inner/level")
    public List<Area> getAreaLevelList(Integer level) {
        List<Area> areaList = areaService.lambdaQuery().eq(Area::getLevel, level).list();
        if (CollectionUtil.isEmpty(areaList)) {
            areaLevel5Service.lambdaQuery().eq(Area::getLevel, level).list();
        }
        return areaList;
    }

    /**
     * 新增行政区域
     *
     * @param area 行政区域
     * @return R
     */
    @ApiOperation(value = "新增行政区域", notes = "新增行政区域")
    @SysLog("新增行政区域")
    @PostMapping
    @PreAuthorize("@pms.hasPermission('area_add')")
    public R<Boolean> save(@RequestBody AreaLevel5 area) {
        if (!NumberUtil.isNumber(area.getCode())) {
            return R.failed("国家统计局编号必须是数字");
        }
        if (AreaLevelEnum.VILLAGE.getValue().equals(area.getLevel())) {
            return R.ok(areaLevel5Service.save(area));
        } else {
            return R.ok(areaService.save(area));
        }
    }

    /**
     * 修改行政区域
     *
     * @param area 行政区域
     * @return R
     */
    @ApiOperation(value = "修改行政区域", notes = "修改行政区域")
    @SysLog("修改行政区域")
    @PutMapping
    @PreAuthorize("@pms.hasPermission('area_edit')")
    @CacheEvict(value = CacheConstants.AREA_DETAILS, key = "'id_' + #area.id")
    public R<Boolean> updateById(@RequestBody AreaLevel5 area) {
        if (AreaLevelEnum.VILLAGE.getValue().equals(area.getLevel())) {
            return R.ok(areaLevel5Service.updateById(area));
        } else {
            return R.ok(areaService.updateById(area));
        }
    }

    /**
     * 通过id删除行政区域
     *
     * @param id id
     * @return R
     */
    @ApiOperation(value = "通过id删除行政区域", notes = "通过id删除行政区域")
    @SysLog("通过id删除行政区域")
    @DeleteMapping("/{id}")
    @PreAuthorize("@pms.hasPermission('area_del')")
    @CacheEvict(value = CacheConstants.AREA_DETAILS, key = "'id_' + #id")
    public R<Boolean> removeById(@PathVariable String id) {
        Area byId = areaService.getById(id);
        if (byId != null) {
            return R.ok(areaService.removeById(id));
        } else {
            return R.ok(areaLevel5Service.removeById(id));
        }
    }
}

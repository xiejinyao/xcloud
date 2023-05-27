package com.xjinyao.xcloud.admin.controller;

import cn.hutool.core.util.BooleanUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xjinyao.xcloud.admin.api.entity.SysApi;
import com.xjinyao.xcloud.admin.api.entity.SysApi_;
import com.xjinyao.xcloud.admin.service.ISysApiService;
import com.xjinyao.xcloud.common.core.redis.constant.CacheConstants;
import com.xjinyao.xcloud.common.core.redis.service.RedisService;
import com.xjinyao.xcloud.common.core.util.R;
import com.xjinyao.xcloud.common.log.annotation.SysLog;
import com.xjinyao.xcloud.common.mybatis.wrappers.HightQueryWrapper;
import com.xjinyao.xcloud.core.rule.po.ApiStatusList;
import com.xjinyao.xcloud.core.rule.service.IRuleCacheService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpMethod;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.*;
import java.util.stream.Collectors;

/**
 * API管理 前端控制器
 *
 * @author 谢进伟
 * @since 2020-11-09
 */
@RestController
@AllArgsConstructor
@RequestMapping("/api")
@Api(value = "API管理", tags = "API管理接口")
public class SysApiController {

    private final ISysApiService sysApiService;
    private final RedisService redisService;
    private final IRuleCacheService ruleCacheService;

    /**
     * 分页列表
     *
     * @param page   分页信息
     * @param sysApi 　搜索关键词
     * @return Result
     */
    @SysLog("API列表")
    @GetMapping("/page")
    @ApiOperation(value = "API列表", notes = "分页查询")
    public R<Page<SysApi>> page(Page page, SysApi sysApi, HttpServletRequest request) {
        return R.ok(sysApiService.page(page, HightQueryWrapper.wrapper(sysApi, request.getParameterMap())
                .orderByDesc(SysApi_.createTime.getColumn())));
    }

    /**
     * API管理信息
     *
     * @param id Id
     * @return Result
     */
    @SysLog(value = "API信息")
    @GetMapping("/get")
    @ApiOperation(value = "API信息", notes = "根据ID查询")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", required = true, value = "ID", paramType = "form"),
    })
    public R<SysApi> get(@RequestParam String id) {
        return R.ok(sysApiService.getById(id));
    }

    /**
     * API管理设置
     *
     * @param sysApi SysApi 对象
     * @return Result
     */
    @SysLog(value = "API设置")
    @PostMapping("/set")
    @ApiOperation(value = "API设置", notes = "API设置,支持新增或修改")
    public R<Boolean> set(@Valid @RequestBody SysApi sysApi) {
        SysApi existsApi = null;
        if (sysApi.getId() != null) {
            existsApi = sysApiService.getById(sysApi.getId());
        }
        if (sysApiService.saveOrUpdate(sysApi)) {
            if (existsApi != null) {
                ruleCacheService.deleteApiCache(existsApi.getServiceId(), HttpMethod.valueOf(existsApi.getMethod()),
                        existsApi.getPattern());
            }
            ruleCacheService.cacheApis(new ApiStatusList(sysApi.getServiceId(), HttpMethod.valueOf(existsApi.getMethod()),
                    sysApi.getPattern(), sysApi.getStatus()));
        }
        return R.ok(Boolean.TRUE);
    }

    /**
     * API管理删除
     *
     * @param ids id字符串，根据,号分隔
     * @return Result
     */
    @SysLog(value = "API删除")
    @DeleteMapping("/del")
    @ApiOperation(value = "API删除", notes = "API删除")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "ids", required = true, value = "多个用,号隔开", paramType = "form")
    })
    public R<Boolean> deleteApiCaches(@RequestParam String ids) {
        List<String> idList = Arrays.asList(StringUtils.split(ids, ","));
        boolean data = false;
        List<SysApi> apis = sysApiService.listByIds(idList);
        if (apis != null) {
            data = sysApiService.removeByIds(apis.stream().map(SysApi::getId).collect(Collectors.toList()));
            if (data) {
                deleteApiCaches(apis);
            }
        }
        return R.ok(data);
    }

    /**
     * API状态
     *
     * @param ids    多个Id，用,号分隔
     * @param status 状态：启用、禁用
     * @return Result
     */
    @SysLog(value = "API状态")
    @PutMapping("/set-status")
    @ApiOperation(value = "API状态", notes = "状态包括：启用、禁用")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "ids", required = true, value = "多个id用,号隔开", paramType = "form"),
            @ApiImplicitParam(name = "status", required = true, value = "状态", paramType = "form")
    })
    public R<Boolean> setStatus(@RequestParam String ids, @RequestParam Boolean status) {
        boolean result = sysApiService.status(ids, status);
        if (result) {
            List<String> idList = Arrays.asList(StringUtils.split(ids, ","));
            List<SysApi> sysApis = sysApiService.listByIds(idList);
            updateApiCacheStatus(sysApis);
        }
        return R.ok(result);
    }

    /**
     * 从redis同步api至数据库
     *
     * @return Boolean
     */
    @GetMapping("/sync")
    @ApiOperation(value = "API同步", notes = "API同步")
    @SysLog(value = "API同步")
    public R<Boolean> sync() {
        Set<Object> serviceIds = redisService.sGet(CacheConstants.XCLOUD_SERVICE_RESOURCE);
        if (!CollectionUtils.isEmpty(serviceIds)) {
            List<SysApi> allExistsApi = sysApiService.lambdaQuery()
                    .select(SysApi::getId, SysApi::getCode, SysApi::getStatus)
                    .list();
            Map<String, Long> allExistsApiCodeIdMap = new HashMap<>();
            Map<String, Boolean> allExistsApiCodeStatusMap = new HashMap<>();
            Optional.ofNullable(allExistsApi).ifPresent(list -> list.forEach(api -> {
                allExistsApiCodeIdMap.put(api.getCode(), api.getId());
                allExistsApiCodeStatusMap.put(api.getCode(), api.getStatus());
            }));
            List<SysApi> apiList = new ArrayList<>();
            for (Object service : serviceIds) {
                if (redisService.hHasKey(CacheConstants.XCLOUD_API_RESOURCE, service.toString())) {
                    Map<String, Object> apiMap = (Map<String, Object>) redisService.hget(
                            CacheConstants.XCLOUD_API_RESOURCE, service.toString());
                    List<Map<String, String>> list = (List<Map<String, String>>) apiMap.get("list");
                    list.forEach(item -> {
                        SysApi sysApi = new SysApi();
                        sysApi.setAuth(BooleanUtil.toBoolean(item.getOrDefault(SysApi_.auth.getProperty(),
                                Boolean.FALSE.toString())));
                        sysApi.setClassName(item.get(SysApi_.className.getProperty()));
                        sysApi.setCode(item.get(SysApi_.code.getProperty()));
                        sysApi.setContentType(item.get(SysApi_.contentType.getProperty()));
                        sysApi.setMethod(item.get(SysApi_.method.getProperty()));
                        sysApi.setMethodName(item.get(SysApi_.methodName.getProperty()));
                        sysApi.setName(item.get(SysApi_.name.getProperty()));
                        sysApi.setNotes(item.get(SysApi_.notes.getProperty()));
                        sysApi.setPath(item.get(SysApi_.path.getProperty()));
                        sysApi.setPattern(item.get(SysApi_.pattern.getProperty()));
                        sysApi.setServiceId(item.get(SysApi_.serviceId.getProperty()));
                        sysApi.setId(allExistsApiCodeIdMap.get(sysApi.getCode()));
                        sysApi.setStatus(Optional.ofNullable(allExistsApiCodeStatusMap.get(sysApi.getCode()))
                                .orElse(Boolean.TRUE));
                        apiList.add(sysApi);
                    });
                }
                redisService.hdel(CacheConstants.XCLOUD_API_RESOURCE, service.toString());
            }
            redisService.del(CacheConstants.XCLOUD_SERVICE_RESOURCE);
            sysApiService.saveOrUpdateBatch(apiList);
            updateApiCacheStatus(apiList);
        }
        return R.ok(Boolean.TRUE);
    }

    public void updateApiCacheStatus(List<SysApi> sysApis) {
        if (sysApis != null) {
            List<ApiStatusList> apis = new ArrayList<>();
            sysApis.forEach(api -> apis.add(new ApiStatusList(api.getServiceId(), HttpMethod.valueOf(api.getMethod()),
                    api.getPattern(), api.getStatus())));

            deleteApiCaches(sysApis);
            ruleCacheService.cacheApis(apis);
        }
    }

    private void deleteApiCaches(List<SysApi> apis) {
        apis.parallelStream()
                .collect(Collectors.groupingBy(SysApi::getServiceId, Collectors.groupingBy(SysApi::getMethod)))
                .forEach((serviceId, methodMap) -> methodMap.forEach((method, sysApis) -> {
                    List<String> patternsList = new ArrayList<>();
                    sysApis.forEach(api -> patternsList.add(api.getPattern()));
                    ruleCacheService.deleteApiCaches(serviceId, HttpMethod.valueOf(method), patternsList);
                }));
    }
}


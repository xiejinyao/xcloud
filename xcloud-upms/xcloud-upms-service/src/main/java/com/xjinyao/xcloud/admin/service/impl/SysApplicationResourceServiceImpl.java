package com.xjinyao.xcloud.admin.service.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xjinyao.xcloud.admin.api.entity.SysApplication;
import com.xjinyao.xcloud.admin.api.entity.SysApplicationResource;
import com.xjinyao.xcloud.admin.mapper.SysApplicationResourceMapper;
import com.xjinyao.xcloud.admin.service.ISysApplicationResourceService;
import com.xjinyao.xcloud.admin.service.ISysApplicationService;
import lombok.AllArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 应用资源管理
 *
 * @author 谢进伟
 * @date 2021-02-23 11:58:04
 */
@Service
@AllArgsConstructor
public class SysApplicationResourceServiceImpl extends ServiceImpl<SysApplicationResourceMapper, SysApplicationResource> implements ISysApplicationResourceService {

    private ISysApplicationService applicationService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean batchSaveOrUpdate(List<SysApplicationResource> sysApplicationResource) {
        if (CollectionUtils.isEmpty(sysApplicationResource)) {
            return false;
        }
        List<Integer> applicationIdList = new ArrayList<>();
        sysApplicationResource
                .parallelStream()
                .filter(sar -> sar.getApplicationId() != null)
                .collect(Collectors.groupingBy(SysApplicationResource::getApplicationId))
                .forEach((applicationId, resourcesList) -> {
                    applicationIdList.add(applicationId);
                    //更新的资源
                    List<Long> updateIdList = resourcesList
                            .parallelStream()
                            .filter(sar -> sar.getId() != null)
                            .map(SysApplicationResource::getId)
                            .collect(Collectors.toList());
                    //删除资源
                    if (CollectionUtils.isNotEmpty(updateIdList)) {
                        this.remove(Wrappers.<SysApplicationResource>lambdaQuery()
                                .eq(SysApplicationResource::getApplicationId, applicationId)
                                .notIn(SysApplicationResource::getId, updateIdList));
                    } else {
                        this.remove(Wrappers.<SysApplicationResource>lambdaQuery()
                                .eq(SysApplicationResource::getApplicationId, applicationId));
                    }
                    //新增的资源
                    List<SysApplicationResource> addList = resourcesList
                            .parallelStream()
                            .filter(sar -> sar.getId() == null)
                            .collect(Collectors.toList());
                    if (CollectionUtils.isNotEmpty(addList)) {
                        this.saveBatch(addList);
                    }
                });
        clearApplicationResourceCache(applicationIdList);
        return Boolean.TRUE;
    }

    @Override
    public void clearApplicationResourceCache(List<Integer> applicationIdList) {
        //清理缓存
        if (CollectionUtils.isNotEmpty(applicationIdList)) {
            List<SysApplication> list = applicationService.lambdaQuery()
                    .in(SysApplication::getId, applicationIdList)
                    .select(SysApplication::getCode)
                    .list();
            if (CollectionUtils.isNotEmpty(list)) {
                list.forEach(application -> applicationService.cacheEvictByApplicationCode(application.getCode()));
            }
        }
    }

}

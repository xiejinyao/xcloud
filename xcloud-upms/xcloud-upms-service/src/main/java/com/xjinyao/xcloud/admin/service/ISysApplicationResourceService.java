package com.xjinyao.xcloud.admin.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xjinyao.xcloud.admin.api.entity.SysApplicationResource;

import java.util.List;

/**
 * 应用资源管理
 *
 * @author 谢进伟
 * @date 2021-02-23 11:58:04
 */
public interface ISysApplicationResourceService extends IService<SysApplicationResource> {

    Boolean batchSaveOrUpdate(List<SysApplicationResource> sysApplicationResource);

    void clearApplicationResourceCache(List<Integer> applicationIdList);
}

package com.xjinyao.xcloud.admin.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xjinyao.xcloud.admin.api.entity.SysApplication;

/**
 * 应用管理
 *
 * @author 谢进伟
 * @date 2021-02-23 11:25:53
 */
public interface ISysApplicationService extends IService<SysApplication> {

    void cacheEvictByApplicationCode(String applicationCode);
}

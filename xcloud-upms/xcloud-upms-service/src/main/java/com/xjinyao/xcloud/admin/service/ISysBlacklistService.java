package com.xjinyao.xcloud.admin.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xjinyao.xcloud.admin.api.entity.SysBlacklist;

/**
 * <p>
 * 系统黑名单表 服务类
 * </p>
 *
 * @author 谢进伟
 * @since 2020-08-26
 */
public interface ISysBlacklistService extends IService<SysBlacklist> {

    boolean status(String ids, Boolean status);
}

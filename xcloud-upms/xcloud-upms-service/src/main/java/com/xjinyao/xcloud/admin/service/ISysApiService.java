package com.xjinyao.xcloud.admin.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xjinyao.xcloud.admin.api.entity.SysApi;

/**
 * <p>
 * 系统接口表 服务类
 * </p>
 *
 * @author 谢进伟
 * @since 2020-10-14
 */
public interface ISysApiService extends IService<SysApi> {

    /**
     * 根据code查询对象
     *
     * @param code 编码
     * @return SysApi
     */
    SysApi getByCode(String code);


    /**
     * 设置API状态
     *
     * @param ids    id串
     * @param status 　状态标识
     * @return Boolean
     */
    boolean status(String ids, Boolean status);
}

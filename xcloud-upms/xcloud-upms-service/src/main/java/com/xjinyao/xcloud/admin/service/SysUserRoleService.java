package com.xjinyao.xcloud.admin.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xjinyao.xcloud.admin.api.entity.SysUserRole;

/**
 * <p>
 * 用户角色表 服务类
 * </p>
 *
 * @since 2019/2/1
 */
public interface SysUserRoleService extends IService<SysUserRole> {

    /**
     * 根据用户Id删除该用户的角色关系
     *
     * @param userId 用户ID
     * @return boolean
     * @date 2017年12月7日 16:31:38
     */
    Boolean removeRoleByUserId(Integer userId);

}

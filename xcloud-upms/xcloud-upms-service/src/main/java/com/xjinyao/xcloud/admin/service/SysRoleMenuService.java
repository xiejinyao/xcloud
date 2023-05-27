package com.xjinyao.xcloud.admin.service;

import com.xjinyao.xcloud.admin.api.dto.RoleMenuDTO;
import com.xjinyao.xcloud.admin.api.entity.SysRoleMenu;
import com.xjinyao.xcloud.common.mybatis.service.IXService;

import java.util.List;

/**
 * <p>
 * 角色菜单表 服务类
 * </p>
 *
 * @since 2019/2/1
 */
public interface SysRoleMenuService extends IXService<SysRoleMenu> {

    /**
     * 更新角色菜单
     *
     * @param role
     * @param roleId  角色
     * @param menuIds 菜单ID拼成的字符串，每个id之间根据逗号分隔
     * @return
     */
    Boolean saveRoleMenus(String role, Long roleId, List<RoleMenuDTO.RoleMenuInfo> menuIds);

}

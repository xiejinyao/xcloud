package com.xjinyao.xcloud.admin.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.xjinyao.xcloud.admin.api.dto.RoleMenuDTO;
import com.xjinyao.xcloud.admin.api.entity.SysRole;
import com.xjinyao.xcloud.admin.api.entity.SysRoleMenu;
import com.xjinyao.xcloud.admin.mapper.SysRoleMenuMapper;
import com.xjinyao.xcloud.admin.service.SysRoleMenuService;
import com.xjinyao.xcloud.admin.service.SysRoleService;
import com.xjinyao.xcloud.common.core.redis.constant.CacheConstants;
import com.xjinyao.xcloud.common.mybatis.service.impl.XServiceImpl;
import lombok.RequiredArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

import static com.xjinyao.xcloud.common.core.util.StringUtils.SLASH_SEPARATOR;

/**
 * <p>
 * 角色菜单表 服务实现类
 * </p>
 *
 * @since 2019/2/1
 */
@Service
@RequiredArgsConstructor
public class SysRoleMenuServiceImpl extends XServiceImpl<SysRoleMenuMapper, SysRoleMenu> implements SysRoleMenuService {

	private final CacheManager cacheManager;

	private final SysRoleService roleService;

	/**
	 * @param role
	 * @param roleId  角色
	 * @param menuIds 菜单ID拼成的字符串，每个id之间根据逗号分隔
	 * @return
	 */
	@Override
	@Transactional(rollbackFor = Exception.class)
	@CacheEvict(value = CacheConstants.MENU_DETAILS, key = "#roleId + '_menu'")
	public Boolean saveRoleMenus(String role, Long roleId, List<RoleMenuDTO.RoleMenuInfo> menuIds) {
		boolean result = Boolean.TRUE;
		if (CollectionUtils.isEmpty(menuIds)) {
			return Boolean.TRUE;
		}

		Map<Long, Boolean> menuInfoMap = menuIds.stream()
				.collect(Collectors.toMap(RoleMenuDTO.RoleMenuInfo::getMenuId, RoleMenuDTO.RoleMenuInfo::getReAuth));
		Set<Long> menuIdList = menuInfoMap.keySet();
		if (CollectionUtil.isEmpty(menuIdList)) {
			result = this.remove(Wrappers.<SysRoleMenu>query().lambda().eq(SysRoleMenu::getRoleId, roleId));
		} else {
			List<SysRoleMenu> existsRoleMenuList = Optional.ofNullable(this.lambdaQuery()
							.eq(SysRoleMenu::getRoleId, roleId)
							.in(SysRoleMenu::getMenuId, menuIdList)
							.list())
					.orElse(Collections.emptyList());


			//已被删除的数据
			this.remove(Wrappers.<SysRoleMenu>query().lambda()
					.eq(SysRoleMenu::getRoleId, roleId)
					.notIn(SysRoleMenu::getMenuId, menuIdList));

			//删除角色的子角色数据
			List<Integer> childrenRoleIdList = Optional.ofNullable(roleService.lambdaQuery()
							.like(SysRole::getParentRoleIdPath, SLASH_SEPARATOR + roleId + SLASH_SEPARATOR)
							.list())
					.orElse(Collections.emptyList())
					.stream()
					.map(SysRole::getRoleId)
					.collect(Collectors.toList());
			if (CollectionUtils.isNotEmpty(childrenRoleIdList)) {
				this.remove(Wrappers.<SysRoleMenu>query().lambda()
						.in(SysRoleMenu::getRoleId, childrenRoleIdList)
						.notIn(SysRoleMenu::getMenuId, menuIdList));
			}

			List<Long> existsRoleMenuIdList = existsRoleMenuList
					.stream()
					.map(SysRoleMenu::getMenuId)
					.collect(Collectors.toList());

			//新增的数据
			List<SysRoleMenu> roleMenuList = menuIdList.stream()
					.filter(menuId -> !existsRoleMenuIdList.contains(menuId))
					.map(menuId -> new SysRoleMenu() {{
						this.setRoleId(roleId);
						this.setMenuId(menuId);
						this.setReAuth(menuInfoMap.get(menuId));
					}})
					.collect(Collectors.toList());
			if (CollectionUtil.isNotEmpty(roleMenuList)) {
				result = this.saveBatch(roleMenuList);
			}

			//更新已存在的数据（更新是否支持再授权字段）
			if (CollectionUtils.isNotEmpty(existsRoleMenuList)) {
				this.updateBatch(existsRoleMenuList.stream()
								.filter(d -> !Objects.equals(d.getReAuth(), menuInfoMap.get(d.getMenuId())))
								.peek(d -> d.setReAuth(menuInfoMap.get(d.getMenuId())))
								.collect(Collectors.toList()),
						entity -> Wrappers.<SysRoleMenu>lambdaUpdate()
								.eq(SysRoleMenu::getRoleId, entity.getRoleId())
								.eq(SysRoleMenu::getMenuId, entity.getMenuId()));
			}
		}

		//清空userinfo
		Cache cache = cacheManager.getCache(CacheConstants.USER_DETAILS);
		if (cache != null) {
			cache.invalidate();
		}

		return result;
	}

}

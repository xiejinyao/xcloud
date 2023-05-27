package com.xjinyao.xcloud.admin.service.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xjinyao.xcloud.admin.api.dto.RoleDTO;
import com.xjinyao.xcloud.admin.api.dto.RoleDataPermissionDimensionIdentifier;
import com.xjinyao.xcloud.admin.api.entity.SysRole;
import com.xjinyao.xcloud.admin.api.entity.SysRoleDataPermission;
import com.xjinyao.xcloud.admin.api.entity.SysRoleMenu;
import com.xjinyao.xcloud.admin.mapper.SysRoleMapper;
import com.xjinyao.xcloud.admin.mapper.SysRoleMenuMapper;
import com.xjinyao.xcloud.admin.service.ISysRoleDataPermissionService;
import com.xjinyao.xcloud.admin.service.SysRoleService;
import com.xjinyao.xcloud.common.core.redis.constant.CacheConstants;
import com.xjinyao.xcloud.common.core.tree.TreeUtil;
import com.xjinyao.xcloud.common.core.util.StringUtils;
import com.xjinyao.xcloud.common.security.util.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

import static com.xjinyao.xcloud.common.core.constant.CommonConstants.TREE_ROOT_ID_ZERO;
import static com.xjinyao.xcloud.common.core.util.StringUtils.SLASH_SEPARATOR;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @since 2019/2/1
 */
@Service
@RequiredArgsConstructor
public class SysRoleServiceImpl extends ServiceImpl<SysRoleMapper, SysRole> implements SysRoleService {

	private final SysRoleMenuMapper sysRoleMenuMapper;
	private final ISysRoleDataPermissionService sysRoleDataPermissionService;

	/**
	 * 通过用户ID，查询角色信息
	 *
	 * @param userId
	 * @return
	 */
	@Override
	public List findRolesByUserId(Integer userId) {
		return baseMapper.listRolesByUserId(userId);
	}

	/**
	 * 通过角色ID，删除角色,并清空角色菜单缓存
	 *
	 * @param id
	 * @return
	 */
	@Override
	@CacheEvict(value = CacheConstants.MENU_DETAILS, allEntries = true)
	@Transactional(rollbackFor = Exception.class)
	public Boolean removeRoleById(Integer id) {
		//子角色
		List<Integer> childrenIds = Optional.ofNullable(this.lambdaQuery()
						.select(SysRole::getRoleId)
						.like(SysRole::getParentRoleIdPath, SLASH_SEPARATOR + id + SLASH_SEPARATOR)
						.list())
				.orElse(Collections.emptyList())
				.stream()
				.map(SysRole::getRoleId)
				.collect(Collectors.toList());

		//子角色相关数据级联删除
		if (CollectionUtils.isNotEmpty(childrenIds)) {
			//删除所有子角色所分配的菜单
			sysRoleMenuMapper.delete(Wrappers.<SysRoleMenu>lambdaQuery()
					.in(SysRoleMenu::getRoleId, childrenIds));

			//删除子角色的所有数据权限
			sysRoleDataPermissionService.remove(Wrappers.<SysRoleDataPermission>lambdaQuery()
					.in(SysRoleDataPermission::getRoleId, childrenIds));

			//删除所有子角色
			this.removeByIds(childrenIds);
		}

		//删除角色所分配的菜单
		sysRoleMenuMapper.delete(Wrappers.<SysRoleMenu>lambdaQuery()
				.eq(SysRoleMenu::getRoleId, id));

		//删除角色所分配的数据权限
		sysRoleDataPermissionService.remove(Wrappers.<SysRoleDataPermission>lambdaQuery()
				.eq(SysRoleDataPermission::getRoleId, id));

		//删除自己
		return this.removeById(id);
	}

	/**
	 * 通过角色ID，批量删除角色
	 *
	 * @param ids
	 * @return
	 */
	@Override
	@Transactional(rollbackFor = Exception.class)
	public Boolean removeRoleByIds(List<Integer> ids) {
		if (CollectionUtils.isNotEmpty(ids)) {
			ids.forEach(this::removeById);
		}
		return true;
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public boolean save(RoleDTO sysRole) {
		sysRole.setCreateUserId(SecurityUtils.getUserId());
		Integer parentRoleId = sysRole.getParentRoleId();
		if (!Objects.equals(parentRoleId, TREE_ROOT_ID_ZERO.intValue())) {
			SysRole parentRole = this.getById(sysRole.getParentRoleId());
			if (parentRole == null) {
				return false;
			}
			sysRole.setParentRoleIdPath(parentRole.getParentRoleIdPath() + parentRole.getRoleId() + SLASH_SEPARATOR);
		} else {
			sysRole.setParentRoleIdPath(SLASH_SEPARATOR);
		}
		if (super.save(sysRole)) {
			return doSaveBatch(sysRole.getRoleId(), sysRole.getAddDimensionIdentifiers());
		}
		return false;
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public boolean updateById(RoleDTO sysRole) {
		if (super.updateById(sysRole)) {
			Optional.ofNullable(sysRole.getDeleteDimensionIdentifiers())
					.ifPresent(d -> d.stream()
							.filter(i -> StringUtils.isNotBlank(i.getDimensionValue()) &&
									CollectionUtils.isNotEmpty(i.getIdentifierValues()))
							.forEach(i -> sysRoleDataPermissionService.remove(Wrappers.<SysRoleDataPermission>lambdaQuery()
									.eq(SysRoleDataPermission::getDimension, i.getDimensionValue())
									.in(SysRoleDataPermission::getIdentifierValue, i.getIdentifierValues())
									.eq(SysRoleDataPermission::getRoleId, sysRole.getRoleId()))));

			return doSaveBatch(sysRole.getRoleId(), sysRole.getAddDimensionIdentifiers());
		}
		return false;
	}

	private boolean doSaveBatch(Integer roleId, List<RoleDataPermissionDimensionIdentifier> dimensionIdentifiers) {
		List<SysRoleDataPermission> dataPermissionList = new ArrayList<>();
		Optional.ofNullable(dimensionIdentifiers)
				.ifPresent(p -> p.forEach(d -> Optional.ofNullable(d.getIdentifierValues())
						.ifPresent(ivs -> ivs.forEach(iv -> dataPermissionList.add(new SysRoleDataPermission() {{
							this.setDimension(d.getDimension().getValue());
							this.setIdentifierValue(iv);
							this.setRoleId(roleId);
						}})))));
		Optional.of(dataPermissionList).ifPresent(sysRoleDataPermissionService::saveBatch);
		return true;
	}


	@Override
	public List<SysRole> buildTree(List<SysRole> list) {
		if (CollectionUtils.isEmpty(list)) {
			return Collections.emptyList();
		}

		Set<Integer> roleIdSet = list.stream()
				.map(SysRole::getRoleId)
				.collect(Collectors.toSet());

		List<SysRole> list1 = this.lambdaQuery()
				.and(q -> roleIdSet.forEach(d -> q.or()
						.like(SysRole::getParentRoleIdPath, SLASH_SEPARATOR + d + SLASH_SEPARATOR)))
				.list();

		List<SysRole> allData = new ArrayList<>();
		allData.addAll(list);
		allData.addAll(list1);

		return TreeUtil.buildTree(allData, d -> roleIdSet.contains(d.getRoleId()),
				(p, c) -> p.getRoleId().equals(c.getParentRoleId()),
				SysRole::getChildren,
				SysRole::setChildren);
	}

}

package com.xjinyao.xcloud.admin.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xjinyao.xcloud.admin.api.dto.OrganizationTreeNode;
import com.xjinyao.xcloud.admin.api.entity.SysOrganization;
import com.xjinyao.xcloud.common.core.redis.service.RedisService;
import com.xjinyao.xcloud.common.core.util.SpringContextHolder;

import java.util.List;
import java.util.Optional;

import static com.xjinyao.xcloud.common.core.redis.constant.CacheConstants.ANY;
import static com.xjinyao.xcloud.common.core.redis.constant.CacheConstants.ORGANIZATION_BY_PARENT_ID;

/**
 * <p>
 * 组织管理 服务类
 * </p>
 *
 * @since 2019/2/1
 */
public interface SysOrganizationService extends IService<SysOrganization> {

	/**
	 * 查询组织树菜单
	 *
	 * @param name 节点名称
	 * @return 树
	 */
	List<OrganizationTreeNode> listOrganizationTrees(String name);

	/**
	 * 通过父节点Id查询
	 *
	 * @param parentId                         父节点id
	 * @param excludeNotCanSelectOrganizations 是否排除不能选择的组织
	 * @param userId                           用户Id
	 * @return {@link List}<{@link OrganizationTreeNode}>
	 */
	List<OrganizationTreeNode> getOrganizationByParentId(Integer userId, String parentId, boolean excludeNotCanSelectOrganizations);

	/**
	 * 清除用户组织缓存
	 *
	 * @param userId 用户Id
	 */
	default void clearUserOrganizationCache(Integer userId) {
		RedisService redisService = SpringContextHolder.getBean(RedisService.class);
		Optional.ofNullable(redisService.keys(ORGANIZATION_BY_PARENT_ID
						+ ANY + userId + "_" + ANY))
				.ifPresent(keys -> redisService.del(keys.toArray(new String[0])));

	}

	/**
	 * 添加信息组织
	 *
	 * @param sysOrganization
	 * @return
	 */
	Boolean saveOrganization(SysOrganization sysOrganization);


	/**
	 * 删除组织通过id
	 *
	 * @param organization 组织
	 * @return 成功、失败
	 */
	Boolean removeOrganizationById(SysOrganization organization);

	/**
	 * 更新组织
	 *
	 * @param sysOrganization 组织信息
	 * @return 成功、失败
	 */
	Boolean updateOrganizationById(SysOrganization sysOrganization);

	/**
	 * 查询指定组织的所有上级id
	 *
	 * @param id 组织id
	 */
	List<String> getOrganizationParentIds(String id);

	/**
	 * 重置排序
	 *
	 * @param parentId 上级Id
	 */
	void resetSort(String parentId);

	/**
	 * 获取所有子节点id
	 *
	 * @param parentId 父id
	 * @return {@link List}<{@link String}>
	 */
	List<String> getChildrenIds(String parentId);

	/**
	 * 获取同一级别的节点Id
	 *
	 * @param id              id
	 * @param includeChildren 是否包含子节点
	 * @return {@link List}<{@link String}>
	 */
	List<String> getSiblingsChildrenIds(String id, Boolean includeChildren);
}

package com.xjinyao.xcloud.admin.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.extension.conditions.query.LambdaQueryChainWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xjinyao.xcloud.admin.api.dto.DataPermission;
import com.xjinyao.xcloud.admin.api.dto.OrganizationTreeNode;
import com.xjinyao.xcloud.admin.api.entity.SysOrganization;
import com.xjinyao.xcloud.admin.api.enums.RoleDataPermissionTypeEnum;
import com.xjinyao.xcloud.admin.mapper.SysOrganizationMapper;
import com.xjinyao.xcloud.admin.service.SysOrganizationService;
import com.xjinyao.xcloud.common.core.redis.constant.CacheConstants;
import com.xjinyao.xcloud.common.core.tree.TreeUtil;
import com.xjinyao.xcloud.common.core.util.NumberUtils;
import com.xjinyao.xcloud.common.core.util.StringUtils;
import com.xjinyao.xcloud.common.security.util.SecurityUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;

import static com.xjinyao.xcloud.common.core.constant.CommonConstants.TREE_ROOT_ID_ZERO;
import static com.xjinyao.xcloud.common.core.util.StringUtils.SLASH_SEPARATOR;

/**
 * 组织管理 服务实现类
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SysOrganizationServiceImpl extends ServiceImpl<SysOrganizationMapper, SysOrganization> implements SysOrganizationService {

	@Override
	@Transactional(rollbackFor = Exception.class)
	@CacheEvict(value = CacheConstants.ORGANIZATION_BY_PARENT_ID, allEntries = true)
	public Boolean saveOrganization(SysOrganization organization) {
		if (this.lambdaQuery().eq(SysOrganization::getCode, organization.getCode()).exists()) {
			return Boolean.FALSE;
		}
		this.save(organization);
		if (!NumberUtils.equals(TREE_ROOT_ID_ZERO, organization.getParentId())) {
			List<String> organizationParentIds = this.getOrganizationParentIds(organization.getId());
			organization.setParentIdPath(StringUtils.join(organizationParentIds, SLASH_SEPARATOR));
			this.updateById(organization);
		}
		return Boolean.TRUE;
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	@CacheEvict(value = CacheConstants.ORGANIZATION_BY_PARENT_ID, allEntries = true)
	public Boolean removeOrganizationById(SysOrganization organization) {
		if (organization != null) {
			List<SysOrganization> list = this.lambdaQuery()
					.eq(SysOrganization::getId, organization.getId())
					.or()
					.likeRight(SysOrganization::getParentIdPath, SLASH_SEPARATOR + organization.getParentIdPath()
							+ SLASH_SEPARATOR)
					.list();
			if (CollectionUtil.isNotEmpty(list)) {
				this.removeByIds(list);
			}
		}
		return Boolean.TRUE;
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	@CacheEvict(value = CacheConstants.ORGANIZATION_BY_PARENT_ID, allEntries = true)
	public Boolean updateOrganizationById(SysOrganization sysOrganization) {
		SysOrganization organization = this.getById(sysOrganization.getId());
		if (!organization.getParentId().equals(sysOrganization.getParentId())) {
			List<String> organizationParentIds = this.getOrganizationParentIds(sysOrganization.getId());
			sysOrganization.setParentIdPath(StringUtils.join(organizationParentIds, SLASH_SEPARATOR));
		}
		//更新组织状态
		this.updateById(sysOrganization);
		return Boolean.TRUE;
	}

	@Override
	public List<String> getOrganizationParentIds(String id) {
		List<String> idList = new ArrayList<>();
		this.getOrganizationParentId(id, idList);
		return CollectionUtil.reverse(idList);
	}

	@Override
	public List<OrganizationTreeNode> listOrganizationTrees(String name) {
		LambdaQueryChainWrapper<SysOrganization> queryChainWrapper = this.lambdaQuery();

		if (!SecurityUtils.isSuperAdmin()) {
			userAuthorityWrapper(queryChainWrapper);
		}

		List<SysOrganization> searchResults = queryChainWrapper.like(SysOrganization::getName, name).list();

		Set<String> parentIds = searchResults.stream()
				.map(d -> d.getParentIdPath().split(SLASH_SEPARATOR))
				.flatMap(Arrays::stream)
				.filter(StringUtils::isNotBlank)
				.collect(Collectors.toSet());

		TreeSet<String> ids = new TreeSet<>();
		ids.addAll(searchResults.stream().map(SysOrganization::getId).collect(Collectors.toList()));
		ids.addAll(parentIds);

		if (CollectionUtil.isNotEmpty(ids)) {
			return getOrganizationTree(this.lambdaQuery()
					.in(SysOrganization::getId, ids)
					.list());
		}
		return Collections.emptyList();
	}

	@Override
	@Cacheable(value = CacheConstants.ORGANIZATION_BY_PARENT_ID,
			key = "#userId + '_' + #parentId + '_' + #excludeNotCanSelectOrganizations",
			condition = "#result!=null && result.size()>0 && parentId!=0")
	public List<OrganizationTreeNode> getOrganizationByParentId(Integer userId, String parentId, boolean excludeNotCanSelectOrganizations) {
		LambdaQueryChainWrapper<SysOrganization> queryChainWrapper = this.lambdaQuery();
		Map<String, Boolean> noChildrenMap = new HashMap<>();
		if (!SecurityUtils.isSuperAdmin()) {
			SysOrganization userOrganization = this.lambdaQuery()
					.eq(SysOrganization::getId, SecurityUtils.getOrganizationId())
					.oneOpt()
					.orElse(null);
			if (userOrganization == null) {
				return Collections.emptyList();
			}
			String parentIdPath = userOrganization.getParentIdPath();
			String userOrganizationId = userOrganization.getId();
			String userOrganizationSiblingParentId = userOrganization.getParentId();
			String[] parentIdArray = Arrays.stream(parentIdPath.split(SLASH_SEPARATOR))
					.filter(StringUtils::isNotBlank)
					.collect(Collectors.toList())
					.toArray(new String[]{});
			if (Objects.equals(parentId, TREE_ROOT_ID_ZERO.toString())) {
				queryChainWrapper.eq(SysOrganization::getParentId, parentId);
				String rootParentId = UUID.randomUUID().toString();
				if (Objects.equals(userOrganizationSiblingParentId, TREE_ROOT_ID_ZERO.toString())) {
					rootParentId = userOrganizationId;
				} else {
					if (parentIdArray.length >= 2) {
						rootParentId = parentIdArray[0];
					} else {
						List<String> allParentIds = getOrganizationParentIds(userOrganizationId);
						if (CollectionUtil.isNotEmpty(allParentIds)) {
							rootParentId = allParentIds.get(0);
						}
					}
				}
				queryChainWrapper.eq(SysOrganization::getId, rootParentId);
			} else {
				//权限判断
				List<OrganizationTreeNode> emptyList = userAuthorityWrapper(parentId,
						queryChainWrapper,
						userOrganization,
						noChildrenMap::put);
				if (emptyList != null) return emptyList;
			}
		} else {
			queryChainWrapper.eq(SysOrganization::getParentId, parentId);
		}
		if (excludeNotCanSelectOrganizations) {
			queryChainWrapper.eq(SysOrganization::getIsCanSelect, true);
		}
		List<SysOrganization> list = queryChainWrapper.orderByAsc(SysOrganization::getSort).list();
		List<OrganizationTreeNode> OrganizationTrees = new ArrayList<>();
		if (CollectionUtil.isNotEmpty(list)) {
			List<String> organizationIdList = new ArrayList<>();
			list.forEach(sd -> organizationIdList.add(sd.getId()));
			HashMap<String, Boolean> organizationHasChildrenMap = new HashMap<>();
			Map<String, Map<String, Long>> maps = this.baseMapper.hasChildren(organizationIdList);
			maps.forEach((id, map) -> organizationHasChildrenMap.put(id, map.get("count") > 0));
			list.forEach(sd -> {
				OrganizationTreeNode organizationTree = getOrganizationTree(sd);
				String treeId = organizationTree.getId().toString();
				Boolean hasChildren = noChildrenMap.getOrDefault(treeId, organizationHasChildrenMap.get(treeId));
				organizationTree.setHasChildren(hasChildren != null && hasChildren);
				OrganizationTrees.add(organizationTree);
			});
		}
		return OrganizationTrees;
	}

	/**
	 * 用户权限包装
	 *
	 * @param queryChainWrapper 查询链包装
	 * @return {@link List}<{@link OrganizationTreeNode}>
	 */
	private void userAuthorityWrapper(LambdaQueryChainWrapper<SysOrganization> queryChainWrapper) {
		SysOrganization userOrganization = this.lambdaQuery()
				.eq(SysOrganization::getId, SecurityUtils.getOrganizationId())
				.oneOpt()
				.orElse(null);
		if (userOrganization == null) {
			queryChainWrapper.eq(SysOrganization::getId, UUID.randomUUID());
			return;
		}
		String parentIdPath = userOrganization.getParentIdPath();
		String userOrganizationId = userOrganization.getId();
		String userOrganizationSiblingParentId = userOrganization.getParentId();
		String[] parentIdArray = Arrays.stream(parentIdPath.split(SLASH_SEPARATOR))
				.filter(StringUtils::isNotBlank)
				.collect(Collectors.toList())
				.toArray(new String[]{});

		DataPermission userDataPermission = SecurityUtils.getUserDataPermission();
		List<String> parentIdList = Arrays.stream(parentIdArray)
				.filter(StringUtils::isNotBlank)
				.collect(Collectors.toList());
		if (CollectionUtils.isEmpty(parentIdList)) {
			parentIdList = getOrganizationParentIds(userOrganizationId);
		}
		if (Optional.ofNullable(parentIdList).orElse(Collections.emptyList()).isEmpty()) {
			log.error("疑似错误数据，组织ID：{}", userOrganizationId);
			queryChainWrapper.eq(SysOrganization::getId, UUID.randomUUID());
			return;
		}

		if (userDataPermission != null) {
			RoleDataPermissionTypeEnum type = userDataPermission.getType();
			if (type != null) {
				switch (type) {
					case SELF:
						queryChainWrapper.eq(SysOrganization::getId, userOrganizationId);
						break;
					case SELF_AND_CHILDREN:
						queryChainWrapper.like(SysOrganization::getParentIdPath,
								SLASH_SEPARATOR + userOrganizationId + SLASH_SEPARATOR);
						break;
					case SIBLINGS:
						queryChainWrapper.in(SysOrganization::getId, parentIdList);
						break;
					case SIBLINGS_AND_CHILDREN:
						queryChainWrapper.like(SysOrganization::getParentIdPath,
								SLASH_SEPARATOR + userOrganizationSiblingParentId + SLASH_SEPARATOR);
						break;
					case CUSTOM:
					case ALL:
					case NONE:
						//do nothing
						break;

				}
			}
		}
	}

	/**
	 * 用户权限包装
	 *
	 * @param parentId          父id
	 * @param queryChainWrapper 查询链包装
	 * @param userOrganization  用户组织
	 * @param consumer          消费者
	 * @return {@link List}<{@link OrganizationTreeNode}>
	 */
	private List<OrganizationTreeNode> userAuthorityWrapper(
			String parentId,
			LambdaQueryChainWrapper<SysOrganization> queryChainWrapper,
			SysOrganization userOrganization,
			BiConsumer<String, Boolean> consumer) {
		String parentIdPath = userOrganization.getParentIdPath();
		String userOrganizationId = userOrganization.getId();
		String userOrganizationSiblingParentId = userOrganization.getParentId();
		String[] parentIdArray = Arrays.stream(parentIdPath.split(SLASH_SEPARATOR))
				.filter(StringUtils::isNotBlank)
				.collect(Collectors.toList())
				.toArray(new String[]{});

		DataPermission userDataPermission = SecurityUtils.getUserDataPermission();
		List<String> parentIdList = Arrays.stream(parentIdArray)
				.filter(StringUtils::isNotBlank)
				.collect(Collectors.toList());
		if (CollectionUtils.isEmpty(parentIdList)) {
			parentIdList = getOrganizationParentIds(userOrganizationId);
		}
		if (Optional.ofNullable(parentIdList).orElse(Collections.emptyList()).isEmpty()) {
			log.error("疑似错误数据，组织ID：{}", userOrganizationId);
			return Collections.emptyList();
		}
		BiConsumer<String, Boolean> c = consumer == null ? (str, bool) -> {
		} : consumer;
		if (userDataPermission != null) {
			RoleDataPermissionTypeEnum type = userDataPermission.getType();
			if (parentId != null) {
				queryChainWrapper.eq(SysOrganization::getParentId, parentId);
			}
			if (type != null) {
				boolean grandpaParents = false;
				int index = ArrayUtils.indexOf(parentIdArray, userOrganizationSiblingParentId);
				if (index > -1) {
					grandpaParents = ArrayUtils.contains(ArrayUtils.remove(parentIdArray, index), parentId);
				}
				switch (type) {
					case SELF:
						if (grandpaParents) {
							if (!Objects.equals(parentId, userOrganizationId)) {
								queryChainWrapper.in(SysOrganization::getId, parentIdList);
							}
						}
						if (Objects.equals(parentId, userOrganizationSiblingParentId)) {
							queryChainWrapper.eq(SysOrganization::getId, userOrganizationId);
							c.accept(userOrganizationId, Boolean.FALSE);
						}
						if (Objects.equals(parentId, userOrganizationId)) {
							return Collections.emptyList();
						}
						break;
					case SELF_AND_CHILDREN:
						if (grandpaParents) {
							if (!Objects.equals(parentId, userOrganizationId)) {
								queryChainWrapper.in(SysOrganization::getId, parentIdList);
							}
						}
						if (Objects.equals(parentId, userOrganizationSiblingParentId)) {
							queryChainWrapper.eq(SysOrganization::getId, userOrganizationId);
						}
						break;
					case SIBLINGS:
						if (grandpaParents) {
							queryChainWrapper.in(SysOrganization::getId, parentIdList);
						}
						List<String> siblingsOrganizationIdList = Optional.ofNullable(this.lambdaQuery()
										.select(SysOrganization::getId)
										.eq(SysOrganization::getParentId, userOrganizationSiblingParentId)
										.list())
								.orElse(Collections.emptyList())
								.stream()
								.map(SysOrganization::getId)
								.collect(Collectors.toList());

						if (Objects.equals(parentId, userOrganizationSiblingParentId)) {
							siblingsOrganizationIdList.stream()
									.filter(d -> !d.equals(userOrganizationId))
									.forEach(_id -> c.accept(_id, Boolean.FALSE));
						}
						if (!Objects.equals(parentId, userOrganizationId)) {
							if (siblingsOrganizationIdList.contains(parentId)) {
								return Collections.emptyList();
							}
						}
						break;
					case SIBLINGS_AND_CHILDREN:
						if (grandpaParents) {
							queryChainWrapper.in(SysOrganization::getId, parentIdList);
						}
						break;
					case CUSTOM:
					case ALL:
						//do nothing
						break;
					case NONE:
						return Collections.emptyList();

				}
			}
		}
		return null;
	}

	@Override
	@CacheEvict(value = CacheConstants.ORGANIZATION_BY_PARENT_ID, allEntries = true)
	public void resetSort(String parentId) {
		List<SysOrganization> list = this.lambdaQuery()
				.select(SysOrganization::getId)
				.eq(SysOrganization::getParentId, parentId)
				.list();
		if (CollectionUtil.isNotEmpty(list)) {
			for (int i = 0; i < list.size(); i++) {
				list.get(i).setSort(i + 1);
			}
			this.updateBatchById(list);
			list.forEach(d -> resetSort(d.getId()));
		}
	}

	/**
	 * 获取所有子节点id
	 *
	 * @param parentId 父id
	 * @return {@link List}<{@link String}>
	 */
	@Override
	public List<String> getChildrenIds(String parentId) {
		return Optional.ofNullable(this.lambdaQuery()
						.select(SysOrganization::getId)
						.ne(SysOrganization::getId, parentId)
						.like(SysOrganization::getParentIdPath, SLASH_SEPARATOR + parentId + SLASH_SEPARATOR)
						.list())
				.orElse(Collections.emptyList())
				.stream()
				.map(SysOrganization::getId)
				.collect(Collectors.toList());
	}


	/**
	 * 获取同一级别的节点Id
	 *
	 * @param id              id
	 * @param includeChildren 是否包含子节点
	 * @return {@link List}<{@link String}>
	 */
	@Override
	public List<String> getSiblingsChildrenIds(String id, Boolean includeChildren) {
		SysOrganization organization = this.getById(id);
		if (organization == null) {
			return Collections.emptyList();
		}
		String parentId = organization.getParentId();
		LambdaQueryChainWrapper<SysOrganization> wrapper = this.lambdaQuery()
				.select(SysOrganization::getId);
		if (BooleanUtils.toBoolean(includeChildren)) {
			wrapper.and(q -> q.like(SysOrganization::getParentIdPath, SLASH_SEPARATOR + parentId + SLASH_SEPARATOR)
					.or()
					.eq(SysOrganization::getParentId, parentId));
		} else {
			wrapper.eq(SysOrganization::getParentId, parentId);
		}

		return Optional.ofNullable(wrapper.list())
				.orElse(Collections.emptyList())
				.stream()
				.map(SysOrganization::getId)
				.collect(Collectors.toList());
	}

	private void getOrganizationParentId(String id, List<String> idList) {
		SysOrganization organization = this.lambdaQuery().eq(SysOrganization::getId, id).oneOpt().orElse(null);
		if (organization != null) {
			idList.add(organization.getId());
			if (!NumberUtils.equals(TREE_ROOT_ID_ZERO, organization.getParentId())) {
				this.getOrganizationParentId(organization.getParentId(), idList);
			}
		}
	}

	private List<OrganizationTreeNode> getOrganizationTree(List<SysOrganization> organizations) {
		List<OrganizationTreeNode> treeList = organizations.stream()
				.filter(organization -> !organization.getId().equals(organization.getParentId()))
				.sorted(Comparator.comparingInt(SysOrganization::getSort))
				.map(this::getOrganizationTree)
				.collect(Collectors.toList());

		return TreeUtil.buildTree(treeList,
				d -> NumberUtils.equals(d.getParentId(), TREE_ROOT_ID_ZERO),
				(p, c) -> Objects.equals(p.getId(), c.getParentId()),
				OrganizationTreeNode::getChildren,
				OrganizationTreeNode::setChildren);
	}

	private OrganizationTreeNode getOrganizationTree(SysOrganization organization) {
		OrganizationTreeNode node = new OrganizationTreeNode();
		node.setId(organization.getId());
		node.setParentId(organization.getParentId());
		node.setName(organization.getName());
		node.setDetails(organization);
		return node;
	}

}

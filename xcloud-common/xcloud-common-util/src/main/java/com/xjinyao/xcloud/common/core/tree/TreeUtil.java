package com.xjinyao.xcloud.common.core.tree;

import com.xjinyao.xcloud.common.core.util.NumberUtils;
import lombok.Builder;
import lombok.experimental.UtilityClass;
import org.apache.commons.collections4.CollectionUtils;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 树工具
 *
 * @author 谢进伟
 * @createDate 2022/11/21
 */
@UtilityClass
public class TreeUtil {

	/**
	 * 构建树
	 *
	 * @param treeNodes    传入的树节点列表
	 * @param rootParentId 根节点的副节点Id
	 * @return {@link List}<{@link T}>
	 */
	public <T extends TreeNode<T>> List<T> buildTree(List<T> treeNodes,
													 Number rootParentId) {
		return buildTree(treeNodes, rootParentId, -1);
	}

	/**
	 * 构建树
	 *
	 * @param treeNodes    传入的树节点列表
	 * @param rootParentId 根节点的副节点Id
	 * @return {@link List}<{@link T}>
	 */
	public <T extends TreeNode<T>> Collection<T> buildTree(Collection<T> treeNodes,
														   Number rootParentId) {
		return buildTree(treeNodes, rootParentId, -1);
	}

	/**
	 * 构建树
	 *
	 * @param treeNodes    传入的树节点列表
	 * @param rootParentId 根节点的副节点Id
	 * @param maxLevel     最大深度
	 * @return {@link List}<{@link T}>
	 */
	public <T extends TreeNode<T>> List<T> buildTree(List<T> treeNodes,
													 Number rootParentId,
													 Integer maxLevel) {
		return new ArrayList<>(doBuildTree(treeNodes, rootParentId, maxLevel));
	}

	/**
	 * 构建树
	 *
	 * @param treeNodes    传入的树节点列表
	 * @param rootParentId 根节点的副节点Id
	 * @param maxLevel     最大深度
	 * @return {@link List}<{@link T}>
	 */
	public <T extends TreeNode<T>> Collection<T> buildTree(Collection<T> treeNodes,
														   Number rootParentId,
														   Integer maxLevel) {
		return doBuildTree(treeNodes, rootParentId, maxLevel);
	}

	/**
	 * 构建树
	 * 使用递归方法建树
	 *
	 * @param treeNodes      所有数据
	 * @param rootFun        判断是否是跟节点函数
	 * @param relationFun    上级与下级的关联函数 ,第一个参数是父节点对象，第二个参数为子节点对象
	 * @param getChildrenFun 获取子节点的函数，参数为节点对象
	 * @param setChildrenFun 设置子节点的函数，第一个参数为节点对象，第二个参数为子节点集合
	 * @return {@link List}<{@link T}>
	 */
	public <T> List<T> buildTree(List<T> treeNodes,
								 Function<T, Boolean> rootFun,
								 BiFunction<T, T, Boolean> relationFun,
								 Function<T, List<T>> getChildrenFun,
								 BiConsumer<T, List<T>> setChildrenFun) {
		return buildTree(treeNodes, rootFun, relationFun, getChildrenFun, setChildrenFun, -1);
	}

	/**
	 * 构建树
	 * 使用递归方法建树
	 *
	 * @param treeNodes      所有数据
	 * @param rootFun        判断是否是跟节点函数
	 * @param relationFun    上级与下级的关联函数 ,第一个参数是父节点对象，第二个参数为子节点对象
	 * @param getChildrenFun 获取子节点的函数，参数为节点对象
	 * @param setChildrenFun 设置子节点的函数，第一个参数为节点对象，第二个参数为子节点集合
	 * @return {@link Collection}<{@link T}>
	 */
	public <T> Collection<T> buildTree(Collection<T> treeNodes,
								 Function<T, Boolean> rootFun,
								 BiFunction<T, T, Boolean> relationFun,
								 Function<T, List<T>> getChildrenFun,
								 BiConsumer<T, List<T>> setChildrenFun) {
		return buildTree(treeNodes, rootFun, relationFun, getChildrenFun, setChildrenFun, -1);
	}


	/**
	 * 构建树
	 * 使用递归方法建树
	 *
	 * @param treeNodes      所有数据
	 * @param rootFun        判断是否是跟节点函数
	 * @param relationFun    上级与下级的关联函数 ,第一个参数是父节点对象，第二个参数为子节点对象
	 * @param getChildrenFun 获取子节点的函数，参数为节点对象
	 * @param setChildrenFun 设置子节点的函数，第一个参数为节点对象，第二个参数为子节点集合
	 * @param maxLevel       最大深度
	 * @return {@link List}<{@link T}>
	 */
	public <T> List<T> buildTree(List<T> treeNodes,
								 Function<T, Boolean> rootFun,
								 BiFunction<T, T, Boolean> relationFun,
								 Function<T, List<T>> getChildrenFun,
								 BiConsumer<T, List<T>> setChildrenFun,
								 Integer maxLevel) {
		return new ArrayList<>(doBuildTree(treeNodes, rootFun, relationFun, getChildrenFun, setChildrenFun, maxLevel));
	}

	/**
	 * 构建树
	 * 使用递归方法建树
	 *
	 * @param treeNodes      所有数据
	 * @param rootFun        判断是否是跟节点函数
	 * @param relationFun    上级与下级的关联函数 ,第一个参数是父节点对象，第二个参数为子节点对象
	 * @param getChildrenFun 获取子节点的函数，参数为节点对象
	 * @param setChildrenFun 设置子节点的函数，第一个参数为节点对象，第二个参数为子节点集合
	 * @param maxLevel       最大深度
	 * @return {@link Collection}<{@link T}>
	 */
	public <T> Collection<T> buildTree(Collection<T> treeNodes,
									   Function<T, Boolean> rootFun,
									   BiFunction<T, T, Boolean> relationFun,
									   Function<T, List<T>> getChildrenFun,
									   BiConsumer<T, List<T>> setChildrenFun,
									   Integer maxLevel) {
		return doBuildTree(treeNodes, rootFun, relationFun, getChildrenFun, setChildrenFun, maxLevel);

	}

	/**
	 * 构建孩子树
	 * 构建指定节点的子节点树
	 *
	 * @param parent         上级节点
	 * @param nodes          所有节点
	 * @param relationFun    上级与下级的关联函数 ,第一个参数是父节点对象，第二个参数为子节点对象
	 * @param getChildrenFun 获取子节点的函数，参数为节点对象
	 * @param setChildrenFun 设置子节点的函数，第一个参数为节点对象，第二个参数为子节点集合
	 * @return {@link T}
	 */
	public <T> T buildChildrenTree(T parent,
								   Collection<T> nodes,
								   BiFunction<T, T, Boolean> relationFun,
								   Function<T, List<T>> getChildrenFun,
								   BiConsumer<T, List<T>> setChildrenFun) {
		return buildChildrenTree(parent, nodes, relationFun, getChildrenFun, setChildrenFun,
				-1);
	}

	/**
	 * 构建孩子树
	 * 构建指定节点的子节点树
	 *
	 * @param parent         上级节点
	 * @param nodes          所有节点
	 * @param relationFun    上级与下级的关联函数 ,第一个参数是父节点对象，第二个参数为子节点对象
	 * @param getChildrenFun 获取子节点的函数，参数为节点对象
	 * @param setChildrenFun 设置子节点的函数，第一个参数为节点对象，第二个参数为子节点集合
	 * @param maxLevel       最大深度
	 * @return {@link T}
	 */
	public <T> T buildChildrenTree(T parent,
								   Collection<T> nodes,
								   BiFunction<T, T, Boolean> relationFun,
								   Function<T, List<T>> getChildrenFun,
								   BiConsumer<T, List<T>> setChildrenFun,
								   Integer maxLevel) {
		return doBuildChildrenTree(parent, nodes, relationFun, getChildrenFun, setChildrenFun,
				maxLevel, initParents(parent));
	}

	/**
	 * 构建树
	 * 使用递归方法建树
	 *
	 * @param treeNodes      所有数据
	 * @param rootFun        判断是否是跟节点函数
	 * @param relationFun    上级与下级的关联函数 ,第一个参数是父节点对象，第二个参数为子节点对象
	 * @param getChildrenFun 获取子节点的函数，参数为节点对象
	 * @param setChildrenFun 设置子节点的函数，第一个参数为节点对象，第二个参数为子节点集合
	 * @param maxLevel       最大深度
	 * @return {@link Collection}<{@link T}>
	 */
	private  <T> Collection<T> doBuildTree(Collection<T> treeNodes,
										   Function<T, Boolean> rootFun,
										   BiFunction<T, T, Boolean> relationFun,
										   Function<T, List<T>> getChildrenFun,
										   BiConsumer<T, List<T>> setChildrenFun,
										   Integer maxLevel) {

		if (CollectionUtils.isEmpty(treeNodes)) {
			return Collections.emptyList();
		}
		//根节点
		Collection<T> roots = treeNodes.stream()
				.filter(rootFun::apply)
				.collect(Collectors.toList());

		//非根所有节点
		Collection<T> notRootNodes = treeNodes.stream()
				.filter(d -> !rootFun.apply(d))
				.collect(Collectors.toList());

		roots.forEach(parent -> buildChildrenTree(parent, notRootNodes, relationFun, getChildrenFun, setChildrenFun,
				maxLevel));

		return roots;
	}

	/**
	 * 构建树
	 *
	 * @param treeNodes    传入的树节点列表
	 * @param rootParentId 根节点的副节点Id
	 * @param maxLevel     最大深度
	 * @return {@link List}<{@link T}>
	 */
	private <T extends TreeNode<T>> Collection<T> doBuildTree(Collection<T> treeNodes,
															  Number rootParentId,
															  Integer maxLevel) {
		List<T> roots = treeNodes.stream()
				.filter(d -> NumberUtils.equals(rootParentId, d.getParentId()))
				.collect(Collectors.toList());

		List<T> notRootNodes = treeNodes.stream()
				.filter(d -> !NumberUtils.equals(rootParentId, d.getParentId()))
				.collect(Collectors.toList());

		roots.forEach(parent -> buildChildrenTree(parent, notRootNodes,
				(p, c) -> NumberUtils.equals(p.getId(), c.getParentId()),
				TreeNode<T>::getChildren,
				TreeNode<T>::setChildren,
				maxLevel));

		return roots;
	}

	/**
	 * 建立孩子树
	 * 构建指定节点的子节点树,起始深度默认为0
	 *
	 * @param parent         上级节点
	 * @param nodes          所有节点
	 * @param relationFun    上级与下级的关联函数 ,第一个参数是父节点对象，第二个参数为子节点对象
	 * @param getChildrenFun 获取子节点的函数，参数为节点对象
	 * @param setChildrenFun 设置子节点的函数，第一个参数为节点对象，第二个参数为子节点集合
	 * @param maxLevel       最大深度
	 * @param parents        所有父节点集合
	 * @return {@link T}
	 */
	private <T> T doBuildChildrenTree(T parent,
									  Collection<T> nodes,
									  BiFunction<T, T, Boolean> relationFun,
									  Function<T, List<T>> getChildrenFun,
									  BiConsumer<T, List<T>> setChildrenFun,
									  Integer maxLevel,
									  Set<T> parents) {
		AtomicInteger nowLevel = new AtomicInteger(0);
		return doBuildChildrenTree(Node.<T>builder()
						.level(0)
						.node(parent)
						.build(), nodes, relationFun, getChildrenFun, setChildrenFun,
				nowLevel, maxLevel, parents);
	}

	/**
	 * 建立孩子树
	 * 构建指定节点的子节点树
	 *
	 * @param parentNode     上级节点
	 * @param nodes          所有节点
	 * @param relationFun    上级与下级的关联函数 ,第一个参数是父节点对象，第二个参数为子节点对象
	 * @param getChildrenFun 获取子节点的函数，参数为节点对象
	 * @param setChildrenFun 设置子节点的函数，第一个参数为节点对象，第二个参数为子节点集合
	 * @param nowLevel       当前深度
	 * @param maxLevel       最大深度
	 * @param parents        所有父节点集合
	 * @return {@link T}
	 */
	private <T> T doBuildChildrenTree(Node<T> parentNode,
									  Collection<T> nodes,
									  BiFunction<T, T, Boolean> relationFun,
									  Function<T, List<T>> getChildrenFun,
									  BiConsumer<T, List<T>> setChildrenFun,
									  AtomicInteger nowLevel,
									  Integer maxLevel,
									  Set<T> parents) {
		if (nowLevel == null) {
			nowLevel = new AtomicInteger(0);
		}
		T parent = parentNode.node;
		int parentLevel = parentNode.level;
		if (parentLevel <= maxLevel || maxLevel == -1) {
			AtomicInteger finalNowLevel = nowLevel;
			Optional.ofNullable(nodes).orElse(Collections.emptyList())
					.stream()
					.filter(d -> !parents.contains(d))
					.collect(Collectors.toList()).forEach(it -> {
						if (relationFun.apply(parent, it)) {
							List<T> childrenCollection = getChildrenFun.apply(parent);
							if (childrenCollection == null) {
								childrenCollection = new ArrayList<>();
								setChildrenFun.accept(parent, childrenCollection);
							}

							parents.add(it);

							Node<T> tNode = Node.<T>builder()
									.level(parentLevel + 1)
									.node(it)
									.build();

							childrenCollection.add(doBuildChildrenTree(tNode, nodes, relationFun, getChildrenFun,
									setChildrenFun, finalNowLevel, maxLevel, parents));
						}
					});
			nowLevel.set(0);
		}
		return parent;
	}

	/**
	 * 初始化父节点集合容器
	 *
	 * @param parent 父节点
	 * @return {@link HashSet}<{@link T}>
	 */
	private static <T> HashSet<T> initParents(T parent) {
		return new HashSet<>() {{
			add(parent);
		}};
	}


	@Builder
	public static class Node<T> {
		T node;
		int level;
	}
}

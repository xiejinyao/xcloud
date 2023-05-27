package com.xjinyao.xcloud.common.mybatis.service;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.metadata.OrderItem;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.xjinyao.xcloud.common.core.util.StringUtils;
import org.springframework.beans.BeanUtils;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author 谢进伟
 * @createDate 2022/9/3 00:19
 */
public interface IXService<T> extends IService<T> {

	/**
	 * 分页查询，支持条件筛选分页
	 *
	 * @param pageParam    分页参数对象
	 * @param queryWrapper 条件参数对象
	 * @return
	 */
	default <VO> Page<VO> page(Page<T> pageParam, Wrapper<T> queryWrapper, Class<VO> voClass) {
		Class<T> entityClass = getEntityClass();

		//排序处理
		Optional.ofNullable(pageParam.orders())
				.ifPresent(orderItems -> {
					List<OrderItem> newOrders = new ArrayList<>();

					//数据库中所有字段列表
					List<String> columnList = Arrays.stream(entityClass.getDeclaredFields())
							.filter(f -> f.isAnnotationPresent(TableField.class))
							.flatMap(f -> Stream.of(f.getAnnotation(TableField.class).value()))
							.collect(Collectors.toList());

					orderItems.forEach(orderItem -> {
						String column = orderItem.getColumn();
						//如果此处的column不再数据库字段列表中，则尝试假设column的值为实体类的字段名从而查找映射的字段名
						if (!columnList.contains(column)) {
							try {
								Field field = entityClass.getDeclaredField(column);
								TableField tableField = field.getAnnotation(TableField.class);
								if (tableField != null) {
									String value = tableField.value();
									if (StringUtils.isNotBlank(value)) {
										orderItem.setColumn(value);
									}
								}
								newOrders.add(orderItem);
							} catch (NoSuchFieldException e) {
								//忽略
							}
						} else {
							newOrders.add(orderItem);
						}
					});
					pageParam.setOrders(newOrders);
				});

		//实体类粉也数据
		Page<T> entityPage = this.page(pageParam, queryWrapper);

		//转换VO
		List<VO> voList = Optional.ofNullable(entityPage.getRecords())
				.orElse(Collections.emptyList())
				.stream()
				.map(e -> getVo(voClass, e))
				.collect(Collectors.toList());

		//封装结果
		Page<VO> targetVoPage = new Page<>();
		BeanUtils.copyProperties(entityPage, targetVoPage, "records");
		targetVoPage.setRecords(voList);
		return targetVoPage;
	}

	/**
	 * 实体类转换成VO
	 *
	 * @param voClass VO 类
	 * @param entity  实体类
	 * @param <VO>
	 * @return
	 */
	private <VO> VO getVo(Class<VO> voClass, T entity) {
		VO t;
		try {
			t = voClass.getDeclaredConstructor().newInstance();
			BeanUtils.copyProperties(entity, t);
		} catch (InstantiationException | IllegalAccessException | InvocationTargetException |
				 NoSuchMethodException ex) {
			throw new RuntimeException(ex);
		}
		return t;
	}

}

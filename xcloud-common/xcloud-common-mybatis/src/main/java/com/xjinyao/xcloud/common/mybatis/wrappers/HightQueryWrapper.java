package com.xjinyao.xcloud.common.mybatis.wrappers;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.xjinyao.xcloud.common.core.annotations.DefaultSearchMode;
import com.xjinyao.xcloud.common.core.util.FieldUtil;
import com.xjinyao.xcloud.common.mybatis.constants.ParamNameSuffix;
import com.xjinyao.xcloud.common.mybatis.constants.ValuePrefix;
import com.xjinyao.xcloud.common.mybatis.resolver.ModelArgumentResolver;
import com.xjinyao.xcloud.common.swagger.params.XRangeParam;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.springframework.data.annotation.Transient;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.*;
import java.util.stream.Collectors;

import static com.xjinyao.xcloud.common.core.annotations.DefaultSearchMode.SearchMode.EQUALS;
import static com.xjinyao.xcloud.common.core.annotations.DefaultSearchMode.SearchMode.LIKE;

/**
 * @description 高级查询包装器
 * @createDate 2020/5/8 10:24
 */
public class HightQueryWrapper<T> extends QueryWrapper<T> {

	/**
	 * 精确匹配的类型集合
	 * <p>
	 * 如果SearchDto的字段类型在该集合中，则会采用精确匹配模式进行查询，可以使用 {@link DefaultSearchMode.SearchMode} 注解更改此行为
	 */
	private static final List<Class<?>> exactMatchClasses = new ArrayList<>();

	static {
		exactMatchClasses.add(Boolean.class);
		exactMatchClasses.add(Integer.class);
		exactMatchClasses.add(Long.class);
		exactMatchClasses.add(Short.class);
		exactMatchClasses.add(boolean.class);
		exactMatchClasses.add(int.class);
		exactMatchClasses.add(long.class);
		exactMatchClasses.add(short.class);
	}

	private <S> HightQueryWrapper(Class<T> cls, S searchDTO, Map<String, String> requestParams) {
		super.setEntity(null);
		super.initNeed();
		List<String> entityFieldNameList = FieldUtils.getAllFieldsList(cls)
				.stream()
				.map(Field::getName)
				.collect(Collectors.toList());
		List<Field> fieldList = FieldUtils.getAllFieldsList(searchDTO.getClass());
		if (!fieldList.isEmpty()) {
			Set<String> paramNames = null;
			if (requestParams != null && !requestParams.isEmpty()) {
				paramNames = requestParams.keySet();
			}
			Set<String> finalParamNames = paramNames;
			fieldList.stream()
					.filter(field -> entityFieldNameList.contains(field.getName())
							&& !field.isEnumConstant()
							&& !Modifier.isFinal(field.getModifiers())
							&& !field.isAnnotationPresent(Transient.class))
					.forEach(field -> parse(searchDTO, requestParams, field, finalParamNames));
		}
	}

	private HightQueryWrapper(T entity, Map<String, String> requestParams) {
		super.setEntity(null);
		super.initNeed();
		if (entity != null) {
			List<Field> fieldList = FieldUtil.getFieldList(entity.getClass());
			if (fieldList != null && !fieldList.isEmpty()) {
				Set<String> paramNames = null;
				if (requestParams != null && !requestParams.isEmpty()) {
					paramNames = requestParams.keySet();
				}
				Set<String> finalParamNames = paramNames;
				fieldList.stream()
						.filter(field -> !field.isEnumConstant() && !Modifier.isFinal(field.getModifiers()))
						.forEach(field -> parse(entity, requestParams, field, finalParamNames));
				;
			}
		}
	}

	/**
	 * 构建一个高级查询包装器
	 *
	 * @param entity        实体类对象
	 * @param requestParams 请求参数集合
	 * @param <E>
	 * @return
	 */
	@Deprecated
	public static <E> HightQueryWrapper<E> wrapper(E entity, Map<String, String[]> requestParams) {
		Map<String, String> tempMap = new HashMap<>();
		if (requestParams != null && !requestParams.isEmpty()) {
			copyRequestParam(requestParams, tempMap);
		}
		return new HightQueryWrapper<>(entity, tempMap);
	}

	private static void copyRequestParam(Map<String, String[]> requestParams, Map<String, String> tempMap) {
		requestParams.forEach((k, v) -> {
			if (v.length == 1) {
				tempMap.put(k, v[0]);
			} else if (v.length == 2) {
				tempMap.put(k + ParamNameSuffix.BEGIN, v[0]);
				tempMap.put(k + ParamNameSuffix.END, v[1]);
			}
		});
	}

	/**
	 * 构建一个高级查询包装器
	 *
	 * @param cls          实体类对象类
	 * @param searchParams 实体类对应的搜索对象
	 * @param <T>          实体类
	 * @param <S>          搜索条件对象
	 * @return
	 */
	public static <T, S> HightQueryWrapper<T> build(Class<T> cls, S searchDTO, Map<String, String[]> searchParams) {
		Map<String, String> tempMap = new HashMap<>();
		if (CollectionUtils.isNotEmpty(searchParams)) {
			copyRequestParam(searchParams, tempMap);
		}
		return new HightQueryWrapper<>(cls, searchDTO, tempMap);
	}

	/**
	 * orIn
	 *
	 * @param column    数据库字段
	 * @param value     用指定分隔符分隔的值
	 * @param separator 分隔符
	 * @return
	 */
	public HightQueryWrapper<T> orIn(String column, String value, String separator) {
		if (StrUtil.isNotBlank(column) && StrUtil.isNotBlank(value)) {
			String[] separatorValueArray = StrUtil.split(value, separator);
			orLike(column, separator, separatorValueArray);
		}
		return this;
	}

	/**
	 * orIn 适用于单个个字段同时以or的形式，同时每一个字段的值都是以特定分隔符分割的情况
	 *
	 * @param orInColumn 数据库字段信息
	 * @return
	 */
	public HightQueryWrapper<T> orIn(OrInColumn orInColumn) {
		return orIn(orInColumn.getColumn(), orInColumn.getValue(), orInColumn.getSeparator());
	}

	/**
	 * orIn 适用于多个字段同时以or的形式，同时每一个字段的值都是以特定分隔符分割的情况
	 *
	 * @param orInColumnList 数据库字段信息
	 * @return
	 */
	public HightQueryWrapper<T> orIn(List<OrInColumn> orInColumnList) {
		if (CollectionUtil.isNotEmpty(orInColumnList)) {
			this.and(wr -> {
				QueryWrapper<T> or = wr.or();
				for (OrInColumn orInColumn : orInColumnList) {
					String column = orInColumn.getColumn();
					String value = orInColumn.getValue();
					String separator = orInColumn.getSeparator();
					if (StrUtil.isNotBlank(column) && StrUtil.isNotBlank(value)) {
						String[] separatorValueArray = StrUtil.split(value, separator);
						for (String queryStr : separatorValueArray) {
							or.or(
									w -> w.like(column, separator + queryStr + separator)
											.or().likeLeft(column, separator + queryStr)
											.or().likeRight(column, queryStr + separator)
											.or().eq(column, queryStr)
							);
						}
					}
				}
			});
		}
		return this;
	}

	private <S> void parse(S entity, Map<String, String> requestParams, Field field, Set<String> finalParamNames) {

		try {
			field.setAccessible(true);
			String fieldName = field.getName();//实体类字段名
			String columnName;
			TableId tableId = field.getAnnotation(TableId.class);
			TableField tableField = field.getAnnotation(TableField.class);
			DefaultSearchMode defaultSearchMode = field.getAnnotation(DefaultSearchMode.class);
			if (tableField != null) {
				if (!tableField.exist()) {
					return;
				}
				columnName = tableField.value();//数据库字段名
			} else {
				columnName = fieldName.replaceAll("([A-Z])", "_$1").toLowerCase();
			}
			if (tableId != null) {//主键字段直接精确匹配
				Object paramValue = requestParams.get(fieldName);
				if (paramValue != null && StrUtil.isNotBlank(paramValue.toString())) {
					this.eq(columnName, paramValue);
				}
				return;
			}

			//范围值判断
			if (field.getType().equals(XRangeParam.class)) {
				Object xRangeParam = field.get(entity);
				if (xRangeParam != null) {
					Object beginVal = ModelArgumentResolver.BEGIN_FIELD.get(xRangeParam);
					if (beginVal != null) {
						this.ge(columnName, beginVal);
					}
					Object endVal = ModelArgumentResolver.END_FIELD.get(xRangeParam);
					if (endVal != null) {
						this.le(columnName, endVal);
					}
					return;
				}
			}

			Object objValue = field.get(entity);
			boolean typeInExactMatchClasses = exactMatchClasses.contains(field.getType());
			if (finalParamNames != null) {
				// ORELIKE 解析,处理多个值用分隔符隔开的搜索，多个分割值之间或关系
				if (finalParamNames.contains(fieldName + ParamNameSuffix.ORELIKE)) {
					String paramValue = requestParams.get(fieldName + ParamNameSuffix.ORELIKE);
					if (StrUtil.isNotBlank(paramValue)) {
						String separator = getInSeparator(requestParams, fieldName);
						String[] separatorValueArray = StrUtil.split(paramValue, separator);
						if (separatorValueArray.length > 0) {
							orLike(columnName, separator, separatorValueArray);
						}
					}
				}
				// ANDELIKE 解析,处理多个值用分隔符隔开的搜索,多个分割值之间并且关系
				if (finalParamNames.contains(fieldName + ParamNameSuffix.ANDELIKE)) {
					String paramValue = requestParams.get(fieldName + ParamNameSuffix.ANDELIKE);
					if (StrUtil.isNotBlank(paramValue)) {
						String sep = getInSeparator(requestParams, fieldName);
						String[] separatorValueArray = StrUtil.split(paramValue, sep);
						if (separatorValueArray.length > 0) {
							this.and(wr -> {
								for (String queryStr : separatorValueArray) {
									wr.and(
											w -> w.like(columnName, sep + queryStr + sep)
													.or().likeLeft(columnName, sep + queryStr)
													.or().likeRight(columnName, queryStr + sep)
													.or().eq(columnName, queryStr)
									);
								}
							});
						}
					}
				}
				// IN 解析
				if (finalParamNames.contains(fieldName + ParamNameSuffix.IN)) {
					String paramValue = requestParams.get(fieldName + ParamNameSuffix.IN);
					if (StrUtil.isNotBlank(paramValue)) {
						String sep = getInSeparator(requestParams, fieldName);
						Object[] values = StrUtil.split(paramValue, sep);
						this.in(columnName, values);
					}
				}
				// NOT IN 解析
				if (finalParamNames.contains(fieldName + ParamNameSuffix.NOT_IN)) {
					String paramValue = requestParams.get(fieldName + ParamNameSuffix.NOT_IN);
					if (StrUtil.isNotBlank(paramValue)) {
						String sep = getInSeparator(requestParams, fieldName);
						Object[] values = StrUtil.split(paramValue, sep);
						this.notIn(columnName, values);
					}
				}
				//范围值解析(字段封装形式)
				if (finalParamNames.contains(fieldName + ParamNameSuffix.BEGIN)) {//起始值解析
					String paramValue = requestParams.get(fieldName + ParamNameSuffix.BEGIN);
					if (StrUtil.isNotBlank(paramValue)) {
						this.ge(columnName, paramValue);
					}
				}
				if (finalParamNames.contains(fieldName + ParamNameSuffix.END)) {//结束值解析
					String paramValue = requestParams.get(fieldName + ParamNameSuffix.END);
					if (StrUtil.isNotBlank(paramValue)) {
						this.le(columnName, paramValue);
					}
				}
				//范围值解析(后缀形式)
				if (finalParamNames.contains(fieldName + ParamNameSuffix.BEGIN_1)) {//起始值解析
					String paramValue = requestParams.get(fieldName + ParamNameSuffix.BEGIN_1);
					if (StrUtil.isNotBlank(paramValue)) {
						this.ge(columnName, paramValue);
					}
				}
				if (finalParamNames.contains(fieldName + ParamNameSuffix.END_1)) {//结束值解析
					String paramValue = requestParams.get(fieldName + ParamNameSuffix.END_1);
					if (StrUtil.isNotBlank(paramValue)) {
						this.le(columnName, paramValue);
					}
				}

				//特殊前缀解析
				Object paramValue = requestParams.get(fieldName);
				if (paramValue != null) {
					if (StrUtil.startWith(paramValue.toString(), ValuePrefix.IS_NULL)) {
						this.isNull(columnName);
					} else if (StrUtil.startWith(paramValue.toString(), ValuePrefix.NOT_NULL)) {
						this.isNotNull(columnName);
					} else if (StrUtil.startWith(paramValue.toString(), ValuePrefix.IS_EMPTY)) {
						this.eq(columnName, "");
					} else if (StrUtil.startWith(paramValue.toString(), ValuePrefix.NOT_EMPTY)) {
						this.ne(columnName, "");
					} else if (objValue != null && StrUtil.isNotBlank(objValue.toString())) {
						if (typeInExactMatchClasses) {
							defaultSearch(columnName, defaultSearchMode, objValue, true);
						} else if (StrUtil.startWith(paramValue.toString(), ValuePrefix.EQ)) {
							this.eq(columnName, objValue);
						} else if (StrUtil.startWith(paramValue.toString(), ValuePrefix.NE)) {
							this.ne(columnName, objValue);
						} else if (StrUtil.startWith(paramValue.toString(), ValuePrefix.GT)) {
							this.gt(columnName, objValue);
						} else if (StrUtil.startWith(paramValue.toString(), ValuePrefix.LT)) {
							this.lt(columnName, objValue);
						} else if (StrUtil.startWith(paramValue.toString(), ValuePrefix.GE)) {
							this.ge(columnName, objValue);
						} else if (StrUtil.startWith(paramValue.toString(), ValuePrefix.LE)) {
							this.le(columnName, objValue);
						} else {
							defaultSearch(columnName, defaultSearchMode, objValue, false);
						}
					}
				} else if (objValue != null) {
					defaultSearch(columnName, defaultSearchMode, objValue, typeInExactMatchClasses);
				}
			} else if (objValue != null) {
				defaultSearch(columnName, defaultSearchMode, objValue, typeInExactMatchClasses);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 或者像
	 *
	 * @param columnName          列名
	 * @param separator           分隔符
	 * @param separatorValueArray 分隔符值数组
	 */
	private void orLike(String columnName, String separator, String[] separatorValueArray) {
		this.and(wr -> {
			QueryWrapper<T> or = wr.or();
			for (String queryStr : separatorValueArray) {
				or.or(
						w -> w.like(columnName, separator + queryStr + separator)
								.or().likeLeft(columnName, separator + queryStr)
								.or().likeRight(columnName, queryStr + separator)
								.or().eq(columnName, queryStr)
				);
			}
		});
	}

	/**
	 * 默认搜索
	 *
	 * @param columnName                       列名
	 * @param defaultSearchMode                默认搜索模式
	 * @param objValue                         obj对象
	 * @param ifSearchModeIsNullThatExactMatch 如果搜索模式未设置,精确匹配搜索模式
	 */
	private void defaultSearch(String columnName,
							   DefaultSearchMode defaultSearchMode,
							   Object objValue,
							   boolean ifSearchModeIsNullThatExactMatch) {
		if (defaultSearchMode == null) {
			if (ifSearchModeIsNullThatExactMatch) {
				this.eq(columnName, objValue);
			} else {
				this.like(columnName, objValue);
			}
		} else {
			DefaultSearchMode.SearchMode searchMode = defaultSearchMode.value();
			if (searchMode.equals(LIKE)) {
				this.like(columnName, objValue);
			} else if (searchMode.equals(EQUALS)) {
				this.eq(columnName, objValue);
			}
		}
	}

	private String getInSeparator(Map<String, String> requestParams, String fieldName) {
		String sep = requestParams.get(fieldName + ParamNameSuffix.IN_SEPARATOR);
		if (StrUtil.isBlank(sep)) {
			sep = ParamNameSuffix.DEFAULT_IN_SEPARATOR;
		}
		return sep;
	}
}

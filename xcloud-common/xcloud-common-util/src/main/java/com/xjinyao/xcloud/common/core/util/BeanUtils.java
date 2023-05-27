package com.xjinyao.xcloud.common.core.util;

import cn.hutool.core.util.ObjectUtil;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.Tolerate;
import lombok.experimental.UtilityClass;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeansException;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 扩展 {@link org.springframework.beans.BeanUtils }
 *
 * @author 谢进伟
 * @createDate 2022/9/3 01:05
 */
@UtilityClass
public class BeanUtils extends org.springframework.beans.BeanUtils {

    /**
     * Copy the property values of the given source bean into the given target bean,
     * ignoring the given "ignoreProperties".
     * <p>Note: The source and target classes do not have to match or even be derived
     * from each other, as long as the properties match. Any bean properties that the
     * source bean exposes but the target bean does not will silently be ignored.
     * <p>This is just a convenience method. For more complex transfer needs,
     * consider using a full {@link BeanWrapper}.
     * <p>As of Spring Framework 5.3, this method honors generic type information
     * when matching properties in the source and target objects. See the
     * documentation for {@link #copyProperties(Object, Object)} for details.
     *
     * @param source           the source bean
     * @param target           the target bean
     * @param ignoreProperties array of property names to ignore
     * @return target
     * @throws BeansException if the copying failed
     * @see BeanWrapper
     */
    public <S, T> T copyPropertiesAndGetTarget(S source, T target, String... ignoreProperties) {
        if (source == null) {
            return target;
        }
        copyProperties(source, target, ignoreProperties);
        return target;
    }


    /**
     * 将一个集合转换成指定泛型的集合
     *
     * @param sources 带转换的集合
     * @param cls     目标集合泛型
     * @param <T>     目标泛型
     * @return
     */
    public <T> List<T> transform(Collection<?> sources, Class<T> cls) {
        return transform(sources, cls, new String[]{});
    }

    /**
     * 变换
     * 将一个集合转换成指定泛型的集合
     *
     * @param sources          带转换的集合
     * @param cls              目标集合泛型
     * @param ignoreProperties 忽略属性
     * @return {@link List}<{@link T}>
     */
    public <T> List<T> transform(Collection<?> sources, Class<T> cls, String... ignoreProperties) {
        return Optional.ofNullable(sources)
                .orElse(Collections.emptyList())
                .stream()
                .map(d -> getT(cls, d, ignoreProperties))
                .collect(Collectors.toList());
    }

    /**
     * 对比两个对象中相同字段名的值变化情况
     *
     * @param sourceList                源对象
     * @param targetList                目标对象
     * @param matchFun                  匹配方法
     * @param targetFieldDescriptionFun 获取字段描述的方法
     * @param ignoreSourceFieldNames    忽略字段名
     * @param keyFun                    对象唯一标识字段，id、code之类的字段
     * @return {@link Map}<{@link K}, {@link List}<{@link ContrastInfo}>>
     */
    public <S, T, K> Map<K, List<ContrastInfo>> contrast(S sourceList,
                                                         T targetList,
                                                         Function<S, K> keyFun,
                                                         BiFunction<S, T, Boolean> matchFun,
                                                         Function<Field, String> targetFieldDescriptionFun,
                                                         String... ignoreSourceFieldNames) {

        return contrast(Collections.singletonList(sourceList),
                Collections.singletonList(targetList),
                keyFun,
                matchFun,
                targetFieldDescriptionFun,
                ignoreSourceFieldNames);
    }

    /**
     * 对比两个集合中的对象变化情况
     *
     * @param sourceList                源集合
     * @param targetList                目标集合
     * @param matchFun                  对象匹配函数
     * @param targetFieldDescriptionFun 字段中文描述方法
     * @param ignoreSourceFieldNames    忽略字段名
     * @param keyFun                    对象唯一标识字段，id、code之类的字段
     * @return {@link Map}<{@link K}, {@link List}<{@link ContrastInfo}>>
     */
    public <S, T, K> Map<K, List<ContrastInfo>> contrast(List<S> sourceList,
                                                         List<T> targetList,
                                                         Function<S, K> keyFun,
                                                         BiFunction<S, T, Boolean> matchFun,
                                                         Function<Field, String> targetFieldDescriptionFun,
                                                         String... ignoreSourceFieldNames) {
        Map<K, List<ContrastInfo>> result = new HashMap<>();

        sourceList.forEach(sourceObj -> {
            List<Field> sourceFields = FieldUtil.getFieldList(sourceObj.getClass());
            targetList.stream()
                    .filter(tagObj -> matchFun.apply(sourceObj, tagObj))
                    .findFirst()
                    .ifPresent(targetObj -> {
                        Class<?> targetObjCls = targetObj.getClass();
                        List<ContrastInfo> contrastInfos = new ArrayList<>();
                        sourceFields.stream()
                                .filter(field -> Arrays.stream(ignoreSourceFieldNames)
                                        .noneMatch(d -> Objects.equals(d, field.getName())))
                                .forEach(sourceField -> Optional.ofNullable(FieldUtils.getDeclaredField(targetObjCls,
                                                sourceField.getName(), true))
                                        .ifPresent(targetField ->
                                                match(contrastInfos, sourceObj, targetObj, sourceField, targetField,
                                                        targetFieldDescriptionFun)));
                        if (CollectionUtils.isNotEmpty(contrastInfos)) {
                            result.put(keyFun.apply(sourceObj), contrastInfos);
                        }
                    });
        });
        return result;
    }

    /**
     * 格式对比信息
     *
     * @param contrastInfoList 对比信息列表
     * @param formatter        格式化程序
     * @return {@link List}<{@link String}>
     */
    public static List<String> formatContrastInfo(List<ContrastInfo> contrastInfoList, Function<ContrastInfo, String> formatter) {
        return Optional.ofNullable(contrastInfoList).stream()
                .map(list -> list.stream()
                        .map(formatter)
                        .collect(Collectors.toList()))
                .flatMap(Collection::stream)
                .collect(Collectors.toList());
    }

    /**
     * 匹配
     *
     * @param contrastInfos             对比信息
     * @param sourceObj                 源obj
     * @param targetObj                 目标obj
     * @param sourceField               源领域
     * @param targetField               目标字段
     * @param targetFieldDescriptionFun 目标字段描述有趣
     */
    private static <S, T> void match(List<ContrastInfo> contrastInfos,
                                     S sourceObj,
                                     T targetObj,
                                     Field sourceField,
                                     Field targetField,
                                     Function<Field, String> targetFieldDescriptionFun) {
        try {
            Object sourceObjFieldValue = FieldUtils.readField(sourceField, sourceObj, true);
            Object targetObjFieldValue = FieldUtils.readField(targetField, targetObj, true);
            if (ObjectUtil.isNotEmpty(sourceObjFieldValue)) {
                if (sourceObjFieldValue instanceof Number) {
                    sourceObjFieldValue = ((Number) sourceObjFieldValue).doubleValue();
                }
                if (targetObjFieldValue instanceof Number) {
                    targetObjFieldValue = ((Number) targetObjFieldValue).doubleValue();
                }
                if (!Objects.equals(targetObjFieldValue, sourceObjFieldValue)) {
                    String description = targetFieldDescriptionFun.apply(targetField);
                    contrastInfos.add(ContrastInfo.builder()
                            .description(description)
                            .sourceValue(sourceObjFieldValue)
                            .targetValue(targetObjFieldValue)
                            .build());

                }
            }
        } catch (IllegalAccessException e) {
            //ingore
        }
    }

    /**
     * gett
     *
     * @param cls              cls
     * @param d                d
     * @param ignoreProperties 忽略属性
     * @return {@link T}
     */
    private static <T> T getT(Class<T> cls, Object d, String... ignoreProperties) {
        try {
            return copyPropertiesAndGetTarget(d, cls.getDeclaredConstructor().newInstance(), ignoreProperties);
        } catch (InstantiationException
                 | IllegalAccessException
                 | InvocationTargetException
                 | NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }

    public static String deFormatContrastInfoDetails(BeanUtils.ContrastInfo d) {
        return d.getDescription() + "从[" + d.getSourceValue() + "] 修改为[" + d.getTargetValue() + "]";
    }

    /**
     * 对比信息
     *
     * @author 谢进伟
     * @createDate 2023/02/03
     */
    @Data
    @Builder
    public static class ContrastInfo {

        /**
         * 描述
         */
        private String description;

        /**
         * 源值
         */
        private Object sourceValue;
        /**
         * 目标值
         */
        private Object targetValue;

        @Tolerate
        public ContrastInfo() {

        }
    }
}

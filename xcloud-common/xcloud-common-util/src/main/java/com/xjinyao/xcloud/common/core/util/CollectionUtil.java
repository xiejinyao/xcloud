package com.xjinyao.xcloud.common.core.util;

import java.util.ArrayList;
import java.util.List;

/**
 * 集合工具类
 *
 * @author liwei
 * @createDate 2023-4-14 15:35
 */
public class CollectionUtil {
    /**
     * Object对象转为List集合
     *
     * @param obj   源对象
     * @param clazz List属性class
     * @param <T>   List属性类
     * @return List集合
     */
    public static <T> List<T> castList(Object obj, Class<T> clazz) {
        List<T> result = new ArrayList<>();
        if (obj instanceof List<?>) {
            for (Object o : (List<?>) obj) {
                result.add(clazz.cast(o));
            }
            return result;
        }
        return null;
    }
}

package com.xjinyao.xcloud.common.core.util;

import cn.hutool.core.date.LocalDateTimeUtil;
import com.xjinyao.xcloud.common.core.exception.DateParseException;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.LinkedList;
import java.util.List;

/**
 * @description 反射字段工具
 * @createDate 2020/5/8 14:34
 */
@Slf4j
@UtilityClass
public class FieldUtil {

    public List<Field> getFieldList(Class<?> clazz) {
        if (null == clazz) {
            return null;
        }
        List<Field> fieldList = new LinkedList<Field>();
        Field[] fields = clazz.getDeclaredFields();
        for (Field field : fields) {
            //过滤静态属性
            if (Modifier.isStatic(field.getModifiers())) {
                continue;
            }
            //过滤transient 关键字修饰的属性
            if (Modifier.isTransient(field.getModifiers())) {
                continue;
            }
            fieldList.add(field);
        }
        //处理父类字段
        Class<?> superClass = clazz.getSuperclass();
        if (superClass.equals(Object.class)) {
            return fieldList;
        }
        fieldList.addAll(getFieldList(superClass));
        return fieldList;
    }


    public void setFieldVal(Object obj, Field field, String value_str) {
        if (obj != null && field != null && StringUtils.isNotBlank(value_str)) {
            String fieldTypeName = field.getType().getSimpleName();
            setFieldVal(obj, field, value_str, fieldTypeName);
        }
    }

    public void setFieldVal(Object obj, Field field, String value_str, String fieldTypeName) {
        if (!field.canAccess(obj)) {
            field.setAccessible(true);
        }
        try {
            switch (fieldTypeName) {
                case "String":
                    field.set(obj, value_str);
                    break;
                case "BigDecimal":
                    field.set(obj, new BigDecimal(value_str));
                    break;
                case "float":
                case "Float":
                    field.set(obj, Float.valueOf(value_str));
                    break;
                case "byte":
                case "Byte":
                    field.set(obj, Byte.valueOf(value_str));
                    break;
                case "short":
                case "Short":
                    field.set(obj, Short.valueOf(value_str));
                    break;
                case "double":
                case "Double":
                    field.set(obj, Double.valueOf(value_str));
                    break;
                case "boolean":
                case "Boolean":
                    field.set(obj, BooleanUtils.toBoolean(value_str));
                    break;
                case "long":
                case "Long":
                    field.set(obj, Long.valueOf(value_str));
                    break;
                case "int":
                case "Integer":
                    field.set(obj, Integer.valueOf(value_str));
                    break;
                case "char":
                case "Character":
                    field.set(obj, value_str.charAt(0));
                    break;
                case "Date":
                    field.set(obj, DateUtil.parse(value_str));
                    break;
                case "Timestamp":
                    field.set(obj, new Timestamp(DateUtil.parse(value_str).getTime()));
                    break;
                case "LocalDateTime":
                    field.set(obj, LocalDateTimeUtil.of(DateUtil.parse(value_str)));
                    break;
                case "LocalDate":
                    field.set(obj, LocalDate.parse(value_str));
                    break;
                default:
                    log.error(fieldTypeName + " unable to resolve!");
            }
        } catch (IllegalAccessException | DateParseException e) {
            throw new RuntimeException(e);
        }
    }

}

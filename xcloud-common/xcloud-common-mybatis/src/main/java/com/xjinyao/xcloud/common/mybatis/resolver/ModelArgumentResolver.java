package com.xjinyao.xcloud.common.mybatis.resolver;

import com.baomidou.mybatisplus.extension.activerecord.Model;
import com.xjinyao.xcloud.common.core.util.FieldUtil;
import com.xjinyao.xcloud.common.mybatis.constants.ValuePrefix;
import com.xjinyao.xcloud.common.swagger.params.SearchParamSerializable;
import com.xjinyao.xcloud.common.swagger.params.XRangeParam;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.MethodParameter;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.util.Arrays;
import java.util.List;

/**
 * @description 实体类模型参数解析
 * @createDate 2020/5/8 11:40
 */
@Slf4j
public class ModelArgumentResolver implements HandlerMethodArgumentResolver {

	private static final String[] searchList;
	private static final String[] replacementList;

	public static Field BEGIN_FIELD;
	public static Field END_FIELD;

	static {
		Field[] fields = ValuePrefix.class.getFields();
		searchList = new String[fields.length];
		replacementList = new String[fields.length];
		for (int i = 0; i < fields.length; i++) {
			Field field = fields[i];
			try {
				searchList[i] = field.get(null).toString();
				replacementList[i] = "";
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		try {
			String beginFieldName = "begin";
			BEGIN_FIELD = XRangeParam.class.getDeclaredField(beginFieldName);
			BEGIN_FIELD.setAccessible(true);
		} catch (NoSuchFieldException e) {
			throw new RuntimeException(e);
		}
		try {
			String endFieldName = "end";
			END_FIELD = XRangeParam.class.getDeclaredField(endFieldName);
			END_FIELD.setAccessible(true);
		} catch (NoSuchFieldException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public boolean supportsParameter(MethodParameter parameter) {
		boolean isSupport = false;
		Class<?> cls = parameter.getParameterType();
		for (Class<?> acls = cls; acls != null; acls = acls.getSuperclass()) {
			if (acls.equals(Model.class) || acls.equals(SearchParamSerializable.class)) {
				isSupport = true;
				break;
			}
		}
		if (!isSupport) {
			isSupport = Arrays.asList(cls.getInterfaces()).contains(SearchParamSerializable.class);
		}
		return isSupport;
	}

	@Override
	public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer,
								  NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {
		HttpServletRequest request = webRequest.getNativeRequest(HttpServletRequest.class);
		Class<?> cls = parameter.getParameterType();
		Object obj = cls.getDeclaredConstructor().newInstance();
		List<Field> declaredFields = FieldUtil.getFieldList(cls);

		declaredFields.forEach(field -> {
			//过滤静态属性
			if (Modifier.isStatic(field.getModifiers())) {
				return;
			}
			//过滤transient 关键字修饰的属性
			if (Modifier.isTransient(field.getModifiers())) {
				return;
			}
			field.setAccessible(true);
			String fieldName = field.getName();
			assert request != null;
			Class<?> fieldType = field.getType();
			if (fieldType.equals(XRangeParam.class)) {
				log.info(fieldName + " is range param");
				String[] parameterValues = request.getParameterValues(fieldName);
				if (ArrayUtils.isNotEmpty(parameterValues)) {
					XRangeParam<?> xRangeParam = new XRangeParam<>();
					String xangetValueType = ((ParameterizedType) field.getGenericType()).getActualTypeArguments()[0].getTypeName();
					xangetValueType = StringUtils.substringAfterLast(xangetValueType, ".");
					if (parameterValues.length > 0) {
						if (StringUtils.isNotBlank(parameterValues[0])) {
							FieldUtil.setFieldVal(xRangeParam, BEGIN_FIELD, parameterValues[0], xangetValueType);
						}
						if (parameterValues.length > 1) {
							if (StringUtils.isNotBlank(parameterValues[1])) {
								FieldUtil.setFieldVal(xRangeParam, END_FIELD, parameterValues[1], xangetValueType);
							}
						}
						try {
							field.set(obj, xRangeParam);
						} catch (IllegalAccessException e) {
							throw new RuntimeException(e);
						}
					}
				}
			} else {
				String value_str = request.getParameter(fieldName);
				if (value_str != null) {
					value_str = StringUtils.replaceEach(value_str, searchList, replacementList);//剔除特殊搜索前缀
					FieldUtil.setFieldVal(obj, field, value_str);
				}
			}
		});

		return obj;
	}

}

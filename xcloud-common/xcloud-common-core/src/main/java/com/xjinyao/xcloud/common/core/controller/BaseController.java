package com.xjinyao.xcloud.common.core.controller;

import com.xjinyao.xcloud.common.core.util.FieldUtil;
import com.xjinyao.xcloud.common.core.util.R;
import com.xjinyao.xcloud.common.core.util.StringUtils;
import com.xjinyao.xcloud.common.swagger.annotation.AddDefaultValue;
import com.xjinyao.xcloud.common.swagger.params.AddParamSerializable;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.constraints.*;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.ParameterizedType;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.*;

/**
 * @author 谢进伟
 * @createDate 2022/12/6 08:56
 */
public class BaseController<AddDTO extends AddParamSerializable> {


	protected static Logger log;
	/**
	 * 请求对象
	 */
	@Resource
	private HttpServletRequest request;

	/**
	 * 响应对象对象
	 */
	@Resource
	private HttpServletResponse response;

	public BaseController() {
		log = LoggerFactory.getLogger(this.getClass());
	}

	/**
	 * 获取当前请求的所有参数
	 *
	 * @return
	 */
	protected Map<String, String[]> getParameterMap() {
		return Optional.ofNullable(request)
				.get()
				.getParameterMap();
	}

	@ApiOperation(value = "获取新增时的默认值参数", produces = MediaType.APPLICATION_JSON_VALUE)
	@GetMapping("/get/defaultParams")
	public R<AddDTO> getDefaultParams() {
		try {
			Class<?> superclass = this.getClass();
			Class<?> cls = (Class<?>) ((ParameterizedType) superclass.getGenericSuperclass())
					.getActualTypeArguments()[0];
			Object obj = cls.getDeclaredConstructor().newInstance();
			FieldUtils.getFieldsListWithAnnotation(cls, AddDefaultValue.class).forEach(field -> {
				String defaultValue = field.getAnnotation(AddDefaultValue.class).value();
				FieldUtil.setFieldVal(obj, field, defaultValue);
			});
			return R.ok((AddDTO) obj);
		} catch (InstantiationException | IllegalAccessException | InvocationTargetException |
				 NoSuchMethodException e) {
			throw new RuntimeException(e);
		}
	}

	@ApiOperation(value = "获取新增/修改时的参数验证规则", produces = MediaType.APPLICATION_JSON_VALUE)
	@GetMapping("/get/validateRules")
	public R<Map<String, List<Map<String, Object>>>> getValidateRules(@ApiParam("dto引用全路径") String className) {
		try {
			Class<?> cls = Class.forName(className);
			Map<String, List<Map<String, Object>>> allRules = new HashMap<>();
			FieldUtils.getAllFieldsList(cls).forEach(field -> {
				Class<?> fieldType = field.getType();
				String fieldTypeName = fieldType.getSimpleName().toLowerCase();

				List<Map<String, Object>> fieldRules = allRules.computeIfAbsent(field.getName(), v -> new ArrayList<>());
				String[] defaultTrigger = {"blur", "change"};

				if (fieldType.equals(Boolean.class) || fieldType.equals(boolean.class)) {
					//带注释的元素必须为false。支持的类型有布尔型和布尔型。 空元素被认为是有效的。
					AssertFalse assertFalse = field.getAnnotation(AssertFalse.class);
					if (assertFalse != null) {
						fieldRules.add(new HashMap<>() {{
							put("type", fieldTypeName);
							put("enum", new String[]{"false"});
							put("trigger", defaultTrigger);
							put("message", assertFalse.message());
						}});
					}
					//带注释的元素必须为true。支持的类型有布尔型和布尔型。 空元素被认为是有效的。
					AssertTrue assertTrue = field.getAnnotation(AssertTrue.class);
					if (assertTrue != null) {
						fieldRules.add(new HashMap<>() {{
							put("type", fieldTypeName);
							put("enum", new String[]{"true"});
							put("trigger", defaultTrigger);
							put("message", assertTrue.message());
						}});
					}
				}

				if (Arrays.asList(new Class<?>[]{
						BigDecimal.class,
						BigInteger.class,
						CharSequence.class,
						byte.class,
						short.class,
						int.class,
						long.class,
						Byte.class,
						Short.class,
						Integer.class,
						Long.class
				}).contains(fieldType)) {
					String type = fieldTypeName.equals("string") ? "string" : "number";
					String targetValue = fieldTypeName.equals("string") ? "value.length" : "value";
					//带注释的元素必须是一个数值，其值必须小于或等于指定的最大值。 支持的类型有：BigDecimal BigInteger CharSequence byte, short, int,long 及其各自的包装器 请注意，由于舍入错误，不支持double和float,空元素被认为是有效的
					DecimalMax decimalMax = field.getAnnotation(DecimalMax.class);
					if (decimalMax != null) {
						fieldRules.add(new HashMap<>() {{
							put("type", type);
							put("validator", "(rule, value, callback) => {\n" +
									"    if (/^(\\s{0}|undefined)$/.test(value) || " + targetValue + " " + (decimalMax.inclusive() ? "<=" : "<") + decimalMax.value() + ") {\n" +
									"        callback();\n" +
									"    } else {\n" +
									"        callback(new Error('" + decimalMax.message() + "'));\n" +
									"    }\n" +
									"}");
							put("trigger", defaultTrigger);
						}});
					}

					//带注释的元素必须是一个数值，其值必须大于或等于指定的最小值。 支持的类型有：BigDecimal BigInteger CharSequence byte, short, int,long 及其各自的包装器 请注意，由于舍入错误，不支持double和float,空元素被认为是有效的
					DecimalMin decimalMin = field.getAnnotation(DecimalMin.class);
					if (decimalMin != null) {
						fieldRules.add(new HashMap<>() {{
							put("type", type);
							put("validator", "(rule, value, callback) => {\n" +
									"    if (/^(\\s{0}|undefined)$/.test(value) || " + targetValue + " " + (decimalMin.inclusive() ? ">=" : ">") + decimalMin.value() + ") {\n" +
									"        callback();\n" +
									"    } else {\n" +
									"        callback(new Error('" + decimalMin.message() + "'));\n" +
									"    }\n" +
									"}");
							put("trigger", defaultTrigger);
						}});
					}
					//带注释的元素必须是可接受范围内的数字。 支持的类型有BigDecimal BigInteger CharSequence byte, short, int, long 及其各自的包装器,空元素被认为是有效的
					Digits digits = field.getAnnotation(Digits.class);
					if (digits != null) {
						fieldRules.add(new HashMap<>() {{
							put("type", type);
							String regex;
							if (digits.fraction() == 0) {
								//无小数部分
								regex = "/^\\d{1," + digits.integer() + "}$/";
							} else {
								//有小数部分
								regex = "/^\\d{1," + digits.integer() + "}(\\.(?<=\\.)\\d{1," + digits.fraction() + "})?$/";
							}
							put("validator", "(rule, value, callback) => {\n" +
									"    if (/^(\\s{0}|undefined)$/.test(value) || " + regex + ".test(value)) {\n" +
									"        callback();\n" +
									"    } else {\n" +
									"        callback(new Error('" + digits.message() + "'));\n" +
									"    }\n" +
									"}");
							put("trigger", defaultTrigger);
						}});
					}

				}

				if (Arrays.asList(new Class<?>[]{
						BigDecimal.class,
						BigInteger.class,
						byte.class,
						short.class,
						int.class,
						long.class,
						Byte.class,
						Short.class,
						Integer.class,
						Long.class
				}).contains(fieldType)) {
					//带注释的元素必须是一个数值，其值必须小于或等于指定的最大值。 支持的类型有：BigDecimal BigInteger byte, short, int, long,及其各自的包装器 请注意，由于舍入错误，不支持double和float，空元素被认为是有效的
					Max max = field.getAnnotation(Max.class);
					if (max != null) {
						fieldRules.add(new HashMap<>() {{
							put("type", "number");
							put("max", max.value());
							put("trigger", defaultTrigger);
							put("message", max.message());
						}});
					}

					//带注释的元素必须是一个数值，其值必须小于或等于指定的最小值。 支持的类型有：BigDecimal BigInteger byte, short, int, long,及其各自的包装器 请注意，由于舍入错误，不支持double和float，空元素被认为是有效的
					Min min = field.getAnnotation(Min.class);
					if (min != null) {
						fieldRules.add(new HashMap<>() {{
							put("type", "number");
							put("min", min.value());
							put("trigger", defaultTrigger);
							put("message", min.message());
						}});
					}
				}


				//字符串必须是格式正确的电子邮件地址。 空元素被认为是有效的。
				Email email = field.getAnnotation(Email.class);
				if (email != null) {
					fieldRules.add(new HashMap<>() {{
						put("type", "email");
						put("trigger", defaultTrigger);
						put("message", email.message());
					}});
				}


				if (Arrays.asList(new Class<?>[]{
						BigDecimal.class,
						BigInteger.class,
						byte.class,
						short.class,
						int.class,
						long.class,
						float.class,
						double.class,
						Byte.class,
						Short.class,
						Integer.class,
						Long.class,
						Float.class,
						Double.class
				}).contains(fieldType)) {
					//带注释的元素必须是严格的负数（即0被视为无效值）。 支持的类型有：BigDecimal BigInteger byte, short, int, long, float, double,及其各自的包装器，空元素被认为是有效的。
					Negative negative = field.getAnnotation(Negative.class);
					if (negative != null) {
						fieldRules.add(new HashMap<>() {{
							put("type", "number");
							put("validator", "(rule, value, callback) => {\n" +
									"    if (/^(\\s{0}|undefined)$/.test(value) || value < 0) {\n" +
									"        callback();\n" +
									"    } else {\n" +
									"        callback(new Error('" + negative.message() + "'));\n" +
									"    }\n" +
									"}");

							put("trigger", defaultTrigger);
						}});
					}
					//带注释的元素必须是负数或0。 支持的类型有：BigDecimal BigInteger byte, short, int, long, float, double ,及其各自的包装器，空元素被认为是有效的。
					NegativeOrZero negativeOrZero = field.getAnnotation(NegativeOrZero.class);
					if (negativeOrZero != null) {
						fieldRules.add(new HashMap<>() {{
							put("type", "number");
							put("validator", "(rule, value, callback) => {\n" +
									"    if (/^(\\s{0}|undefined)$/.test(value) || value <= 0) {\n" +
									"        callback();\n" +
									"    } else {\n" +
									"        callback(new Error('" + negativeOrZero.message() + "'));\n" +
									"    }\n" +
									"}");

							put("trigger", defaultTrigger);
						}});
					}
					//带注释的元素必须是严格正数（即0被视为无效值）。 支持的类型有：BigDecimal BigInteger byte, short, int, long, float, double 及其各自的包装器，空元素被认为是有效的
					Positive positive = field.getAnnotation(Positive.class);
					if (positive != null) {
						fieldRules.add(new HashMap<>() {{
							put("type", "number");
							put("validator", "(rule, value, callback) => {\n" +
									"    if (/^(\\s{0}|undefined)$/.test(value) || value > 0) {\n" +
									"        callback();\n" +
									"    } else {\n" +
									"        callback(new Error('" + positive.message() + "'));\n" +
									"    }\n" +
									"}");

							put("trigger", defaultTrigger);
						}});
					}
					//带注释的元素必须是正数或0。 支持的类型有：BigDecimal BigInteger byte, short, int, long, float, double 及其各自的包装器，空元素被认为是有效的
					PositiveOrZero positiveOrZero = field.getAnnotation(PositiveOrZero.class);
					if (positiveOrZero != null) {
						fieldRules.add(new HashMap<>() {{
							put("type", "number");
							put("validator", "(rule, value, callback) => {\n" +
									"    if (/^(\\s{0}|undefined)$/.test(value) || value >= 0) {\n" +
									"        callback();\n" +
									"    } else {\n" +
									"        callback(new Error('" + positiveOrZero.message() + "'));\n" +
									"    }\n" +
									"}");

							put("trigger", defaultTrigger);
						}});
					}
				}

				//带注释的元素不能为空，并且必须至少包含一个非空白字符
				NotBlank notBlank = field.getAnnotation(NotBlank.class);
				if (notBlank != null) {
					fieldRules.add(new HashMap<>() {{
						put("type", "string");
						put("required", "true");
						put("validator", "(rule, value, callback) => {\n" +
								"    if (!/^(\\s{0}|undefined)$/.test(value)) {\n" +
								"        callback();\n" +
								"    } else {\n" +
								"        callback(new Error('" + notBlank.message() + "'));\n" +
								"    }\n" +
								"}");
						put("trigger", defaultTrigger);
					}});
				}
				//带注释的元素不能为null或空,支持的类型有： CharSequence (计算字符序列的长度) 、Collection (计算集合大小) 、Map (计算Map大小) 、Array (计算数组长度)
				NotEmpty notEmpty = field.getAnnotation(NotEmpty.class);
				if (notEmpty != null) {
					fieldRules.add(new HashMap<>() {{
						String type = "string";
						if (StringUtils.equalsAny(fieldTypeName, "list", "set")) {
							type = "array";
						} else if (StringUtils.equals(fieldTypeName, "map")) {
							type = "object";
						}
						put("type", type);
						put("required", "true");
						put("validator", "(rule, value, callback) => {" +
								"    const BOOLEAN = 'boolean';\n" +
								"    const NUMBER = 'number';\n" +
								"    const STRING = 'string';\n" +
								"    const FUNCTION = 'function';\n" +
								"    const ARRAY = 'array';\n" +
								"    const DATE = 'date';\n" +
								"    const REGEXP = 'regExp';\n" +
								"    const UNDEFINED = 'undefined';\n" +
								"    const NULL = 'null';\n" +
								"    const OBJECT = 'object';\n" +
								"    let typeMap = {\n" +
								"        '[object Boolean]': BOOLEAN,\n" +
								"        '[object Number]': NUMBER,\n" +
								"        '[object String]': STRING,\n" +
								"        '[object Function]': FUNCTION,\n" +
								"        '[object Array]': ARRAY,\n" +
								"        '[object Date]': DATE,\n" +
								"        '[object RegExp]': REGEXP,\n" +
								"        '[object Undefined]': UNDEFINED,\n" +
								"        '[object Null]': NULL,\n" +
								"        '[object Object]': OBJECT\n" +
								"    }\n" +
								"    let toString = Object.prototype.toString\n" +
								"    let type = typeMap[toString.call(value)];\n" +
								"    let validated = false;\n" +
								"    if (type === ARRAY) {\n" +
								"        validated = value && value.length > 0;\n" +
								"    } else if (type === OBJECT) {\n" +
								"        validated = value && Object.keys(value).length > 0;\n" +
								"    } else {\n" +
								"        if (!/^\\s*$/.test(value) && value != null && (value + '').length > 0) {\n" +
								"            validated = true;\n" +
								"        }\n" +
								"    }\n" +
								"    if (validated) {\n" +
								"        callback();\n" +
								"    } else {\n" +
								"        callback(new Error('此字段不能为空'));\n" +
								"    }\n" +
								"}");
						put("trigger", defaultTrigger);
					}});
				}
				//带注释的元素不能为空
				NotNull notNull = field.getAnnotation(NotNull.class);
				if (notNull != null) {
					fieldRules.add(new HashMap<>() {{
						put("type", "object");
						put("required", "true");
						put("validator", "(rule, value, callback) => {\n" +
								"    if (value != null) {\n" +
								"        callback();\n" +
								"    } else {\n" +
								"        callback(new Error('" + notNull.message() + "'));\n" +
								"    }\n" +
								"}");
						put("trigger", defaultTrigger);
					}});
				}
				//带注释的元素必须为空
				Null aNull = field.getAnnotation(Null.class);
				if (aNull != null) {
					fieldRules.add(new HashMap<>() {{
						put("type", "object");
						put("validator", "(rule, value, callback) => {\n" +
								"    if (value == null) {\n" +
								"        callback();\n" +
								"    } else {\n" +
								"        callback(new Error('" + aNull.message() + "'));\n" +
								"    }\n" +
								"}");
						put("trigger", defaultTrigger);
					}});
				}
				//带注释的CharSequence必须与指定的正则表达式匹配。正则表达式遵循Java正则表达式约定
				Pattern pattern = field.getAnnotation(Pattern.class);
				if (pattern != null) {
					fieldRules.add(new HashMap<>() {{
						put("type", "string");
						String regexp = pattern.regexp();
						if (!regexp.startsWith("/")) {
							regexp = "/" + regexp;
						}
						if (!regexp.endsWith("/")) {
							regexp = regexp + "/";
						}
						put("validator", "(rule, value, callback) => {\n" +
								"    if (/^(\\s{0}|undefined)$/.test(value) || " + regexp + ".test(value)) {\n" +
								"        callback();\n" +
								"    } else {\n" +
								"        callback(new Error('" + pattern.message() + "'));\n" +
								"    }\n" +
								"}");
						put("trigger", defaultTrigger);
					}});
				}
				//注释的元素大小必须介于指定的边界（包括）之间。 支持的类型有：CharSequence (计算字符序列的长度) 、Collection (计算集合大小) 、Map (计算Map大小) 、Array (计算数组长度)，空元素被认为是有效的。
				Size size = field.getAnnotation(Size.class);
				if (size != null) {
					fieldRules.add(new HashMap<>() {{
						put("type", fieldTypeName);
						put("max", size.max());
						put("min", size.min());
						put("trigger", defaultTrigger);
						put("message", size.message());
					}});
				}
			});

			return R.ok(allRules);
		} catch (ClassNotFoundException e) {
			return R.ok(Collections.emptyMap());
		}
	}
}

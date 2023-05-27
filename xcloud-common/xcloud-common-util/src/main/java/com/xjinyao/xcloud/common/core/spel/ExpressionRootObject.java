package com.xjinyao.xcloud.common.core.spel;

import lombok.Data;

import java.lang.reflect.Method;

/**
 * 描述表达式计算期间使用的根对象的类。
 *
 * @author 谢进伟
 * @createDate 2023/5/4 14:21
 */
@Data
class ExpressionRootObject {


	private final Method method;

	private final Object[] args;

	private final Object target;

	private final Class<?> targetClass;

	public ExpressionRootObject(Method method, Object[] args, Object target, Class<?> targetClass) {
		this.method = method;
		this.args = args;
		this.target = target;
		this.targetClass = targetClass;
	}

	public String getMethodName() {
		return this.method.getName();
	}
}

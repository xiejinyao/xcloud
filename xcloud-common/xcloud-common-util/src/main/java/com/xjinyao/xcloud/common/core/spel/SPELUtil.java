package com.xjinyao.xcloud.common.core.spel;

import com.xjinyao.xcloud.common.core.util.StringUtils;
import lombok.experimental.UtilityClass;
import org.springframework.core.DefaultParameterNameDiscoverer;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.Expression;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

/**
 * @author 谢进伟
 * @description Spel表达式工具类
 * @createDate 2022/6/7 08:55
 */
@UtilityClass
public class SPELUtil {

	private final DefaultParameterNameDiscoverer nameDiscoverer = new DefaultParameterNameDiscoverer();
	private final SpelExpressionParser parser = new SpelExpressionParser();
	private static String expressionPrefix = "#";

	/**
	 * 解析表达式
	 *
	 * @param target           目标
	 * @param method           方法
	 * @param args             参数
	 * @param expressionString spEL表达式
	 * @return {@link String}
	 */
	public String parseExpression(Object target,
								  Method method,
								  Object[] args,
								  String expressionString) {
		return parseExpression(target, method, args, expressionString, null);
	}

	/**
	 * 解析表达式
	 *
	 * @param target           目标
	 * @param method           方法
	 * @param args             参数
	 * @param expressionString spEL表达式
	 * @param extendVariables  扩展变量映射
	 * @return {@link String}
	 */
	public String parseExpression(Object target,
								  Method method,
								  Object[] args,
								  String expressionString,
								  Map<String, Object> extendVariables) {
		if (StringUtils.isBlank(expressionString) || !expressionString.contains(expressionPrefix)) {
			return expressionString;
		}
		String[] paramNames = nameDiscoverer.getParameterNames(method);
		ExpressionRootObject expressionRootObject = new ExpressionRootObject(method, args, target, target.getClass());
		Expression expression = parser.parseExpression(expressionString);
		EvaluationContext context = new StandardEvaluationContext(expressionRootObject);
		if (paramNames != null && paramNames.length > 0) {
			for (int i = 0; i < args.length; i++) {
				if (paramNames.length > i) {
					context.setVariable(paramNames[i], args[i]);
				}
			}
		}
		Optional.ofNullable(extendVariables)
				.ifPresent(map -> map.forEach(context::setVariable));

		return Objects.toString(expression.getValue(context), expressionString);
	}
}

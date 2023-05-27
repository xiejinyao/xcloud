package com.xjinyao.xcloud.common.core.util;

import lombok.experimental.UtilityClass;
import org.apache.commons.collections4.CollectionUtils;

import javax.validation.ConstraintViolation;
import javax.validation.Valid;
import javax.validation.Validation;
import javax.validation.ValidatorFactory;
import java.util.Collection;
import java.util.Set;

/**
 * 验证工具
 *
 * @author 谢进伟
 * @createDate 2023/3/17 09:57
 */
@UtilityClass
public class ValidateUtil {

	public <T> String validate(@Valid Collection<T> t) {
		for (T t1 : t) {
			String message = validate(t1);
			if (message != null) {
				return message;
			}
		}
		return null;
	}

	public <T> String validate(@Valid T t) {
		try (ValidatorFactory validatorFactory = Validation.buildDefaultValidatorFactory()) {
			Set<ConstraintViolation<@Valid T>> validateSet = validatorFactory
					.getValidator()
					.validate(t);
			if (!CollectionUtils.isEmpty(validateSet)) {
				return validateSet.stream()
						.map(ConstraintViolation::getMessage)
						.reduce((m1, m2) -> m1 + "；" + m2)
						.orElse("参数输入有误！");
			}
		} catch (Exception e) {
			throw e;
		}
		return null;
	}
}

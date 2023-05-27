package com.xjinyao.xcloud.common.core.constant.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * @author chenjie
 * @description 字段校验常量
 * @createDate 2023/3/16 14:38
 */

@RequiredArgsConstructor
public enum PatternEnum {
    //校验字符串
    INPUT_STRING_RULE(Rules.INPUT_STRING_RULE, Descriptions.INPUT_STRING_RULE);

    /**
     * 校验规则
     */
    @Getter
    private final String rule;

    /**
     * 返回描述
     */
    @Getter
    private final String description;

    public interface Rules {
        String INPUT_STRING_RULE = "规则1";
    }

    public interface Descriptions {
        String INPUT_STRING_RULE = "请输入中文、数字、字符串";
    }
}





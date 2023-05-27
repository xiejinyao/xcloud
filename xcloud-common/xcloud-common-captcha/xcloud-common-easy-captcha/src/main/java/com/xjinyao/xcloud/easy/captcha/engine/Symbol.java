package com.xjinyao.xcloud.easy.captcha.engine;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 标识符
 *
 * @author L.cm
 */
@Getter
@RequiredArgsConstructor
public enum Symbol {
    /**
     * 标识符
     */
    NUM('n', false), ADD('+', false), SUB('-', false), MUL('x', true), DIV('÷', true);

    private final char value;

    private final boolean priority;

    public static Symbol of(char c) {
        Symbol[] values = Symbol.values();
        for (Symbol value : values) {
            if (value.value == c) {
                return value;
            }
        }
        throw new IllegalArgumentException("不支持的标识符，仅仅支持(+、-、×、÷)");
    }

}

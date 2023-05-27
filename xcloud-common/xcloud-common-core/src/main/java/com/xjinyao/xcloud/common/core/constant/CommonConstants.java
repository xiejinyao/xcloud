package com.xjinyao.xcloud.common.core.constant;

/**
 * 常见常量
 *
 * @author mengjiajie
 * @date 2019/2/1
 */
public interface CommonConstants {


    String DEFAULT_BASE_PACKAGE = "com.xjinyao.xcloud";

    /**
     * 删除
     */
    Boolean STATUS_DEL = Boolean.TRUE;

    /**
     * 正常
     */
    Boolean STATUS_NORMAL = Boolean.FALSE;

    /**
     * 拼接符
     */
    String CONCAT_CHARACTER = "/";
    /**
     * 下划线
     */
    String UNDERLINE = "_";
    String ACROSS = "-";

    /**
     * $
     */
    String DOLLAR = "$";
    /**
     * 等于
     */
    String EQUALS = "=";
    /**
     * 乘
     */
    String MULTIPLICATION = "*";
    /**
     * 左大括号
     */
    String LEFT_BRACES = "{";
    /**
     * 右大括号
     */
    String RIGHT_BRACES = "}";

    /**
     * 脱敏分隔符
     */
    String DESENSITIZATION_SEPARATOR = "****";

    /**
     * 逗号
     */
    String COMMA = ",";

    /**
     * 有效
     */
    Integer WORK = 1;
    /**
     * 无效
     */
    Integer NOT_WORK = 0;

    /**
     * 锁定
     */
    String STATUS_LOCK = "9";

    /**
     * 通用树根节点，值为：-1
     */
    Number TREE_ROOT_ID = -1;

    /**
     * 通用树根节点，值为：0
     */
    Number TREE_ROOT_ID_ZERO = 0;

    /**
     * 角色组dict类型
     */
    String ROLE_GROUP_DICT_TYPE = "role_group";

    /**
     * 菜单
     */
    String MENU = "0";

    /**
     * 编码
     */
    String UTF8 = "UTF-8";

    /**
     * JSON 资源
     */
    String CONTENT_TYPE = "application/json; charset=utf-8";

    /**
     * 前端工程名
     */
    String FRONT_END_PROJECT = "tcs-ui";

    /**
     * 后端工程名
     */
    String BACK_END_PROJECT = "tcs";

    /**
     * 成功标记
     */
    Integer SUCCESS = 0;

    /**
     * 失败标记
     */
    Integer FAIL = 1;

    /**
     * 验证码前缀
     */
    String DEFAULT_CODE_KEY = "DEFAULT_CODE_KEY_";

    /**
     * 当前页
     */
    String CURRENT = "current";

    /**
     * size
     */
    String SIZE = "size";

    /**
     * projectId
     */
    String PROJECT_ID = "projectId";

    String ADD = " 加";
    String EVERY_MONTH = " 每月";
    String NUM = "号";

    String SQUARE_METRE = "m²";
}

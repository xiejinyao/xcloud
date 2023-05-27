package com.xjinyao.xcloud.xxl.job.admin.api.enums;

/**
 * 调度过期策略
 *
 * @author 谢进伟
 * @createDate 2023/2/10 17:25
 */
public enum MisfireStrategyEnum {

    /**
     * do nothing
     */
    DO_NOTHING,

    /**
     * fire once now
     */
    FIRE_ONCE_NOW;
}

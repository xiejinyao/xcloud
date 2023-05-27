package com.xjinyao.xcloud.xxl.job.admin.api.enums;

/**
 * 调度类型
 *
 * @author 谢进伟
 * @createDate 2023/2/10 17:25
 */
public enum ScheduleTypeEnum {

    NONE,

    /**
     * schedule by cron
     */
    CRON,

    /**
     * schedule by fixed rate (in seconds)
     */
    FIX_RATE;
}

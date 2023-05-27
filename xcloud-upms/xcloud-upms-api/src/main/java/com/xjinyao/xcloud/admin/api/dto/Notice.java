package com.xjinyao.xcloud.admin.api.dto;

import lombok.Builder;
import lombok.Data;
import lombok.experimental.Tolerate;

/**
 * @author 谢进伟
 * @description 消息通知
 * @createDate 2021/3/8 11:03
 */
@Data
@Builder
public class Notice {

    /**
     * 标题
     */
    private String title;

    /**
     * 子标题
     */
    private String subtitle;

    /**
     * 标签
     */
    private String tag;

    /**
     * 状态
     */
    private String status;

    @Tolerate
    public Notice() {

    }
}

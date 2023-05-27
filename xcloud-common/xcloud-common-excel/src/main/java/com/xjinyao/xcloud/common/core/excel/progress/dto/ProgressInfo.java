package com.xjinyao.xcloud.common.core.excel.progress.dto;

import cn.hutool.core.util.NumberUtil;
import com.xjinyao.xcloud.common.core.util.StringUtils;
import lombok.Getter;

/**
 * @author 谢进伟
 * @description 数据解析进度信息
 * @createDate 2020/9/11 10:23
 */
@Getter
public class ProgressInfo {

    /**
     * 进度说明
     */
    private String stage;
    /**
     * 当前位置
     */
    protected Integer current;
    /**
     * 总量
     */
    protected Integer total;
    /**
     * 进度
     */
    protected String progress;
    /**
     * 进度百分比值
     */
    protected Double percentage;
    /**
     * 进度百分比文本格式
     */
    protected String percentageText;
    /**
     * 备注信息
     */
    protected String remark;

    public ProgressInfo(String stage, Integer current, Integer total, String remark) {
        String percentageFormat = String.format("%.2f", NumberUtil.mul(NumberUtil.div(current, total, 3)));
        this.progress = current + StringUtils.SLASH_SEPARATOR + total;
        this.percentage = Double.valueOf(percentageFormat) * 100;
        this.percentageText = percentage + "%";
        this.stage = stage;
        this.current = current;
        this.total = total;
        this.remark = remark;
    }
}

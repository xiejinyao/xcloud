package com.xjinyao.xcloud.area.enums;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;

/**
 * @author 谢进伟
 * @description 行政区域等级
 * @createDate 2020/5/25 10:48
 */
@ApiModel("行政区域等级")
public enum AreaLevelEnum {

    /**
     * 省
     */
    @ApiModelProperty("省级别")
    PROVINCE(1),
    /**
     * 市
     */
    @ApiModelProperty("市级别")
    CITY(2),
    /**
     * 区县
     */
    @ApiModelProperty("区县级别")
    COUNTY(3),
    /**
     * 乡镇
     */
    @ApiModelProperty("乡镇级别")
    TOWN(4),
    /**
     * 居委会
     */
    @ApiModelProperty("居委会级别")
    VILLAGE(5);

    @Getter
    private Integer value;

    AreaLevelEnum(Integer value) {
        this.value = value;
    }
}

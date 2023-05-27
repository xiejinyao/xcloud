package com.xjinyao.xcloud.admin.api.vo;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.xjinyao.xcloud.admin.api.entity.SysRoute;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 微服务视图对象
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SysRoutePageVO extends SysRoute {

    /**
     * 运行状态
     */
    @ApiModelProperty(value = "运行状态")
    private String runStatus;
    /**
     * 实例总数
     */
    @ApiModelProperty(value = "实例总数")
    private Integer instanceCount;

    /**
     * 已启用实例数
     */
    @ApiModelProperty(value = "已启用实例数")
    private Integer enableInstanceCount;

    /**
     * 健康实列数
     */
    @ApiModelProperty(value = "健康实列数")
    private Integer healthyInstanceCount;
}

package com.xjinyao.xcloud.core.rule.po;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author 谢进伟
 * @description 路由状态列表
 * @createDate 2020/11/17 17:02
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RouterStatusList implements Serializable {

    /**
     * 服务ID
     */
    private String serviceId;

    /**
     * API状态
     */
    private Boolean status;
}

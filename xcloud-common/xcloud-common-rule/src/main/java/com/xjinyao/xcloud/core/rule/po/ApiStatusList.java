package com.xjinyao.xcloud.core.rule.po;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpMethod;

import java.io.Serializable;

/**
 * @author 谢进伟
 * @description API状态列表
 * @createDate 2020/11/17 17:02
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ApiStatusList implements Serializable {

    /**
     * 服务ID
     */
    private String serviceId;

    /**
     * 接口匹配模式
     */
    private HttpMethod method;

    /**
     * 接口匹配模式
     */
    private String pattern;

    /**
     * API状态
     */
    private Boolean status;
}

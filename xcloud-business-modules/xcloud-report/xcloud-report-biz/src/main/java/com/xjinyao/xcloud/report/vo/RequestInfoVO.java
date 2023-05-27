package com.xjinyao.xcloud.report.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * 请求头信息
 *
 * @author liwei
 * @createDate 2023-4-18 11:41
 */
@Data
public class RequestInfoVO implements Serializable  {
    private static final long serialVersionUID = 1L;

    private String referer;
    private String origin;
    private String host;
    private String domain;
    private String loginUserName;
}

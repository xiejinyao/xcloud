package com.xjinyao.xcloud.common.core.properties;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;

/**
 * @author 谢进伟
 * @description 公共配置，该文件用来配置一些公用的资源配置属性
 * @createDate 2020/11/12 14:57
 */
public class CommonProperties {

    /**
     * 各个微服务是否多实例部署
     */
    @Getter
    @Value("${server.deploy.multiple:true}")
    private Boolean serverDeployMultiple;
}

package com.xjinyao.xcloud.gateway;

import com.xjinyao.xcloud.common.swagger.annotation.EnableCustomSwagger2;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @date 2018年06月21日
 * <p>
 * 网关应用
 */
@EnableCustomSwagger2
@SpringBootApplication
public class GatewayApplication {

    public static void main(String[] args) {
        SpringApplication.run(GatewayApplication.class, args);
    }

}

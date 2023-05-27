package com.xjinyao.xcloud.area;

import com.xjinyao.xcloud.common.security.annotation.EnableCustomFeignClients;
import com.xjinyao.xcloud.common.security.annotation.EnableCustomResourceServer;
import com.xjinyao.xcloud.common.swagger.annotation.EnableCustomSwagger2;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @date 2018年06月21日
 * 行政区域
 */
@EnableCustomSwagger2
@EnableCustomResourceServer
@EnableCustomFeignClients
@SpringBootApplication
public class AreaApplication {
    public static void main(String[] args) {
        SpringApplication.run(AreaApplication.class, args);
    }
}

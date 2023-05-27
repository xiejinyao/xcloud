package com.xjinyao.xcloud.auth;

import com.xjinyao.xcloud.common.security.annotation.EnableCustomFeignClients;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @date 2018年06月21日 认证授权中心
 */
@EnableCustomFeignClients
@SpringBootApplication
public class AuthApplication {

    public static void main(String[] args) {
        SpringApplication.run(AuthApplication.class, args);
    }

}

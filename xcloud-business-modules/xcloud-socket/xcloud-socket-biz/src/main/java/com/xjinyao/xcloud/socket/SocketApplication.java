package com.xjinyao.xcloud.socket;

import com.xjinyao.xcloud.common.security.annotation.EnableCustomFeignClients;
import com.xjinyao.xcloud.common.security.annotation.EnableCustomResourceServer;
import com.xjinyao.xcloud.common.swagger.annotation.EnableCustomSwagger2;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @author 谢进伟
 * @description Socket服务启动类
 * @createDate 2020/6/27 16:40
 */
@EnableCustomSwagger2
@EnableCustomResourceServer
@EnableCustomFeignClients
@SpringBootApplication
public class SocketApplication {

    public static void main(String[] args) {
        SpringApplication.run(SocketApplication.class, args);
    }
}

package com.xjinyao.xcloud.file;


import com.xjinyao.xcloud.common.security.annotation.EnableCustomFeignClients;
import com.xjinyao.xcloud.common.security.annotation.EnableCustomResourceServer;
import com.xjinyao.xcloud.common.swagger.annotation.EnableCustomSwagger2;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * @date 2018年06月21日
 * 文件服务
 */
@EnableCustomSwagger2
@EnableCustomResourceServer
@EnableCustomFeignClients
@SpringBootApplication
@EnableTransactionManagement
public class FileApplication {
    public static void main(String[] args) {
        SpringApplication.run(FileApplication.class, args);
    }

}

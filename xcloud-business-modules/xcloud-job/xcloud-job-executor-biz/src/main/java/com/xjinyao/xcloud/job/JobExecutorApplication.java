package com.xjinyao.xcloud.job;


import com.xjinyao.xcloud.common.job.annotation.EnableCustomXxlJob;
import com.xjinyao.xcloud.common.security.annotation.EnableCustomFeignClients;
import com.xjinyao.xcloud.common.security.annotation.EnableCustomResourceServer;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * @date 2018年06月21日
 * 任务执行器
 */
@EnableCustomXxlJob
@EnableCustomResourceServer
@EnableCustomFeignClients
@SpringBootApplication
@EnableTransactionManagement
public class JobExecutorApplication {

    public static void main(String[] args) {
        SpringApplication.run(JobExecutorApplication.class, args);
    }
}

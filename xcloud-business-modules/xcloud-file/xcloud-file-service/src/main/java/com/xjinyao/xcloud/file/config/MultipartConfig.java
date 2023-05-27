package com.xjinyao.xcloud.file.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.web.servlet.MultipartProperties;
import org.springframework.boot.web.servlet.MultipartConfigFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.servlet.MultipartConfigElement;
import java.io.File;

/**
 * @author 谢进伟
 * @createDate 2022/11/7 17:34
 */
@Slf4j
@Configuration
public class MultipartConfig {

    @Bean
    MultipartConfigElement multipartConfigElement(MultipartProperties multipartProperties) {
        MultipartConfigFactory factory = new MultipartConfigFactory();
        File tmpDirFile = new File(multipartProperties.getLocation());
        log.info("文件上传临时文件夹创路径：{}", tmpDirFile.getPath());
        if (!tmpDirFile.exists()) {
            if (tmpDirFile.mkdirs()) {
                log.info("文件上传临时文件夹创建成功！路径：{}", tmpDirFile.getPath());
            } else {
                log.error("文件上传临时文件夹创建失败！路径：{}", tmpDirFile.getPath());
            }
        }
        factory.setMaxFileSize(multipartProperties.getMaxFileSize());
        factory.setMaxRequestSize(multipartProperties.getMaxRequestSize());
        factory.setFileSizeThreshold(multipartProperties.getFileSizeThreshold());
        factory.setLocation(multipartProperties.getLocation());
        return factory.createMultipartConfig();
    }
}

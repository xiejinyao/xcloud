package com.xjinyao.xcloud.interactive.captcha.config;

import com.xjinyao.xcloud.interactive.captcha.core.consts.Const;
import com.xjinyao.xcloud.interactive.captcha.core.service.CaptchaService;
import com.xjinyao.xcloud.interactive.captcha.core.service.impl.CaptchaServiceFactory;
import com.xjinyao.xcloud.interactive.captcha.core.util.ImageUtils;
import com.xjinyao.xcloud.interactive.captcha.properties.CaptchaProperties;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.util.Base64Utils;
import org.springframework.util.FileCopyUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

@Slf4j
@Configuration
public class CaptchaServiceAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public CaptchaService captchaService(CaptchaProperties properties) {
        log.info("自定义配置项：{}", properties.toString());
        Properties config = new Properties();
        config.put(Const.CAPTCHA_CACHETYPE, properties.getCacheType().name());
        config.put(Const.CAPTCHA_WATER_MARK, properties.getWaterMark());
        config.put(Const.CAPTCHA_FONT_TYPE, properties.getFontType());
        config.put(Const.CAPTCHA_TYPE, properties.getType().getCodeValue());
        config.put(Const.CAPTCHA_INTERFERENCE_OPTIONS, properties.getInterferenceOptions());
        config.put(Const.ORIGINAL_PATH_JIGSAW, properties.getJigsaw());
        config.put(Const.ORIGINAL_PATH_PIC_CLICK, properties.getPicClick());
        config.put(Const.CAPTCHA_SLIP_OFFSET, properties.getSlipOffset());
        config.put(Const.CAPTCHA_AES_STATUS, properties.getAesStatus());
        config.put(Const.CAPTCHA_WATER_ENABLE, properties.getWaterMarkEnable());
        config.put(Const.CAPTCHA_WATER_FONT, properties.getWaterFont());
        config.put(Const.CAPTCHA_CACAHE_MAX_NUMBER, properties.getCacheNumber());
        config.put(Const.CAPTCHA_TIMING_CLEAR_SECOND, properties.getTimingClear());
        if ((StringUtils.isNotBlank(properties.getJigsaw()) && properties.getJigsaw().startsWith("classpath:"))
                || (StringUtils.isNotBlank(properties.getPicClick()) && properties.getPicClick().startsWith("classpath:"))) {
            //自定义resources目录下初始化底图
            config.put(Const.CAPTCHA_INIT_ORIGINAL, "true");
            initializeBaseMap(properties.getJigsaw(), properties.getPicClick());
        }
        return CaptchaServiceFactory.getInstance(config);
    }

    public static Map<String, String> getResourcesImagesFile(String path) {
        Map<String, String> imgMap = new HashMap<>();
        ResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
        try {
            Resource[] resources = resolver.getResources(path);
            for (Resource resource : resources) {
                byte[] bytes = FileCopyUtils.copyToByteArray(resource.getInputStream());
                String string = Base64Utils.encodeToString(bytes);
                String filename = resource.getFilename();
                imgMap.put(filename, string);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return imgMap;
    }

    private static void initializeBaseMap(String jigsaw, String picClick) {
        ImageUtils.cacheBootImage(getResourcesImagesFile(jigsaw + "/original/*.png"),
                getResourcesImagesFile(jigsaw + "/slidingBlock/*.png"),
                getResourcesImagesFile(picClick + "/*.png"));
    }
}

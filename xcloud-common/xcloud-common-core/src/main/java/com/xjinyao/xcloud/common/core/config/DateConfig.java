package com.xjinyao.xcloud.common.core.config;

import cn.hutool.core.date.DatePattern;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;

/**
 * @author 谢进伟
 * @description 日期字符串作为参数的解析转换配置;json中包含日期字段时的相关配置见：{@link JacksonConfiguration}
 * @createDate 2020/12/22 9:28
 */
@Configuration
public class DateConfig {

    /**
     * LocalDate转换器，用于转换RequestParam和PathVariable参数
     */
    @Bean
    public Converter<String, LocalDate> localDateConverter() {
        Converter<String, LocalDate> converter = new Converter<String, LocalDate>() {
            @Override
            public LocalDate convert(String source) {
                return LocalDate.parse(source, DateTimeFormatter.ofPattern(DatePattern.NORM_DATE_PATTERN));
            }
        };
        return converter;
    }

    /**
     * LocalDateTime转换器，用于转换RequestParam和PathVariable参数
     */
    @Bean
    public Converter<String, LocalDateTime> localDateTimeConverter() {
        Converter<String, LocalDateTime> converter = new Converter<String, LocalDateTime>() {
            @Override
            public LocalDateTime convert(String source) {
                return LocalDateTime.parse(source, DateTimeFormatter.ofPattern(DatePattern.NORM_DATETIME_PATTERN));
            }
        };
        return converter;
    }

    /**
     * LocalTime转换器，用于转换RequestParam和PathVariable参数
     */
    @Bean
    public Converter<String, LocalTime> localTimeConverter() {
        Converter<String, LocalTime> converter = new Converter<String, LocalTime>() {
            @Override
            public LocalTime convert(String source) {
                return LocalTime.parse(source, DateTimeFormatter.ofPattern(DatePattern.NORM_TIME_PATTERN));
            }
        };
        return converter;
    }

    /**
     * Date转换器，用于转换RequestParam和PathVariable参数
     */
    @Bean
    public Converter<String, Date> dateConverter() {
        Converter<String, Date> converter = new Converter<String, Date>() {
            @Override
            public Date convert(String source) {
                SimpleDateFormat format = new SimpleDateFormat(DatePattern.NORM_DATETIME_PATTERN);
                try {
                    return format.parse(source);
                } catch (ParseException e) {
                    throw new RuntimeException(e);
                }
            }
        };
        return converter;
    }
}

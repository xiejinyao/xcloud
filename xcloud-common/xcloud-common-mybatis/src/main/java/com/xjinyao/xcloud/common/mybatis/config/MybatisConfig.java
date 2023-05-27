package com.xjinyao.xcloud.common.mybatis.config;

import com.baomidou.mybatisplus.autoconfigure.MybatisPlusProperties;
import com.github.thinwonton.mybatis.metamodel.mybatisplus.spring.MetaModelContextFactoryBean;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @since 2018-08-10
 */
@Configuration
@MapperScan(annotationClass = Mapper.class, basePackages = "${mybatis-plus.mapper-base-packages:com.xjinyao.xcloud}")
public class MybatisConfig {

    @Bean
    public MetaModelContextFactoryBean metaModelContextFactory(SqlSessionFactory sqlSessionFactory,
                                                               MybatisPlusProperties mybatisPlusProperties) {
        MetaModelContextFactoryBean metaModelContextFactoryBean = new MetaModelContextFactoryBean(sqlSessionFactory,
                mybatisPlusProperties.getGlobalConfig());
        return metaModelContextFactoryBean;
    }
}

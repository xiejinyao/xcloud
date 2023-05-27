package com.jinyao.xdp.lock.config;

import com.jinyao.xdp.lock.aspect.XLockAspect;
import com.jinyao.xdp.lock.redisson.XRedissonLook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.core.annotation.Order;

/**
 * 分布式锁配置
 * @author 谢进伟
 * @createDate 2022/8/25 08:39
 */
@EnableAspectJAutoProxy
public class XLockAspectConfiguration {

    @Bean
    @Order
    public XLockAspect xLockAop(@Autowired(required = false) XRedissonLook redisLook) {
        return new XLockAspect(redisLook);
    }
}

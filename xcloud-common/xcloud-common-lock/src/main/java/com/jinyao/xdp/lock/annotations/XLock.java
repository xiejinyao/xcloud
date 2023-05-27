package com.jinyao.xdp.lock.annotations;

import com.jinyao.xdp.lock.function.XLockType;

import java.lang.annotation.*;
import java.util.concurrent.TimeUnit;

/**
 * 此注解用来标识方法是否需要在加锁的环境下运行，请勿在异步环境下使用此注解
 * <p>
 * 注意：此注解若在异步环(如"Spring WebFlux")环境此注解无法达到理想的效果甚至失效，这是由于其异步的执行的逻辑所导致的
 *
 * @author 谢进伟
 * @createDate 2022/8/24 14:46
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface XLock {

    /**
     * 加锁的Key，需保证全局唯一
     *
     * @return
     */
    String key();

    /**
     * 等待的时间
     *
     * @return
     */
    long maxWait() default 20;

    /**
     * 时间单位
     *
     * @return
     */
    TimeUnit waitUnit() default TimeUnit.SECONDS;

    /**
     * 锁的类型
     *
     * @return
     */
    XLockType type() default XLockType.REDIS_LOCK;

}

package com.jinyao.xdp.lock.function;

/**
 * @author 谢进伟
 * @createDate 2022/8/27 22:43
 */
public
enum XLockType {

    /**
     * Redis 分布式锁
     */
    REDIS_LOCK,
    /**
     * Redis 分布式读锁
     */
    REDIS_READ_LOCK,
    /**
     * Redis 分布式写锁
     */
    REDIS_WRITE_LOCK,
}

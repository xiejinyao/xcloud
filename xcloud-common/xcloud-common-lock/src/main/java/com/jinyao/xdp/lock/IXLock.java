package com.jinyao.xdp.lock;

import com.jinyao.xdp.lock.function.XLockCallBackFunction;

import java.util.concurrent.TimeUnit;

/**
 * 锁
 * @author 谢进伟
 * @createDate 2022/8/24 14:45
 */
public interface IXLock {

    /**
     * 互斥锁
     *
     * @param call     回调
     * @param key      加锁的key
     * @param maxWait  等待的时间
     * @param waitUnit 时间单位
     * @return
     */
    void lock(XLockCallBackFunction call, String key, long maxWait, TimeUnit waitUnit) throws Exception;

    /**
     * 读锁
     *
     * @param call     回调
     * @param key      加锁的key
     * @param maxWait  等待的时间
     * @param waitUnit 时间单位
     * @return
     */
    void readLock(XLockCallBackFunction call, String key, long maxWait, TimeUnit waitUnit) throws Exception;

    /**
     * 写锁
     *
     * @param call     回调
     * @param key      加锁的key
     * @param maxWait  等待的时间
     * @param waitUnit 时间单位
     * @return
     */
    void writeLock(XLockCallBackFunction call, String key, long maxWait, TimeUnit waitUnit) throws Exception;
}

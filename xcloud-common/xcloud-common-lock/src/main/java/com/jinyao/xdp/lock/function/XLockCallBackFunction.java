package com.jinyao.xdp.lock.function;

/**
 * 加锁之后的函数回调
 * @author 谢进伟
 * @createDate 2022/8/24 23:39
 */
public interface XLockCallBackFunction {

    /**
     * 加锁成功之后的处理回调
     */
    void apply();

}

package com.jinyao.xdp.lock.redisson.enums;

/**
 * 时间循环分组类型枚举
 * @author 谢进伟
 * @createDate 2022/8/26 08:40
 */
public enum XEventLoopGroupEnum {

    /**
     * io.netty.channel.epoll.EpollEventLoopGroup
     */
    EPOLL_EVENT_LOOP_GROUP,

    /**
     * io.netty.channel.kqueue.KQueueEventLoopGroup
     */
    K_QUEUE_EVENT_LOOP_GROUP,

    /**
     * io.netty.channel.nio.NioEventLoopGroup
     */
    NIO_EVENT_LOOP_GROUP
}

package com.jinyao.xdp.lock.redisson.enums;

import org.redisson.api.DefaultNatMapper;
import org.redisson.api.HostNatMapper;
import org.redisson.api.HostPortNatMapper;

/**
 * 定义映射 Redis URI 对象的 NAT 映射器接口。它适用于所有 Redis 连接。
 * @author 谢进伟
 * @createDate 2022/8/26 09:21
 */
public enum XNatMapperEnum {
    /**
     * {@link DefaultNatMapper}
     */
    DEFAULT_NAT_MAPPER,
    /**
     * {@link HostPortNatMapper}
     */
    HOST_PORT_NAT_MAPPER,
    /**
     * {@link HostNatMapper}
     */
    HOST_NAT_MAPPER
}

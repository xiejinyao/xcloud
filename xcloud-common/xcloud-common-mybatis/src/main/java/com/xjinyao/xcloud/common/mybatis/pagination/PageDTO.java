package com.xjinyao.xcloud.common.mybatis.pagination;

import com.baomidou.mybatisplus.core.metadata.OrderItem;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import java.io.Serializable;
import java.util.List;

/**
 * @author 谢进伟
 * @createDate 2022/11/11 20:57
 */
public class PageDTO extends com.baomidou.mybatisplus.extension.plugins.pagination.PageDTO implements Serializable {


    public static <T> Page<T> of(long current, long size, List<OrderItem> items) {
        Page<T> of = of(current, size, 0);
        of.addOrder(items);
        return of;
    }
}

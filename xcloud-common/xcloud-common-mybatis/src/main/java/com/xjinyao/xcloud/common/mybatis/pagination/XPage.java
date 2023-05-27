package com.xjinyao.xcloud.common.mybatis.pagination;

import com.baomidou.mybatisplus.core.metadata.OrderItem;
import lombok.Data;
import lombok.Setter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 分页数据
 *
 * @author 谢进伟
 * @createDate 2022/9/29 11:14
 */
@Data
public class XPage<T> implements Serializable {

    /**
     * 查询数据列表
     */
    private List<T> records = Collections.emptyList();

    /**
     * 总数
     */
    protected long total = 0;
    /**
     * 每页显示条数，默认 10
     */
    protected long pageSize = 10;

    /**
     * 当前页
     */
    protected long current = 1;

    /**
     * 排序字段信息
     */
    @Setter
    protected List<OrderItem> orders = new ArrayList<>();

}

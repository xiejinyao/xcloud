package com.xjinyao.xcloud.common.mybatis.resolver;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.metadata.OrderItem;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xjinyao.xcloud.common.core.util.StringUtils;
import com.xjinyao.xcloud.common.mybatis.pagination.XPageParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.MethodParameter;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import javax.servlet.http.HttpServletRequest;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @date 2019-06-24
 * <p>
 * 解决Mybatis Plus Order By SQL注入问题
 */
@Slf4j
public class SqlFilterArgumentResolver implements HandlerMethodArgumentResolver {

    /**
     * 判断Controller是否包含page 参数
     *
     * @param parameter 参数
     * @return 是否过滤
     */
    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.getParameterType().equals(Page.class) ||
                parameter.getParameterType().equals(XPageParam.class);
    }

    /**
     * @param parameter     入参集合
     * @param mavContainer  model 和 view
     * @param webRequest    web相关
     * @param binderFactory 入参解析
     * @return 检查后新的page对象
     * <p>
     * page 只支持查询 GET .如需解析POST获取请求报文体处理
     */
    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer,
                                  NativeWebRequest webRequest, WebDataBinderFactory binderFactory) {

        HttpServletRequest request = webRequest.getNativeRequest(HttpServletRequest.class);

        String[] ascColumns = request.getParameterValues("ascColumns");
        String[] descColumns = request.getParameterValues("descColumns");
        String current = request.getParameter("current");
        String size = request.getParameter("size");
        String pageSize = request.getParameter("pageSize");

        Long targetCurrent = null;
        if (StrUtil.isNotBlank(current)) {
            targetCurrent = Long.parseLong(current);
        }
        Long targetPageSize = null;
        if (StrUtil.isNotBlank(size)) {
            targetPageSize = Long.parseLong(size);
        } else if (StrUtil.isNotBlank(pageSize)) {
            targetPageSize = Long.parseLong(pageSize);
        }

        List<OrderItem> orderItemList = new ArrayList<>();
        Optional.ofNullable(ascColumns).ifPresent(s -> orderItemList.addAll(Arrays.stream(s).filter(Objects::nonNull)
                .map(this::clear).map(OrderItem::asc).collect(Collectors.toList())));
        Optional.ofNullable(descColumns).ifPresent(s -> orderItemList.addAll(Arrays.stream(s).filter(Objects::nonNull)
                .map(this::clear).map(OrderItem::desc).collect(Collectors.toList())));

        if (parameter.getParameterType().equals(Page.class)) {
            Page<?> page = new Page<>();
            if (targetCurrent != null) {
                page.setCurrent(targetCurrent);
            }

            if (targetPageSize != null) {
                page.setSize(targetPageSize);
            }
            page.addOrder(orderItemList);

            return page;
        } else {
            XPageParam page = new XPageParam();
            if (targetCurrent != null) {
                page.setCurrent(targetCurrent);
            }

            if (targetPageSize != null) {
                page.setPageSize(targetPageSize);
            }
            page.setOrders(orderItemList);
            return page;
        }
    }

    /**
     * 参数清理
     *
     * @param param 参数
     * @return String
     */
    private String clear(String param) {
        if (StrUtil.isBlank(param)) {
            return StrUtil.trim(param);
        }
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < param.length(); i++) {
            char c = param.charAt(i);
            if (Character.isJavaIdentifierPart(c)) {
                builder.append(c);
            }
        }
        return StringUtils.underlineStr(builder.toString());
    }

}

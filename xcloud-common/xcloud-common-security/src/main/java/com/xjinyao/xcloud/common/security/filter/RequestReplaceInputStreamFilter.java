package com.xjinyao.xcloud.common.security.filter;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

/**
 * @author 谢进伟
 * @description 替换HttpServletRequest
 * @createDate 2021/2/26 15:31
 */
@Slf4j
public class RequestReplaceInputStreamFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        log.info("StreamFilter init ...");
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        if (ServletFileUpload.isMultipartContent((HttpServletRequest) request)) {
            //文件上传请求不替换
            chain.doFilter(request, response);
            return;
        }
        ServletRequest requestWrapper = new RequestWrapper((HttpServletRequest) request);

        chain.doFilter(requestWrapper, response);
    }

    @Override
    public void destroy() {
        log.info("StreamFilter destory ...");
    }
}
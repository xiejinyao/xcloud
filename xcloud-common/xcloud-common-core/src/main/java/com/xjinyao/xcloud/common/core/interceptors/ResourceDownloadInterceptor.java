package com.xjinyao.xcloud.common.core.interceptors;

import org.apache.commons.lang3.StringUtils;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

/**
 * @author 谢进伟
 * @description 资源下载拦截器
 * @createDate 2020/9/12 16:30
 */
public class ResourceDownloadInterceptor implements HandlerInterceptor {

    /**
     * 资源下载前缀
     */
    public final static String DOWNLOAD_REQUEST_URI_PREFIX = "/static/download";

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws UnsupportedEncodingException {
        String requestURI = request.getRequestURI();
        String contextPath = request.getContextPath();
        requestURI = StringUtils.substringAfter(requestURI, contextPath);
        if (requestURI.startsWith(DOWNLOAD_REQUEST_URI_PREFIX)) {
            String downloadFileName = StringUtils.substringAfterLast(requestURI, "/");
            response.setHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode(downloadFileName, StandardCharsets.UTF_8.toString()));
        }
        return Boolean.TRUE;
    }

}

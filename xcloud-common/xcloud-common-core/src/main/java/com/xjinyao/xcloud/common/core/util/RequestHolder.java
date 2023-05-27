package com.xjinyao.xcloud.common.core.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.Objects;

/**
 * 获取HttpServletRequest
 *
 * @since 2020-7-13
 */
@Slf4j
public class RequestHolder {

    private static final String UNKNOWN = "unknown";
    private static final String COMMA = ",";

    /**
     * 获取HttpServletRequest请求
     *
     * @return HttpServletRequest
     */
    public static HttpServletRequest getHttpServletRequest() {
        return ((ServletRequestAttributes) Objects.requireNonNull(RequestContextHolder.getRequestAttributes())).getRequest();
    }

    /**
     * 获取请求头参数
     *
     * @param headerName 请求头参数名
     * @return
     */
    public static String getHeaderValue(String headerName) {
        return getHttpServletRequest().getHeader(headerName);
    }

    /**
     * 获取请求头参数
     *
     * @param headerName 请求头参数名
     * @return
     */
    public static Long getHeaderLongValue(String headerName) {
        String header = getHeaderValue(headerName);
        if (StringUtils.isBlank(header)) {
            return null;
        }
        return Long.parseLong(header);
    }

    /**
     * 获取请求头参数
     *
     * @param headerName 请求头参数名
     * @return
     */
    public static Integer getHeaderIntValue(String headerName) {
        String header = getHeaderValue(headerName);
        if (StringUtils.isBlank(header)) {
            return null;
        }
        return Integer.parseInt(header);
    }

    /**
     * 获取请求头参数
     *
     * @param headerName 请求头参数名
     * @return
     */
    public static Double getHeaderDoubleValue(String headerName) {
        String header = getHeaderValue(headerName);
        if (StringUtils.isBlank(header)) {
            return null;
        }
        return Double.parseDouble(header);
    }

    /**
     * 获取请求参数
     *
     * @param paramName 参数名
     * @return
     */
    public static String getParamValue(String paramName) {
        return getHttpServletRequest().getParameter(paramName);
    }

    /**
     * 获取请求参数
     *
     * @param paramName 参数名
     * @return
     */
    public static Long getParamLongValue(String paramName) {
        String parameter = getParamValue(paramName);
        if (StringUtils.isBlank(parameter)) {
            return null;
        }
        return Long.parseLong(parameter);
    }

    /**
     * 获取请求参数
     *
     * @param paramName 参数名
     * @return
     */
    public static Integer getParamIntValue(String paramName) {
        String parameter = getParamValue(paramName);
        if (StringUtils.isBlank(parameter)) {
            return null;
        }
        return Integer.parseInt(parameter);
    }

    /**
     * 获取请求参数
     *
     * @param paramName 参数名
     * @return
     */
    public static Double getParamDoubleValue(String paramName) {
        String parameter = getParamValue(paramName);
        if (StringUtils.isBlank(parameter)) {
            return null;
        }
        return Double.parseDouble(parameter);
    }


    /**
     * 获取请求IP
     *
     * @return String IP
     */
    public static String getHttpServletRequestIpAddress() {
        HttpServletRequest request = getHttpServletRequest();
        return getHttpServletRequestIpAddress(request);
    }

    public static String getHttpServletRequestIpAddress(HttpServletRequest request) {
        String ip = request.getHeader("x-forwarded-for");
        if (ip == null || ip.length() == 0 || UNKNOWN.equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || UNKNOWN.equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || UNKNOWN.equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        if (ip.contains(",")) {
            ip = ip.split(",")[0];
        }
        return "0:0:0:0:0:0:0:1".equals(ip) ? "127.0.0.1" : ip;
    }

    public static String getServerHttpRequestIpAddress(ServerHttpRequest request) {
        HttpHeaders headers = request.getHeaders();
        String ip = headers.getFirst("x-forwarded-for");
        if (ip != null && ip.length() != 0 && !UNKNOWN.equalsIgnoreCase(ip)) {
            if (ip.contains(COMMA)) {
                ip = ip.split(COMMA)[0];
            }
        }
        if (ip == null || ip.length() == 0 || UNKNOWN.equalsIgnoreCase(ip)) {
            ip = headers.getFirst("Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || UNKNOWN.equalsIgnoreCase(ip)) {
            ip = headers.getFirst("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || UNKNOWN.equalsIgnoreCase(ip)) {
            ip = headers.getFirst("HTTP_CLIENT_IP");
        }
        if (ip == null || ip.length() == 0 || UNKNOWN.equalsIgnoreCase(ip)) {
            ip = headers.getFirst("HTTP_X_FORWARDED_FOR");
        }
        if (ip == null || ip.length() == 0 || UNKNOWN.equalsIgnoreCase(ip)) {
            ip = headers.getFirst("X-Real-IP");
        }
        if (ip == null || ip.length() == 0 || UNKNOWN.equalsIgnoreCase(ip)) {
            ip = Objects.requireNonNull(request.getRemoteAddress()).getAddress().getHostAddress();
        }
        return "0:0:0:0:0:0:0:1".equals(ip) ? "127.0.0.1" : ip;
    }
}

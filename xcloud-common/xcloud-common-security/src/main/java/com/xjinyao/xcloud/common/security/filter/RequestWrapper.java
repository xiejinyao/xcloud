package com.xjinyao.xcloud.common.security.filter;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.compress.utils.ByteUtils;
import org.apache.commons.io.IOUtils;

import javax.servlet.ReadListener;
import javax.servlet.ServletInputStream;
import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import java.io.*;
import java.nio.charset.Charset;

/**
 * @author 谢进伟
 * @description 请求包装器，包装HttpServletRequest，目的是让其输入流可重复读
 * @createDate 2021/2/26 15:29
 */
@Slf4j
public class RequestWrapper extends HttpServletRequestWrapper {
    /**
     * 存储body数据的容器
     */
    private final byte[] body;

    public RequestWrapper(HttpServletRequest request) throws IOException {
        super(request);

        //获取参数，保证contentType为application/x-www-form-urlencoded;时post请求能正常获取参数
        request.getParameterMap();
        // 将body数据存储起来
        String bodyStr = getBodyString(request);
        body = bodyStr.getBytes(Charset.defaultCharset());
    }

    /**
     * 获取请求Body
     *
     * @param request request
     * @return String
     */
    public String getBodyString(final ServletRequest request) {
        try {
            return inputToString(request.getInputStream());
        } catch (IOException e) {
            log.error("获取body失败!{}", e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }

    /**
     * 获取请求Body
     *
     * @return String
     */
    public String getBodyString() throws IOException {
        final InputStream inputStream = new ByteArrayInputStream(body);
        return inputToString(inputStream);
    }

    /**
     * 将一个输入刘转换成字符串
     *
     * @param inputStream 输入流
     * @return
     * @throws IOException
     */
    private String inputToString(InputStream inputStream) throws IOException {
        return IOUtils.toString(inputStream, Charset.defaultCharset());
    }

    @Override
    public BufferedReader getReader() throws IOException {
        return new BufferedReader(new InputStreamReader(getInputStream()));
    }

    @Override
    public ServletInputStream getInputStream() throws IOException {

        final ByteArrayInputStream inputStream = new ByteArrayInputStream(body);

        return new ServletInputStream() {

            @Override
            public int readLine(byte[] b, int off, int len) throws IOException {
                return (int) ByteUtils.fromLittleEndian(body, off, len);
            }

            @Override
            public int read() throws IOException {
                return inputStream.read();
            }

            @Override
            public boolean isFinished() {
                return false;
            }

            @Override
            public boolean isReady() {
                return false;
            }

            @Override
            public void setReadListener(ReadListener readListener) {

            }
        };
    }
}

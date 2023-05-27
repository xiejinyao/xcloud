package com.xjinyao.xcloud.common.security.aspect;

import cn.hutool.core.util.StrUtil;
import com.xjinyao.xcloud.admin.api.entity.SysResource;
import com.xjinyao.xcloud.admin.api.feign.RemoteApplicationService;
import com.xjinyao.xcloud.common.core.constant.SecurityConstants;
import com.xjinyao.xcloud.common.core.util.R;
import com.xjinyao.xcloud.common.security.annotation.OpenApiResource;
import com.xjinyao.xcloud.common.security.filter.RequestWrapper;
import com.xjinyao.xcloud.common.security.util.SignUtil;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.core.Ordered;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author 谢进伟
 * @description 开放资源资源注解接口不鉴权处理逻辑，队请求参数进行验签(暂不支持文件上传类型请求)
 * <pre>
 * 请求参数验签规则：
 * 1、把除时间戳（timestamp）、随机字符串(randomStr)之外的所有参数按参数的key自然顺序排序。当一个参数同时传递多个值时，值用逗号分割作为一个值参与下面的额拼接
 * 2、根据排序完的key,将所有参数组装成如下格式的字符串：timestamp=客户端传入的timestamp&key1=value1&key2=value2&key3=value3.....&randomStr=客户端传入的randomStr,这一步得到字符串:str1
 * 3、将拼成的字符串str1全部转换成小写,得到字符串：str2
 * 4、将转换成的小写之后的字符串str2倒序,得到字符串：str3
 * 5、分两种情况：
 *      a、非requestBody形式请求：组合需要即将生成签名的字符串，格式：客户端传入的timestamp+第四步得到的str3+()+客户端传入的randomStr，这一步得到字符串：str4
 *      b、requestBody的形式请求：组合需要即将生成签名的字符串，格式：客户端传入的timestamp+第四步得到的str3+#请求的body字符串内容#+客户端传入的randomStr，这一步得到字符串：str4
 * 6、生成签名：使用标准MD5加密第5步获取到的字符串str4,这一步得到的32位字符串将是签名结果
 * </pre>
 * @createDate 2021/2/25 15:11
 */
@Slf4j
@Aspect
@RequiredArgsConstructor
public class CustomSecurityOpenResourceAspect implements Ordered {

    private final RemoteApplicationService remoteApplicationService;

    private final HttpServletRequest request;

    private static final String APP_ID = "appId";
    private static final String APP_SECRET = "appSecret";

    private static final String HEADER_APPLICATION_CODE = "applicationCode";
    private static final String HEADER_TIMESTAMP = "timestamp";
    private static final String HEADER_RANDOM_STR = "randomStr";
    private static final String HEADER_SIGN = "sign";

    @SneakyThrows
    @Around("@annotation(openApiResource)")
    public Object around(ProceedingJoinPoint point, OpenApiResource openApiResource) {
        String applicationCode = request.getParameter(HEADER_APPLICATION_CODE);
        String timestampStr = request.getParameter(HEADER_TIMESTAMP);
        String randomStr = request.getParameter(HEADER_RANDOM_STR);
        String sign = request.getParameter(HEADER_SIGN);
        if (StringUtils.isBlank(applicationCode) ||
                StringUtils.isBlank(timestampStr) ||
                !StringUtils.isNumeric(timestampStr) ||
                StringUtils.isBlank(randomStr) ||
                StringUtils.isBlank(sign)
        ) {
            reject(point, openApiResource, applicationCode, "参数错误!");
        }
        Long timestamp = Long.parseLong(timestampStr);
        String resourceCode = openApiResource.code();
        if (request instanceof MultipartHttpServletRequest) {
            reject(point, openApiResource, applicationCode, "不支持文件上传类型请求!");
        }
        //获取应用编码对应应用的所有挂载的资源信息
        final R<List<SysResource>> result = remoteApplicationService.getApplicationResources(applicationCode,
                SecurityConstants.FROM_IN);
        List<SysResource> resourceList = result.getData();
        if (CollectionUtils.isEmpty(resourceList)) {
            reject(point, openApiResource, applicationCode, null);
        }

        String appId = result.getExtendData(APP_ID, "").toString();
        String appSecret = result.getExtendData(APP_SECRET, "").toString();

        if (StrUtil.isBlankOrUndefined(appId) ||
                StrUtil.isBlankOrUndefined(appSecret)) {
            reject(point, openApiResource, applicationCode, "未配置appId和appSecret参数");
        }
        //判断是否挂载当前需要请求的资源到应用编码对应的应用
        AtomicBoolean exists = new AtomicBoolean(false);
        resourceList.parallelStream()
                .filter(resource -> resourceCode.equals(resource.getCode()))
                .findFirst()
                .ifPresent(resource -> exists.set(true));
        if (!exists.get()) {
            return reject(point, openApiResource, applicationCode, null);
        }
        //参数验签
        MethodSignature signature = (MethodSignature) point.getSignature();
        Method method = signature.getMethod();
        Parameter[] parameters = method.getParameters();
        String bodyStr = null;
        for (Parameter parameter : parameters) {
            // 采用RequestBody形式接收参数
            if (parameter.isAnnotationPresent(RequestBody.class)) {
                bodyStr = new RequestWrapper(request).getBodyString();
            }
        }
        Map<String, Object> paramsMap = new HashMap<>();
        request.getParameterMap().forEach((k, v) -> paramsMap.put(k, StringUtils.join(v, ",")));
        if (!SignUtil.validateSign(paramsMap, bodyStr, timestamp, randomStr, appId, appSecret, sign)) {
            reject(point, openApiResource, applicationCode, "参数验签失败!");
            //TODO 针对请求ip、资源等的请求次数限制，先不搞了。。。。
        }
        return point.proceed();
    }

    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE + 1;
    }


    /**
     * 拒绝访问
     *
     * @param point
     * @param openApiResource
     * @param applicationCode
     * @return
     */
    private Object reject(ProceedingJoinPoint point, OpenApiResource openApiResource, String applicationCode, String errorMsg) {
        String access_is_denied = "Access is denied";
        String msg = errorMsg != null ? errorMsg : access_is_denied;
        log.warn("应用：{} 未开放资源：{}=>{} , 访问接口 {} 没有权限(" + msg + ")", applicationCode,
                openApiResource.code(),
                openApiResource.description(),
                point.getSignature().getDeclaringTypeName());
        throw new AccessDeniedException(msg);
    }
}

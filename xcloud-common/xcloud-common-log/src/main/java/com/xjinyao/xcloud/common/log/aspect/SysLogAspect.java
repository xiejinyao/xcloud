package com.xjinyao.xcloud.common.log.aspect;

import cn.hutool.json.JSONUtil;
import com.xjinyao.xcloud.admin.api.entity.SysLog;
import com.xjinyao.xcloud.common.core.util.SpringContextHolder;
import com.xjinyao.xcloud.common.log.event.SysLogEvent;
import com.xjinyao.xcloud.common.log.util.LogTypeEnum;
import com.xjinyao.xcloud.common.log.util.SysLogUtils;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.web.bind.annotation.RequestBody;

import java.lang.annotation.Annotation;

/**
 * 操作日志使用spring event异步入库
 */
@Aspect
@Slf4j
public class SysLogAspect {

    @Around("@annotation(sysLog)")
    @SneakyThrows
    public Object around(ProceedingJoinPoint point, com.xjinyao.xcloud.common.log.annotation.SysLog sysLog) {
        String strClassName = point.getTarget().getClass().getName();
        String strMethodName = point.getSignature().getName();
        log.debug("[类名]:{},[方法]:{}", strClassName, strMethodName);

        MethodSignature signature = (MethodSignature) point.getSignature();


        String body = null;
        Object[] args = point.getArgs();
        if (args != null) {
            Annotation[][] parameterAnnotations = signature.getMethod().getParameterAnnotations();
            if (parameterAnnotations != null && parameterAnnotations.length > 0) {
                for (int i = 0; i < args.length; i++) {
                    Object arg = args[i];
                    if (arg == null) {
                        continue;
                    }
                    Annotation[] annotations = parameterAnnotations[i];
                    for (Annotation annotation : annotations) {
                        if (annotation instanceof RequestBody) {
                            try {
                                body = JSONUtil.toJsonStr(arg);
                            } catch (Exception e) {
                                body = arg.toString();
                            }
                            break;
                        }
                    }
                    if (StringUtils.isNotBlank(body)) {
                        //去掉空格与换行
                        body = body.replaceAll("[\\n\\r]\\s*(\")?(\\{|\\})?", "$1$2");
                        break;
                    }
                }
            }
        }

        SysLog logVo = SysLogUtils.getSysLog();
        logVo.setTitle(sysLog.value());
        logVo.setBody(body);

        // 发送异步日志事件
        Long startTime = System.currentTimeMillis();
        Object obj;

        try {
            obj = point.proceed();
        } catch (Exception e) {
            logVo.setType(LogTypeEnum.ERROR.getType());
            logVo.setException(e.getMessage());
            throw e;
        } finally {
            Long endTime = System.currentTimeMillis();
            logVo.setTime(endTime - startTime);
            SpringContextHolder.publishEvent(new SysLogEvent(logVo));
        }

        return obj;
    }
}

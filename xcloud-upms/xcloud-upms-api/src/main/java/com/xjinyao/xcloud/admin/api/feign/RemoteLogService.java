package com.xjinyao.xcloud.admin.api.feign;

import com.xjinyao.xcloud.admin.api.constants.ControllerMapping;
import com.xjinyao.xcloud.admin.api.entity.SysLog;
import com.xjinyao.xcloud.admin.api.feign.factory.RemoteLogServiceFallbackFactory;
import com.xjinyao.xcloud.common.core.constant.SecurityConstants;
import com.xjinyao.xcloud.common.core.constant.ServiceNameConstants;
import com.xjinyao.xcloud.common.core.util.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

/**
 * @date 2019/2/1
 */
@FeignClient(contextId = "remoteLogService", value = ServiceNameConstants.UMPS_SERVICE,
        path = ControllerMapping.SYS_LOG_CONTROLLER_MAPPING,
        fallbackFactory = RemoteLogServiceFallbackFactory.class)
public interface RemoteLogService {

    /**
     * 保存日志
     *
     * @param sysLog 日志实体
     * @param from   内部调用标志
     * @return succes、false
     */
    @PostMapping
    R<Boolean> saveLog(@RequestBody SysLog sysLog, @RequestHeader(SecurityConstants.FROM) String from);

}

package com.xjinyao.xcloud.admin.api.feign;

import com.xjinyao.xcloud.admin.api.constants.ControllerMapping;
import com.xjinyao.xcloud.admin.api.constants.SequenceNames;
import com.xjinyao.xcloud.admin.api.feign.factory.RemoteSysSequenceServiceFallbackFactory;
import com.xjinyao.xcloud.common.core.constant.SecurityConstants;
import com.xjinyao.xcloud.common.core.constant.ServiceNameConstants;
import com.xjinyao.xcloud.common.core.util.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(contextId = "sysSequenceService", value = ServiceNameConstants.UMPS_SERVICE,
        fallbackFactory = RemoteSysSequenceServiceFallbackFactory.class,
        path = ControllerMapping.SYS_SEQUENCE_CONTROLLER_MAPPING)
public interface RemoteSysSequenceService {

    @GetMapping("/getSequenceNum")
    R<String> getSequenceNum(@RequestParam("name") SequenceNames name,
                             @RequestHeader(SecurityConstants.FROM) String from);
}

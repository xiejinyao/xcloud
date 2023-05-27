package com.xjinyao.xcloud.admin.api.feign;

import com.xjinyao.xcloud.admin.api.constants.ControllerMapping;
import com.xjinyao.xcloud.admin.api.entity.SysApplication;
import com.xjinyao.xcloud.admin.api.entity.SysResource;
import com.xjinyao.xcloud.admin.api.feign.factory.RemoteApplicationFallbackFactory;
import com.xjinyao.xcloud.common.core.constant.SecurityConstants;
import com.xjinyao.xcloud.common.core.constant.ServiceNameConstants;
import com.xjinyao.xcloud.common.core.util.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author 谢进伟
 * @description 应用资源远程接口
 * @createDate 2021/2/25 15:58
 */
@FeignClient(contextId = "remoteApplicationResourceService", value = ServiceNameConstants.UMPS_SERVICE,
        path = ControllerMapping.SYS_APPLICATION_CONTROLLER_MAPPING,
        fallbackFactory = RemoteApplicationFallbackFactory.class)
public interface RemoteApplicationService {

    @GetMapping("/resources/{applicationCode}")
    R<List<SysResource>> getApplicationResources(@PathVariable("applicationCode") String applicationCode,
                                                 @RequestHeader(SecurityConstants.FROM) String from);

    @GetMapping("/get/{applicationCode}")
    R<SysApplication> getByApplicationCode(@PathVariable("applicationCode") String applicationCode,
                                           @RequestHeader(SecurityConstants.FROM) String from);

    @PostMapping("/save")
    R<Boolean> save(@RequestBody SysApplication application,
                    @RequestHeader(SecurityConstants.FROM) String from);

    @PutMapping("/update")
    R<Boolean> update(@RequestBody SysApplication application,
                      @RequestHeader(SecurityConstants.FROM) String from);

    @DeleteMapping("/delete/{applicationCode}")
    R<Boolean> delete(@PathVariable("applicationCode") String applicationCode,
                      @RequestHeader(SecurityConstants.FROM) String from);
}

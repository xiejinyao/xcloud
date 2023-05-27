package com.xjinyao.xcloud.admin.api.feign;

import com.xjinyao.xcloud.admin.api.constants.ControllerMapping;
import com.xjinyao.xcloud.admin.api.entity.SysDictItem;
import com.xjinyao.xcloud.admin.api.feign.factory.RemoteDictServiceFallbackFactory;
import com.xjinyao.xcloud.common.core.constant.SecurityConstants;
import com.xjinyao.xcloud.common.core.constant.ServiceNameConstants;
import com.xjinyao.xcloud.common.core.util.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;

import java.util.List;

/**
 * @date 2019/2/1
 */
@FeignClient(contextId = "remoteDictService", value = ServiceNameConstants.UMPS_SERVICE,
        path = ControllerMapping.SYS_DICT_CONTROLLER_MAPPING,
        fallbackFactory = RemoteDictServiceFallbackFactory.class)
public interface RemoteDictService {

    @GetMapping("/type/{type}")
    R<List<SysDictItem>> getDictByType(@PathVariable("type") String type,
									   @RequestHeader(SecurityConstants.FROM) String from);

}

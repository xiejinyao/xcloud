package com.xjinyao.xcloud.admin.api.feign;

import com.xjinyao.xcloud.admin.api.constants.ControllerMapping;
import com.xjinyao.xcloud.admin.api.dto.UserInfo;
import com.xjinyao.xcloud.admin.api.feign.factory.RemoteUserServiceFallbackFactory;
import com.xjinyao.xcloud.common.core.constant.SecurityConstants;
import com.xjinyao.xcloud.common.core.constant.ServiceNameConstants;
import com.xjinyao.xcloud.common.core.util.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * @date 2019/2/1
 */
@FeignClient(contextId = "remoteUserService", value = ServiceNameConstants.UMPS_SERVICE,
        path = ControllerMapping.SYS_USER_CONTROLLER_MAPPING,
        fallbackFactory = RemoteUserServiceFallbackFactory.class)
public interface RemoteUserService {

    /**
     * 根据用户名获取指定用户全部信息
     *
     * @param username 用户名
     * @param from     调用标志
     * @return R
     */
    @GetMapping("/info/{username}")
    R<UserInfo> info(@PathVariable("username") String username, @RequestHeader(SecurityConstants.FROM) String from);

    /**
     * 根据用户名和第三方id获取指定用户全部信息
     *
     * @param username     用户名
     * @param thirdPartyId 第三方身份Id
     * @param sources      第三平台编码
     * @param from         调用标志
     * @return R
     */
    @GetMapping("/info")
    R<UserInfo> info(@RequestParam("username") String username,
                     @RequestParam("thirdPartyId") String thirdPartyId,
                     @RequestParam("sources") String sources,
                     @RequestHeader(SecurityConstants.FROM) String from);
}

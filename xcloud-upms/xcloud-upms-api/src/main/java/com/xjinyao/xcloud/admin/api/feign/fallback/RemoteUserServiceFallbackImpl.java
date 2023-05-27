package com.xjinyao.xcloud.admin.api.feign.fallback;

import com.xjinyao.xcloud.admin.api.dto.UserInfo;
import com.xjinyao.xcloud.admin.api.feign.RemoteUserService;
import com.xjinyao.xcloud.common.core.util.R;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

/**
 * @date 2019/2/1
 */
@Slf4j
public class RemoteUserServiceFallbackImpl implements RemoteUserService {

    @Setter
    private Throwable cause;

    /**
     * 根据用户名获取指定用户全部信息
     *
     * @param username 用户名
     * @param from     内外标志
     * @return R
     */
    @Override
    public R<UserInfo> info(String username, String from) {
        log.error("feign 根据用户名获取指定用户全部信息失败！ username：{}", username, cause);
        return null;
    }

    /**
     * 根据用户名和第三方id获取指定用户全部信息
     *
     * @param username     用户名
     * @param thirdPartyId 第三方身份Id
     * @param sources      第三平台编码
     * @param from         调用标志
     * @return R
     */
    @Override
    public R<UserInfo> info(String username, String thirdPartyId, String sources, String from) {
        log.error("feign 根据用户名和第三方id获取指定用户全部信息失败！username：{}，thirdPartyId：{}, sources：{}", username,
                thirdPartyId, sources, cause);
        return null;
    }
}

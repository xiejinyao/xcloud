package com.xjinyao.xcloud.admin.controller;

import com.xjinyao.xcloud.admin.api.feign.RemoteTokenService;
import com.xjinyao.xcloud.common.core.constant.SecurityConstants;
import com.xjinyao.xcloud.common.core.util.R;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * @date 2018/9/4 getTokenPage 管理
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/token")
@Api(value = "token", tags = "令牌管理模块")
public class TokenController {

    private final RemoteTokenService remoteTokenService;

    /**
     * 分页token 信息
     *
     * @param params 参数集
     * @return token集合
     */
    @GetMapping("/page")
    @ApiOperation(value = "分页查询token信息", notes = "分页查询token信息")
    public R token(@RequestParam Map<String, Object> params) {
        return remoteTokenService.getTokenPage(params, SecurityConstants.FROM_IN);
    }

    /**
     * 删除
     *
     * @param id ID
     * @return success/false
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("@pms.hasPermission('sys_token_del')")
    @ApiOperation(value = "删除Token", notes = "删除Token")
    public R<Boolean> delete(@PathVariable String id) {
        return remoteTokenService.removeToken(id, SecurityConstants.FROM_IN);
    }
}

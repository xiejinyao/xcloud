package com.xjinyao.xcloud.admin.controller.innner;

import com.xjinyao.xcloud.admin.api.constants.ControllerMapping;
import com.xjinyao.xcloud.admin.api.entity.SysLog;
import com.xjinyao.xcloud.admin.service.SysLogService;
import com.xjinyao.xcloud.common.core.util.R;
import com.xjinyao.xcloud.common.security.annotation.Inner;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.annotations.ApiIgnore;

import javax.validation.Valid;

/**
 * 内部接口：日志管理
 *
 * @author 谢进伟
 * @createDate 2022/11/16 11:04
 */
@ApiIgnore
@RestController
@RequiredArgsConstructor
@RequestMapping(ControllerMapping.SYS_LOG_CONTROLLER_MAPPING)
public class InnerLogController {


    private final SysLogService sysLogService;

    /**
     * 插入日志
     *
     * @param sysLog 日志实体
     * @return success/false
     */
    @Inner
    @PostMapping
    @ApiOperation(value = "插入日志", notes = "插入日志", hidden = true)
    public R<Boolean> save(@Valid @RequestBody SysLog sysLog) {
        return R.ok(sysLogService.save(sysLog));
    }
}

package com.xjinyao.xcloud.admin.controller.innner;

import com.xjinyao.xcloud.admin.api.constants.ControllerMapping;
import com.xjinyao.xcloud.admin.api.constants.SequenceNames;
import com.xjinyao.xcloud.admin.service.ISysSequenceService;
import com.xjinyao.xcloud.common.core.util.R;
import com.xjinyao.xcloud.common.security.annotation.Inner;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.annotations.ApiIgnore;

/**
 * 内部接口：系统序列表
 *
 * @author 谢进伟
 * @createDate 2022/11/16 10:58
 */
@ApiIgnore
@RestController
@AllArgsConstructor
@RequestMapping(ControllerMapping.SYS_SEQUENCE_CONTROLLER_MAPPING)
public class InnerSysSequenceController {

    private final ISysSequenceService sysSequenceService;

    @Inner
    @ApiOperation(value = "根据name，获取自增值", notes = "获取自增值")
    @GetMapping("/getSequenceNum")
    public R<String> getSequenceNum(@RequestParam("name") SequenceNames name) {
        return R.ok(sysSequenceService.getSequenceNum(name.name()));
    }
}

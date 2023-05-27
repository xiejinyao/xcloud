package com.xjinyao.xcloud.admin.controller.innner;

import com.github.xiaoymin.knife4j.annotations.ApiSupport;
import com.xjinyao.xcloud.admin.api.constants.ControllerMapping;
import com.xjinyao.xcloud.admin.api.dto.log.SysBusinessLogAddDTO;
import com.xjinyao.xcloud.admin.api.entity.SysBusinessLog;
import com.xjinyao.xcloud.admin.api.entity.SysBusinessLog_;
import com.xjinyao.xcloud.admin.api.enums.BusinessLogTypeEnum;
import com.xjinyao.xcloud.admin.api.vo.SysBusinessLogGroupVO;
import com.xjinyao.xcloud.admin.api.vo.XSysBusinessLogVO;
import com.xjinyao.xcloud.admin.service.SysBusinessLogService;
import com.xjinyao.xcloud.common.core.util.BeanUtils;
import com.xjinyao.xcloud.common.core.util.R;
import com.xjinyao.xcloud.common.security.annotation.Inner;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import javax.validation.Valid;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 针对表【sys_business_log(业务变更日志表)】的表控制层
 *
 * @author mengjiajie
 * @createDate 2022-11-17 17:13:15
 */
@ApiIgnore
@RestController
@AllArgsConstructor
@RequestMapping(ControllerMapping.SYS_BUSINESS_LOG_CONTROLLER_MAPPING)
@ApiSupport(author = "mengjiajie", order = 1)
public class InnerSysBusinessLogController {

    private final SysBusinessLogService service;


    /**
     * 新增业务变更日志表数据
     *
     * @param addDTO 数据
     * @return 新增结果
     */
    @Inner
    @PostMapping(value = "/add")
    @ApiOperation(value = "新增业务变更日志表数据", produces = MediaType.APPLICATION_JSON_VALUE)
    public R<XSysBusinessLogVO> add(@Valid @RequestBody SysBusinessLogAddDTO addDTO) {
        SysBusinessLog entity = BeanUtils.copyPropertiesAndGetTarget(addDTO, new SysBusinessLog(),
                SysBusinessLog_.type.getProperty());
        entity.setType(addDTO.getType().getValue());
        boolean result = service.save(entity);
        if (result) {
            return R.ok(BeanUtils.copyPropertiesAndGetTarget(entity, new XSysBusinessLogVO()), "新增业务变更日志表成功!");
        }
        return R.failed("新增业务变更日志表失败");
    }

    @Inner
    @PostMapping(value = "/batch/add")
    @ApiOperation(value = "新增业务变更日志表数据", produces = MediaType.APPLICATION_JSON_VALUE)
    public R<Boolean> batchAdd(@Valid @RequestBody List<SysBusinessLogAddDTO> addDTOs) {
        List<SysBusinessLog> entityList = new ArrayList<>();
        addDTOs.forEach(add -> entityList.add(SysBusinessLog.builder()
                .projectId(add.getProjectId())
                .pkId(add.getPkId())
                .details(add.getDetails())
                .operationTime(add.getOperationTime())
                .operationUserId(add.getOperationUserId())
                .operationUserName(add.getOperationUserName())
                .createUser(add.getCreateUser())
                .title(add.getTitle())
                .result(add.getResult())
                .type(add.getType().getValue())
                .params(add.getParams())
                .build()));
        boolean result = service.saveBatch(entityList);
        if (result) {
            return R.ok(Boolean.TRUE, "新增业务变更日志表成功!");
        }
        return R.failed("新增业务变更日志表失败");
    }

    @Inner
    @GetMapping("/listByType")
    @ApiOperation(value = "根据类型查询列表", produces = MediaType.APPLICATION_JSON_VALUE)
    public R<List<SysBusinessLogGroupVO>> listByType(@Valid @RequestParam("projectId") String projectId,
                                                     @Valid @RequestParam("type") BusinessLogTypeEnum type,
                                                     @Valid @RequestParam("pkId") String pkId) {
        List<SysBusinessLog> list = service.lambdaQuery()
                .eq(SysBusinessLog::getType, type.getValue())
                .eq(SysBusinessLog::getProjectId, projectId)
                .eq(SysBusinessLog::getPkId, pkId)
                .orderByDesc(SysBusinessLog::getOperationTime)
                .list();

        List<SysBusinessLogGroupVO> resultList = new ArrayList<>();

        list.stream()
                .map(d -> BeanUtils.copyPropertiesAndGetTarget(d, new XSysBusinessLogVO()))
                .collect(Collectors.groupingBy(d -> d.getOperationTime().format(DateTimeFormatter.ISO_LOCAL_DATE)))
                .forEach((k, v) -> resultList.add(new SysBusinessLogGroupVO(k, v)));

        return R.ok(resultList.stream()
                .sorted(Comparator.comparing(SysBusinessLogGroupVO::getOperationDate).reversed())
                .collect(Collectors.toList()), "获取成功!");
    }


}

package com.xjinyao.xcloud.admin.api.feign;

import com.alibaba.fastjson.JSONObject;
import com.xjinyao.xcloud.admin.api.constants.ControllerMapping;
import com.xjinyao.xcloud.admin.api.dto.log.SysBusinessLogAddDTO;
import com.xjinyao.xcloud.admin.api.enums.BusinessLogTypeEnum;
import com.xjinyao.xcloud.admin.api.feign.factory.RemoteSysBusinessLogServiceFallbackFactory;
import com.xjinyao.xcloud.admin.api.vo.SysBusinessLogGroupVO;
import com.xjinyao.xcloud.admin.api.vo.XSysBusinessLogVO;
import com.xjinyao.xcloud.common.core.constant.SecurityConstants;
import com.xjinyao.xcloud.common.core.constant.ServiceNameConstants;
import com.xjinyao.xcloud.common.core.util.R;
import com.xjinyao.xcloud.common.core.util.StringUtils;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * @author mengjiajie
 * @description 业务变更日志
 * @createDate 2022/11/17 17:24
 */
@FeignClient(contextId = "remoteSysBusinessLogService", value = ServiceNameConstants.UMPS_SERVICE,
        path = ControllerMapping.SYS_BUSINESS_LOG_CONTROLLER_MAPPING,
        fallbackFactory = RemoteSysBusinessLogServiceFallbackFactory.class)
public interface RemoteSysBusinessLogService {

    @PostMapping(value = "/add")
    R<XSysBusinessLogVO> add(@Valid @RequestBody SysBusinessLogAddDTO addDTO,
                             @RequestHeader(SecurityConstants.FROM) String from);

    @PostMapping(value = "/batch/add")
    R<Boolean> batchAdd(@Valid @RequestBody List<SysBusinessLogAddDTO> addDTOs,
                        @RequestHeader(SecurityConstants.FROM) String from);

    @GetMapping("/listByType")
    R<List<SysBusinessLogGroupVO>> listByType(@Valid @RequestParam("projectId") String projectId,
                                              @Valid @RequestParam("type") BusinessLogTypeEnum type,
                                              @Valid @RequestParam("pkId") String pkId,
                                              @RequestHeader(SecurityConstants.FROM) String from);

    default R<XSysBusinessLogVO> add(String projectId,
                                     BusinessLogTypeEnum type,
                                     String pkId,
                                     String title,
                                     String details,
                                     String operationUserId,
                                     String operationUserName) {
        return this.add(projectId, type, pkId, title, details, operationUserId, operationUserName, null, null);
    }

    default R<XSysBusinessLogVO> add(String projectId,
                                     BusinessLogTypeEnum type,
                                     String pkId,
                                     String title,
                                     String details,
                                     String operationUserId,
                                     String operationUserName,
                                     String result) {
        return this.add(projectId, type, pkId, title, details, operationUserId, operationUserName, null, result);
    }

    default R<XSysBusinessLogVO> add(String projectId,
                                     BusinessLogTypeEnum type,
                                     String pkId,
                                     String title,
                                     String details,
                                     String operationUserId,
                                     String operationUserName,
                                     Map<String, Object> params,
                                     String result) {
        return this.add(projectId,
                type,
                pkId,
                title,
                details,
                operationUserId, operationUserName,
                Optional.ofNullable(params).orElse(Collections.emptyMap()),
                StringUtils.defaultString(result, "操作成功!"),
                SecurityConstants.FROM_IN);
    }

    default R<XSysBusinessLogVO> add(String projectId,
                                     BusinessLogTypeEnum type,
                                     String pkId,
                                     String title,
                                     String details,
                                     String operationUserId,
                                     String operationUserName,
                                     Map<String, Object> params,
                                     String result,
                                     String from) {
        return this.add(SysBusinessLogAddDTO.builder()
                .projectId(projectId)
                .type(type)
                .pkId(pkId)
                .title(title)
                .details(details)
                .operationTime(LocalDateTime.now())
                .operationUserId(operationUserId)
                .operationUserName(operationUserName)
                .createUser(operationUserId)
                .params(JSONObject.toJSONString(Optional.ofNullable(params).orElse(Collections.emptyMap())))
                .result(StringUtils.defaultString(result, "操作成功!"))
                .build(), from);
    }
}

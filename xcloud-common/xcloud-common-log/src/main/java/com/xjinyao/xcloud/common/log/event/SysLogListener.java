package com.xjinyao.xcloud.common.log.event;

import com.xjinyao.xcloud.admin.api.entity.SysLog;
import com.xjinyao.xcloud.admin.api.feign.RemoteLogService;
import com.xjinyao.xcloud.common.core.constant.SecurityConstants;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.core.annotation.Order;
import org.springframework.scheduling.annotation.Async;

/**
 *
 */
@Slf4j
@RequiredArgsConstructor
public class SysLogListener {

    private final RemoteLogService remoteLogService;

    @Async
    @Order
    @EventListener(SysLogEvent.class)
    public void saveSysLog(SysLogEvent event) {
        SysLog sysLog = (SysLog) event.getSource();
        remoteLogService.saveLog(sysLog, SecurityConstants.FROM_IN);
    }

}

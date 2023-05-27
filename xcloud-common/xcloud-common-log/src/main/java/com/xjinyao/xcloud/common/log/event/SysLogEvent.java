package com.xjinyao.xcloud.common.log.event;

import com.xjinyao.xcloud.admin.api.entity.SysLog;
import org.springframework.context.ApplicationEvent;

/**
 *
 */
public class SysLogEvent extends ApplicationEvent {

    public SysLogEvent(SysLog source) {
        super(source);
    }

}

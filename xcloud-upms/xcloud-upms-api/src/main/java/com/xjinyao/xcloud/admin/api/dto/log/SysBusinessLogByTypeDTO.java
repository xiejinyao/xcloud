package com.xjinyao.xcloud.admin.api.dto.log;

import com.xjinyao.xcloud.admin.api.enums.BusinessLogTypeEnum;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.Tolerate;

import java.io.Serializable;

/**
 * @author mengjiajie
 * @description
 * @createDate 2023/2/1 16:44
 */
@Data
@Builder
public class SysBusinessLogByTypeDTO implements Serializable {

    private String projectId;
    private BusinessLogTypeEnum typ;
    private String pkId;

    @Tolerate
    public SysBusinessLogByTypeDTO() {
    }
}

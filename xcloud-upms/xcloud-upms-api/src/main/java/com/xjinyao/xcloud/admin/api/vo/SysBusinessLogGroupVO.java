package com.xjinyao.xcloud.admin.api.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * @author mengjiajie
 * @description
 * @createDate 2023/2/1 15:41
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class SysBusinessLogGroupVO implements Serializable {
    private String operationDate;

    private List<XSysBusinessLogVO> logs;

}

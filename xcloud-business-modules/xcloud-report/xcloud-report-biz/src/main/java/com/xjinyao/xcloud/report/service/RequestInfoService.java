package com.xjinyao.xcloud.report.service;

import com.xjinyao.xcloud.report.vo.RequestInfoVO;

import java.util.List;
import java.util.Map;

/**
 * @author liwei
 * @createDate 2023-4-21 16:32
 */
public interface RequestInfoService {
    /**
     * 获取请求头信息
     *
     * @param dsName
     * @param datasetName
     * @param parameters
     * @return
     */
    List<RequestInfoVO> getRequestInfoVO(String dsName, String datasetName, Map<String, Object> parameters);
}

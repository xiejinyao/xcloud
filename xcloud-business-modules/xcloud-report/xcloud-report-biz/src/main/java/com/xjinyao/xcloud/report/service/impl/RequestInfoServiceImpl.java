package com.xjinyao.xcloud.report.service.impl;

import cn.hutool.core.util.ObjectUtil;
import com.xjinyao.xcloud.common.core.util.RequestHolder;
import com.xjinyao.xcloud.common.security.service.CustomRemoteTokenServices;
import com.xjinyao.xcloud.common.security.service.CustomUser;
import com.xjinyao.xcloud.common.security.util.SecurityUtils;
import com.xjinyao.xcloud.report.provider.JdbcReportProvider;
import com.xjinyao.xcloud.report.service.RequestInfoService;
import com.xjinyao.xcloud.report.vo.RequestInfoVO;
import lombok.AllArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * @author liwei
 * @createDate 2023-4-21 16:33
 */
@AllArgsConstructor
@Service("requestInfoService")
public class RequestInfoServiceImpl implements RequestInfoService {
    private final CustomRemoteTokenServices remoteTokenServices;
    private final JdbcReportProvider jdbcReportProvider;

    @Override
    public List<RequestInfoVO> getRequestInfoVO(String dsName, String datasetName, Map<String, Object> parameters) {
        RequestInfoVO requestInfoVO = new RequestInfoVO();
        String referer = RequestHolder.getHeaderValue("Referer");
        if (ObjectUtil.isEmpty(referer)) {
            referer = "";
        }
        String host = RequestHolder.getHeaderValue("Host");
        if (ObjectUtil.isNotEmpty(host)) {
            requestInfoVO.setReferer(host);
            String domain = "";
            if (referer.startsWith("http://")) {
                domain = domain.concat("http://");
            } else {
                domain = domain.concat("https://");
            }
            domain = domain.concat(host).concat("/xreport/preview");
            requestInfoVO.setDomain(domain);
        }
        String requestAuValue = jdbcReportProvider.getRequestAuValue();
        if (StringUtils.isNotBlank(requestAuValue)) {
            CustomUser user = SecurityUtils.getUser(remoteTokenServices.loadAuthentication(
                    requestAuValue, true));
            requestInfoVO.setLoginUserName(user.getUsername());
        }
        return List.of(requestInfoVO);
    }
}

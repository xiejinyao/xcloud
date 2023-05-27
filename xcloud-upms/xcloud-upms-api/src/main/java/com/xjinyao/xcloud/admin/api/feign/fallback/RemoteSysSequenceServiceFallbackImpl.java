package com.xjinyao.xcloud.admin.api.feign.fallback;

import com.xjinyao.xcloud.admin.api.constants.SequenceNames;
import com.xjinyao.xcloud.admin.api.feign.RemoteSysSequenceService;
import com.xjinyao.xcloud.common.core.util.R;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

/**
 * @author ：lyl
 * @date ：Created in 2021/3/30 19:05
 * @description：
 * @modified By：
 */
@Slf4j
public class RemoteSysSequenceServiceFallbackImpl implements RemoteSysSequenceService {

    @Setter
    private Throwable cause;

    @Override
    public R<String> getSequenceNum(SequenceNames name, String from) {
        log.error("feign 查询自增值信息失败:{}", name, cause);
        return R.failed(null);
    }
}

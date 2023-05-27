package com.xjinyao.xcloud.admin.api.feign.fallback;

import com.xjinyao.xcloud.admin.api.dto.log.SysBusinessLogAddDTO;
import com.xjinyao.xcloud.admin.api.enums.BusinessLogTypeEnum;
import com.xjinyao.xcloud.admin.api.feign.RemoteSysBusinessLogService;
import com.xjinyao.xcloud.admin.api.vo.SysBusinessLogGroupVO;
import com.xjinyao.xcloud.admin.api.vo.XSysBusinessLogVO;
import com.xjinyao.xcloud.common.core.util.R;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.util.Collections;
import java.util.List;

/**
 * @date 2019/2/1
 */
@Slf4j
public class RemoteSysBusinessLogServiceFallbackImpl implements RemoteSysBusinessLogService {

	@Setter
	private Throwable cause;

	@Override
	public R<XSysBusinessLogVO> add(SysBusinessLogAddDTO addDTO, String fro) {
		log.error("feign 新增业务日志失败:{}", addDTO, cause);
		return R.failed();
	}

	@Override
	public R<Boolean> batchAdd(List<SysBusinessLogAddDTO> addDTOs, String fro) {
		log.error("feign 批量新增业务日志失败:{}", addDTOs, cause);
		return R.failed();
	}

	@Override
	public R<List<SysBusinessLogGroupVO>> listByType(String projectId,
	                                                 BusinessLogTypeEnum type,
	                                                 String pkId,
	                                                 String fro) {
		log.error("feign 查询业务日志失败:{}->{}->{}", type, projectId, pkId, cause);
		return R.failed(Collections.emptyList());
	}
}

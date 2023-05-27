package com.xjinyao.xcloud.admin.api.feign.fallback;

import com.xjinyao.xcloud.admin.api.entity.SysDictItem;
import com.xjinyao.xcloud.admin.api.feign.RemoteDictService;
import com.xjinyao.xcloud.common.core.util.R;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.util.Collections;
import java.util.List;

/**
 * @date 2019/2/1
 */
@Slf4j
public class RemoteDictServiceFallbackImpl implements RemoteDictService {

	@Setter
	private Throwable cause;

	@Override
	public R<List<SysDictItem>> getDictByType(String type, String from) {
		log.error("feign 插入日志失败", cause);
		return R.ok(Collections.emptyList());
	}
}

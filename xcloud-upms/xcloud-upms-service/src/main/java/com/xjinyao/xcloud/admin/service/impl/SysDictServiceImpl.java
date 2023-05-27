package com.xjinyao.xcloud.admin.service.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xjinyao.xcloud.admin.api.entity.SysDict;
import com.xjinyao.xcloud.admin.api.entity.SysDictItem;
import com.xjinyao.xcloud.admin.mapper.SysDictItemMapper;
import com.xjinyao.xcloud.admin.mapper.SysDictMapper;
import com.xjinyao.xcloud.admin.service.SysDictItemService;
import com.xjinyao.xcloud.admin.service.SysDictService;
import com.xjinyao.xcloud.common.core.constant.enums.DictTypeEnum;
import com.xjinyao.xcloud.common.core.redis.constant.CacheConstants;
import com.xjinyao.xcloud.common.core.util.StringUtils;
import com.xjinyao.xcloud.common.security.util.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

/**
 * 字典表
 *
 * @date 2019/03/19
 */
@Service
@RequiredArgsConstructor
public class SysDictServiceImpl extends ServiceImpl<SysDictMapper, SysDict> implements SysDictService {

	private final SysDictItemMapper dictItemMapper;
	private final SysDictItemService sysDictItemService;

	/**
	 * 根据ID 删除字典
	 *
	 * @param id 字典ID
	 * @return
	 */
	@Override
	@CacheEvict(value = CacheConstants.DICT_DETAILS, allEntries = true)
	@Transactional(rollbackFor = Exception.class)
	public void removeDict(Integer id) {
		if (!SecurityUtils.isSuperAdmin()) {
			SysDict dict = this.getById(id);
			// 系统内置
			Assert.state(!DictTypeEnum.SYSTEM.getType().equals(dict.getSystem()), "系统内置字典项目不能删除");
		}
		baseMapper.deleteById(id);
		dictItemMapper.delete(Wrappers.<SysDictItem>lambdaQuery().eq(SysDictItem::getDictId, id));
	}

	/**
	 * 更新字典
	 *
	 * @param dict 字典
	 * @return
	 */
	@Override
	public void updateDict(SysDict dict) {
		if (!SecurityUtils.isSuperAdmin()) {
			SysDict sysDict = this.getById(dict.getId());
			// 系统内置
			Assert.state(!DictTypeEnum.SYSTEM.getType().equals(sysDict.getSystem()), "系统内置字典项目不能修改");
			if (!StringUtils.equals(dict.getType(), sysDict.getType())) {
				sysDictItemService.lambdaUpdate()
						.eq(SysDictItem::getType, sysDict.getType())
						.set(SysDictItem::getType, dict.getType())
						.update();
			}
		}
		this.updateById(dict);
	}

}

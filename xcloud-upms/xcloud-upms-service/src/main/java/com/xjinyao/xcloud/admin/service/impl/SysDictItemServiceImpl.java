package com.xjinyao.xcloud.admin.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xjinyao.xcloud.admin.api.entity.SysDict;
import com.xjinyao.xcloud.admin.api.entity.SysDictItem;
import com.xjinyao.xcloud.admin.mapper.SysDictItemMapper;
import com.xjinyao.xcloud.admin.mapper.SysDictMapper;
import com.xjinyao.xcloud.admin.service.SysDictItemService;
import com.xjinyao.xcloud.common.core.constant.enums.DictTypeEnum;
import com.xjinyao.xcloud.common.core.redis.constant.CacheConstants;
import com.xjinyao.xcloud.common.core.util.R;
import com.xjinyao.xcloud.common.security.util.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

/**
 * 字典项
 *
 * @date 2019/03/19
 */
@Service
@RequiredArgsConstructor
public class SysDictItemServiceImpl extends ServiceImpl<SysDictItemMapper, SysDictItem> implements SysDictItemService {

	private final SysDictMapper dictMapper;

	/**
	 * 删除字典项
	 *
	 * @param id 字典项ID
	 * @return
	 */
	@Override
	@CacheEvict(value = CacheConstants.DICT_DETAILS, allEntries = true)
	public R removeDictItem(Integer id) {
		if (!SecurityUtils.isSuperAdmin()) {
			// 根据ID查询字典ID
			SysDictItem dictItem = this.getById(id);
			SysDict dict = dictMapper.selectById(dictItem.getDictId());
			// 系统内置
			Assert.state(!DictTypeEnum.SYSTEM.getType().equals(dict.getSystem()), "系统内置字典项目不能删除");
		}
		return R.ok(this.removeById(id));
	}

	/**
	 * 更新字典项
	 *
	 * @param item 字典项
	 * @return
	 */
	@Override
	@CacheEvict(value = CacheConstants.DICT_DETAILS, allEntries = true)
	public R updateDictItem(SysDictItem item) {
		if (!SecurityUtils.isSuperAdmin()) {
			// 查询字典
			SysDict dict = dictMapper.selectById(item.getDictId());
			// 系统内置
			Assert.state(!DictTypeEnum.SYSTEM.getType().equals(dict.getSystem()), "系统内置字典项目不能修改");
		}
		return R.ok(this.updateById(item));
	}

}

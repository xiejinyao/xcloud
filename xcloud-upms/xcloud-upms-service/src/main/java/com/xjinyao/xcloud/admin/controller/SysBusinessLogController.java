package com.xjinyao.xcloud.admin.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.xiaoymin.knife4j.annotations.ApiSupport;
import com.xjinyao.xcloud.admin.api.dto.log.SysBusinessLogAddDTO;
import com.xjinyao.xcloud.admin.api.dto.log.SysBusinessLogSearchDTO;
import com.xjinyao.xcloud.admin.api.dto.log.SysBusinessLogUpdateDTO;
import com.xjinyao.xcloud.admin.api.entity.SysBusinessLog;
import com.xjinyao.xcloud.admin.api.vo.XSysBusinessLogVO;
import com.xjinyao.xcloud.admin.service.SysBusinessLogService;
import com.xjinyao.xcloud.common.core.util.BeanUtils;
import com.xjinyao.xcloud.common.core.util.R;
import com.xjinyao.xcloud.common.mybatis.pagination.PageDTO;
import com.xjinyao.xcloud.common.mybatis.pagination.XPageParam;
import com.xjinyao.xcloud.common.mybatis.wrappers.HightQueryWrapper;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.List;

/**
 * 针对表【sys_business_log(业务日志表)】的表控制层
 *
 * @author 谢进伟
 * @createDate 2023-01-31 17:27:09
 */
@Api(tags = "业务日志表")
@RestController
@AllArgsConstructor
@RequestMapping("/sysBusinessLog")
@ApiSupport(author = "谢进伟", order = 1)
public class SysBusinessLogController {

	private final SysBusinessLogService service;

	/**
	 * 条件分页查询业务日志表信息
	 *
	 * @param page      分页参数
	 * @param condition 搜索条件
	 * @return 列表数据
	 */
	@GetMapping("/page")
	@ApiOperation(value = "条件分页查询列表查询", produces = MediaType.APPLICATION_JSON_VALUE)
	@PreAuthorize("@pms.hasPermission('sys_business_log_page')")
	public R<Page<XSysBusinessLogVO>> page(XPageParam page, SysBusinessLogSearchDTO condition, HttpServletRequest request) {
		Page<XSysBusinessLogVO> resultPage = service.page(PageDTO.of(page.getCurrent(), page.getPageSize(), page.getOrders()),
				HightQueryWrapper.build(SysBusinessLog.class, condition, request.getParameterMap()), XSysBusinessLogVO.class);
		return R.ok(resultPage, "获取成功!");
	}





	/**
	 * 通过主键查询单条业务日志表数据
	 *
	 * @param id 主键
	 * @return 单条数据
	 */
	@GetMapping("/get/{id}")
	@ApiOperation(value = "通过主键查询单条业务日志表数据", produces = MediaType.APPLICATION_JSON_VALUE)
	@PreAuthorize("@pms.hasPermission('sys_business_log_get_by_id')")
	public R<XSysBusinessLogVO> getById(@PathVariable("id") Long id) {
		SysBusinessLog entity = service.lambdaQuery()
				.eq(SysBusinessLog::getId, id)
				.oneOpt()
				.orElseThrow(() -> new RuntimeException("数据不存在!"));
		return R.ok(BeanUtils.copyPropertiesAndGetTarget(entity, new XSysBusinessLogVO()), "查询成功!");
	}

	/**
	 * 新增业务日志表数据
	 *
	 * @param addDTO 数据
	 * @return 新增结果
	 */
	@PostMapping(value = "/add")
	@ApiOperation(value = "新增业务日志表数据", produces = MediaType.APPLICATION_JSON_VALUE)
	@PreAuthorize("@pms.hasPermission('sys_business_log_add')")
	public R add(@Valid @RequestBody SysBusinessLogAddDTO addDTO) {
		SysBusinessLog entity = BeanUtils.copyPropertiesAndGetTarget(addDTO, new SysBusinessLog());
		boolean result = service.save(entity);
		if (result) {
			return R.ok(BeanUtils.copyPropertiesAndGetTarget(entity, new XSysBusinessLogVO()), "新增业务日志表成功!");
		}
		return R.failed("新增业务日志表失败");
	}

	/**
	 * 通过主键修改单条业务日志表数据
	 *
	 * @param updateDTO 主键
	 * @return 修改结果
	 */
	@PutMapping(value = "/update")
	@ApiOperation(value = "通过主键修改单条业务日志表数据", produces = MediaType.APPLICATION_JSON_VALUE)
	@PreAuthorize("@pms.hasPermission('sys_business_log_update')")
	public R update(@Valid @RequestBody SysBusinessLogUpdateDTO updateDTO) {
		if (updateDTO.getId() != null) {
			if (!this.service.lambdaQuery().eq(SysBusinessLog::getId, updateDTO.getId()).exists()) {
				throw new RuntimeException("数据不存在!");
			}
			SysBusinessLog entity = BeanUtils.copyPropertiesAndGetTarget(updateDTO, new SysBusinessLog());
			boolean result = service.updateById(entity);
			if (result) {
				return R.ok(Boolean.TRUE, "更新业务日志表成功!");
			}
		}
		return R.failed("更新业务日志表失败");
	}

	/**
	 * 通过主键删除单条业务日志表数据
	 *
	 * @param id 主键
	 * @return 删除结果
	 */
	@DeleteMapping("deleteById")
	@ApiOperation(value = "通过主键删除单条业务日志表数据", produces = MediaType.APPLICATION_JSON_VALUE)
	@PreAuthorize("@pms.hasPermission('sys_business_log_delete_by_id')")
	public R<Boolean> deleteById(@RequestParam("id") Long id) {
		if (id != null && !this.service.lambdaQuery().eq(SysBusinessLog::getId, id).exists()) {
			throw new RuntimeException("数据不存在!");
		}
		if (this.service.removeById(id)) {
			return R.ok(Boolean.TRUE, "删除业务日志表成功!");
		}
		return R.failed("删除业务日志表失败");
	}

	/**
	 * 通过主键批量删除业务日志表数据
	 *
	 * @param ids 主键
	 * @return 删除结果
	 */
	@DeleteMapping("batchDeleteByIds")
	@ApiOperation(value = "通过主键删除单条业务日志表数据", produces = MediaType.APPLICATION_JSON_VALUE)
	@PreAuthorize("@pms.hasPermission('sys_business_log_batch_delete_by_id')")
	public R<Boolean> batchDeleteByIds(@RequestBody List<Long> ids) {
		if (this.service.removeByIds(ids)) {
			return R.ok(Boolean.TRUE, "删除业务日志表成功!");
		}
		return R.failed("删除业务日志表失败");
	}
}

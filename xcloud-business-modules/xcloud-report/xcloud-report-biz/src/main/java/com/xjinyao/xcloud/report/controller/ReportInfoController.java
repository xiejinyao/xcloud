package com.xjinyao.xcloud.report.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.xiaoymin.knife4j.annotations.ApiSupport;
import com.xjinyao.xcloud.common.core.util.BeanUtils;
import com.xjinyao.xcloud.common.core.util.R;
import com.xjinyao.xcloud.common.core.util.RequestHolder;
import com.xjinyao.xcloud.common.mybatis.pagination.PageDTO;
import com.xjinyao.xcloud.common.mybatis.pagination.XPageParam;
import com.xjinyao.xcloud.common.mybatis.wrappers.HightQueryWrapper;
import com.xjinyao.xcloud.report.dto.ReportInfoSearchDTO;
import com.xjinyao.xcloud.report.dto.ReportInfoUpdateDTO;
import com.xjinyao.xcloud.report.dto.ReportInfoUpdateSearchFormConfigDTO;
import com.xjinyao.xcloud.report.entity.ReportInfo;
import com.xjinyao.xcloud.report.service.ReportInfoService;
import com.xjinyao.xcloud.report.vo.XReportInfoVO;
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
 * 针对表【sys_report_info(报表信息)】的表控制层
 *
 * @author 谢进伟
 * @createDate 2023-02-28 14:53:43
 */
@Api(tags = "报表信息")
@RestController
@AllArgsConstructor
@RequestMapping("/reportInfo")
@ApiSupport(author = "谢进伟", order = 1)
public class ReportInfoController {

	private final ReportInfoService service;

	/**
	 * 条件分页查询报表信息信息
	 *
	 * @param page      分页参数
	 * @param condition 搜索条件
	 * @return 列表数据
	 */
	@GetMapping("/page")
	@ApiOperation(value = "条件分页查询列表查询", produces = MediaType.APPLICATION_JSON_VALUE)
	@PreAuthorize("@pms.hasPermission('sys_report_info_page')")
	public R<Page<XReportInfoVO>> page(XPageParam page, ReportInfoSearchDTO condition, HttpServletRequest request) {
		String projectId = RequestHolder.getHeaderValue("projectId");
		HightQueryWrapper<ReportInfo> wrapper = HightQueryWrapper.build(ReportInfo.class, condition, request.getParameterMap());
		wrapper.lambda().select(ReportInfo::getId,
				ReportInfo::getProjectId,
				ReportInfo::getType,
				ReportInfo::getName,
				ReportInfo::getFileName,
				ReportInfo::getDescription,
				ReportInfo::getIsTemplate,
				ReportInfo::getVisible,
				ReportInfo::getPreviewImmediatelyLoad,
				ReportInfo::getVersion,
				ReportInfo::getCreateUser,
				ReportInfo::getCreateUserName,
				ReportInfo::getCreateTime,
				ReportInfo::getUpdateUser,
				ReportInfo::getUpdateUserName,
				ReportInfo::getUpdateTime);
		wrapper.lambda().eq(ReportInfo::getVisible, true)
				.and(q -> q.eq(ReportInfo::getProjectId, projectId)
						.or(query -> query.isNull(ReportInfo::getProjectId)
								.eq(ReportInfo::getIsTemplate, true)));
		Page<XReportInfoVO> resultPage = service.page(PageDTO.of(page.getCurrent(), page.getPageSize(), page.getOrders()),
				wrapper, XReportInfoVO.class);
		return R.ok(resultPage, "获取成功!");
	}

	/**
	 * 通过主键查询单条报表信息数据
	 *
	 * @param id 主键
	 * @return 单条数据
	 */
	@GetMapping("/get/{id}")
	@ApiOperation(value = "通过主键查询单条报表信息数据", produces = MediaType.APPLICATION_JSON_VALUE)
	@PreAuthorize("@pms.hasPermission('sys_report_info_get_by_id')")
	public R<XReportInfoVO> getById(@PathVariable("id") Integer id) {
		ReportInfo entity = service.lambdaQuery().select(ReportInfo::getId,
						ReportInfo::getProjectId,
						ReportInfo::getType,
						ReportInfo::getName,
						ReportInfo::getFileName,
						ReportInfo::getDescription,
						ReportInfo::getIsTemplate,
						ReportInfo::getVisible,
						ReportInfo::getPreviewImmediatelyLoad,
						ReportInfo::getVersion,
						ReportInfo::getPreviewParamsDeclarationConfig,
						ReportInfo::getSearchFormConfig,
						ReportInfo::getCreateUser,
						ReportInfo::getCreateUserName,
						ReportInfo::getCreateTime,
						ReportInfo::getUpdateUser,
						ReportInfo::getUpdateUserName,
						ReportInfo::getUpdateTime)
				.eq(ReportInfo::getId, id)
				.oneOpt()
				.orElseThrow(() -> new RuntimeException("数据不存在!"));
		return R.ok(BeanUtils.copyPropertiesAndGetTarget(entity, new XReportInfoVO()), "查询成功!");
	}

	/**
	 * 通过主键修改单条报表信息数据
	 *
	 * @param updateDTO 主键
	 * @return 修改结果
	 */
	@PutMapping(value = "/update")
	@ApiOperation(value = "通过主键修改单条报表信息数据", produces = MediaType.APPLICATION_JSON_VALUE)
	@PreAuthorize("@pms.hasPermission('sys_report_info_update')")
	public R update(@Valid @RequestBody ReportInfoUpdateDTO updateDTO) {
		if (updateDTO.getId() != null) {
			if (!this.service.lambdaQuery().eq(ReportInfo::getId, updateDTO.getId()).exists()) {
				throw new RuntimeException("数据不存在!");
			}
			ReportInfo entity = BeanUtils.copyPropertiesAndGetTarget(updateDTO, new ReportInfo());
			boolean result = service.updateById(entity);
			if (result) {
				return R.ok(Boolean.TRUE, "更新报表信息成功!");
			}
		}
		return R.failed("更新报表信息失败");
	}

	/**
	 * 修改报表搜索表单配置
	 *
	 * @param updateDTO 主键
	 * @return 修改结果
	 */
	@PutMapping(value = "/updateSearchFormConfig")
	@ApiOperation(value = "修改报表搜索表单配置", produces = MediaType.APPLICATION_JSON_VALUE)
	@PreAuthorize("@pms.hasPermission('sys_report_info_update_search_form_config')")
	public R updateSearchFormConfig(@Valid @RequestBody ReportInfoUpdateSearchFormConfigDTO updateDTO) {
		if (updateDTO.getId() != null) {
			if (!this.service.lambdaQuery().eq(ReportInfo::getId, updateDTO.getId()).exists()) {
				throw new RuntimeException("数据不存在!");
			}
			boolean result = service.lambdaUpdate()
					.eq(ReportInfo::getId, updateDTO.getId())
					.set(ReportInfo::getSearchFormConfig, updateDTO.getSearchFormConfig())
					.update();
			if (result) {
				return R.ok(Boolean.TRUE, "更新成功!");
			}
		}
		return R.failed("更新失败");
	}

	/**
	 * 通过主键删除单条报表信息数据
	 *
	 * @param id 主键
	 * @return 删除结果
	 */
	@DeleteMapping("deleteById")
	@ApiOperation(value = "通过主键删除单条报表信息数据", produces = MediaType.APPLICATION_JSON_VALUE)
	@PreAuthorize("@pms.hasPermission('sys_report_info_delete_by_id')")
	public R<Boolean> deleteById(@RequestParam("id") Integer id) {
		if (id != null && !this.service.lambdaQuery().eq(ReportInfo::getId, id).exists()) {
			throw new RuntimeException("数据不存在!");
		}
		if (this.service.removeById(id)) {
			return R.ok(Boolean.TRUE, "删除报表信息成功!");
		}
		return R.failed("删除报表信息失败");
	}

	/**
	 * 通过主键批量删除报表信息数据
	 *
	 * @param ids 主键
	 * @return 删除结果
	 */
	@DeleteMapping("batchDeleteByIds")
	@ApiOperation(value = "通过主键删除单条报表信息数据", produces = MediaType.APPLICATION_JSON_VALUE)
	@PreAuthorize("@pms.hasPermission('sys_report_info_batch_delete_by_id')")
	public R<Boolean> batchDeleteByIds(@RequestBody List<Integer> ids) {
		if (this.service.removeByIds(ids)) {
			return R.ok(Boolean.TRUE, "删除报表信息成功!");
		}
		return R.failed("删除报表信息失败");
	}
}

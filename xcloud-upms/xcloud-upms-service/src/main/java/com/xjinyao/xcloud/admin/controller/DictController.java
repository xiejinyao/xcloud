package com.xjinyao.xcloud.admin.controller;

import cn.hutool.core.io.IoUtil;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.extension.conditions.query.LambdaQueryChainWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xjinyao.xcloud.admin.api.dto.GenDictEnumDto;
import com.xjinyao.xcloud.admin.api.entity.SysDict;
import com.xjinyao.xcloud.admin.api.entity.SysDictItem;
import com.xjinyao.xcloud.admin.api.entity.SysDict_;
import com.xjinyao.xcloud.admin.service.SysDictItemService;
import com.xjinyao.xcloud.admin.service.SysDictService;
import com.xjinyao.xcloud.common.core.redis.constant.CacheConstants;
import com.xjinyao.xcloud.common.core.util.R;
import com.xjinyao.xcloud.common.core.util.StringUtils;
import com.xjinyao.xcloud.common.log.annotation.SysLog;
import com.xjinyao.xcloud.common.mybatis.wrappers.HightQueryWrapper;
import com.xjinyao.xcloud.common.security.annotation.OpenApiResource;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateExceptionHandler;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.apache.commons.io.IOUtils;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpHeaders;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.util.ResourceUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.StringWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;

import static org.springframework.util.ResourceUtils.CLASSPATH_URL_PREFIX;

/**
 * <p>
 * 字典表 前端控制器
 * </p>
 *
 * @since 2019-03-19
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/dict")
@Api(value = "dict", tags = "字典管理模块")
public class DictController {

	private final SysDictItemService sysDictItemService;

	private final SysDictService sysDictService;

	/**
	 * 通过ID查询字典信息
	 *
	 * @param id ID
	 * @return 字典信息
	 */
	@ApiOperation(value = "通过ID查询字典信息", notes = "通过ID查询字典信息")
	@GetMapping("/{id}")
	public R<SysDict> getById(@PathVariable Integer id) {
		return R.ok(sysDictService.getById(id));
	}

	/**
	 * 分页查询字典信息
	 *
	 * @param page 分页对象
	 * @return 分页对象
	 */
	@GetMapping("/page")
	@ApiOperation(value = "分页查询字典信息", notes = "分页查询字典信息")
	public R<Page<SysDict>> getDictPage(Page page, SysDict sysDict, HttpServletRequest request) {
		return R.ok(sysDictService.page(page, HightQueryWrapper.wrapper(sysDict, request.getParameterMap())
				.orderByDesc(SysDict_.createTime.getColumn())));
	}

	/**
	 * 通过字典类型查找字典
	 *
	 * @param type 类型
	 * @return 同类型字典
	 */
	@GetMapping("/type/{type}")
	@ApiOperation(value = "通过字典类型查找字典", notes = "通过字典类型查找字典")
	@Cacheable(value = CacheConstants.DICT_DETAILS,
			key = "'getDictByType_' + #type + '_excludeDisabledItem_' + #excludeDisabledItem")
	public R<List<SysDictItem>> getDictByType(
			@PathVariable String type,
			@RequestParam(required = false, defaultValue = "true") Boolean excludeDisabledItem) {
		return R.ok(getDicts(Collections.singletonList(type), excludeDisabledItem));
	}


	/**
	 * 通过字典类型查找字典
	 *
	 * @param type 类型
	 * @return 同类型字典
	 */
	@GetMapping("/open/type/{type}")
	@ApiOperation(value = "通过字典类型查找字典（open）", notes = "通过字典类型查找字典（open）")
	@OpenApiResource(code = "dict_by_type", title = "通过字典类型查找字典", description = "通过字典类型查找字典")
	public R<List<SysDictItem>> getOpenDictByType(
			@PathVariable String type,
			@RequestParam(required = false, defaultValue = "true") Boolean excludeDisabledItem) {
		return getDictByType(type, excludeDisabledItem);
	}

	/**
	 * 通过字典类型查找字典，可一次查询多个类型的字典
	 *
	 * @param types 类型
	 * @return 同类型字典
	 */
	@GetMapping("/types")
	@ApiOperation(value = "通过字典类型查找字典，可一次查询多个类型的字典", notes = "通过字典类型查找字典，可一次查询多个类型的字典")
	@Cacheable(value = CacheConstants.DICT_DETAILS,
			key = "'getDictByTypes_'+#types + '_excludeDisabledItem_' + #excludeDisabledItem")
	public R<Map<String, List<SysDictItem>>> getDictByTypes(
			@RequestParam List<String> types,
			@RequestParam(required = false, defaultValue = "true") Boolean excludeDisabledItem) {
		Map<String, List<SysDictItem>> result = new HashMap<>();

		List<SysDictItem> list = getDicts(types, excludeDisabledItem);

		if (list != null && !list.isEmpty()) {
			result.putAll(list.parallelStream().collect(Collectors.groupingBy(SysDictItem::getType)));
		}

		return R.ok(result);
	}

	/**
	 * 添加字典
	 *
	 * @param sysDict 字典信息
	 * @return success、false
	 */
	@SysLog("添加字典")
	@PostMapping
	@PreAuthorize("@pms.hasPermission('sys_dict_add')")
	@ApiOperation(value = "添加字典", notes = "添加字典")
	public R<Boolean> save(@Valid @RequestBody SysDict sysDict) {
		return R.ok(sysDictService.save(sysDict));
	}

	/**
	 * 删除字典，并且清除字典缓存
	 *
	 * @param id ID
	 * @return R
	 */
	@SysLog("删除字典")
	@DeleteMapping("/{id}")
	@PreAuthorize("@pms.hasPermission('sys_dict_del')")
	@ApiOperation(value = "删除字典", notes = "删除字典")
	@CacheEvict(value = CacheConstants.DICT_DETAILS, allEntries = true)
	public R<Boolean> removeById(@PathVariable Integer id) {
		sysDictService.removeDict(id);
		return R.ok();
	}

	/**
	 * 修改字典
	 *
	 * @param sysDict 字典信息
	 * @return success/false
	 */
	@PutMapping
	@SysLog("修改字典")
	@PreAuthorize("@pms.hasPermission('sys_dict_edit')")
	@ApiOperation(value = "修改字典", notes = "修改字典")
	@CacheEvict(value = CacheConstants.DICT_DETAILS, allEntries = true)
	public R<Boolean> updateById(@Valid @RequestBody SysDict sysDict) {
		sysDictService.updateDict(sysDict);
		return R.ok();
	}

	/**
	 * 生成枚举
	 *
	 * @param id id 字典id
	 * @return R
	 */
	@SneakyThrows
	@GetMapping("/gen/enum/{id}")
	@ApiOperation(value = "生成枚举", notes = "生成枚举")
	public void genDictEnum(@PathVariable("id") Integer id,
							@RequestParam String packageName,
							@RequestParam String enumFileName,
							@RequestParam String itemDataType,
							HttpServletResponse response) {
		SysDict sysDict = sysDictService.getById(id);

		String fileName = enumFileName.endsWith(".java") ? enumFileName : enumFileName + ".java";
		String fileContent = this.genEnumFileContent(sysDict, packageName, enumFileName, itemDataType);

		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		IOUtils.write(fileContent, outputStream, StandardCharsets.UTF_8);
		byte[] data = outputStream.toByteArray();

		response.reset();
		response.setHeader(HttpHeaders.CONTENT_DISPOSITION,
				String.format("attachment; filename=%s", fileName));
		response.addHeader(HttpHeaders.CONTENT_LENGTH, String.valueOf(data.length));
		response.setContentType("text/plain; charset=UTF-8");
		IoUtil.write(response.getOutputStream(), Boolean.TRUE, data);
	}

	/**
	 * 预览枚举文件内容
	 *
	 * @param id id 字典id
	 * @return R
	 */
	@SneakyThrows
	@GetMapping("/gen/enum/preview/{id}")
	@ApiOperation(value = "生成枚举", notes = "生成枚举")
	public R<String> previewDictEnum(@PathVariable("id") Integer id,
									 @RequestParam String packageName,
									 @RequestParam String enumFileName,
									 @RequestParam String itemDataType) {
		SysDict sysDict = sysDictService.getById(id);
		return R.ok(this.genEnumFileContent(sysDict, packageName, enumFileName, itemDataType));
	}


	/**
	 * 分页查询
	 *
	 * @param page        分页对象
	 * @param sysDictItem 字典项
	 * @return
	 */
	@GetMapping("/item/page")
	@ApiOperation(value = "分页查询字典项信息", notes = "分页查询字典项信息")
	public R<Page<SysDictItem>> getSysDictItemPage(Page page, SysDictItem sysDictItem, HttpServletRequest request) {
		return R.ok(sysDictItemService.page(page, HightQueryWrapper.wrapper(sysDictItem, request.getParameterMap())));
	}

	/**
	 * 通过id查询字典项
	 *
	 * @param id id
	 * @return R
	 */
	@GetMapping("/item/{id}")
	@ApiOperation(value = "通过id查询字典项", notes = "通过id查询字典项")
	public R<SysDictItem> getDictItemById(@PathVariable("id") Integer id) {
		return R.ok(sysDictItemService.getById(id));
	}

	/**
	 * 新增字典项
	 *
	 * @param sysDictItem 字典项
	 * @return R
	 */
	@SysLog("新增字典项")
	@PostMapping("/item")
	@PreAuthorize("@pms.hasPermission('sys_dict_item_add')")
	@CacheEvict(value = CacheConstants.DICT_DETAILS, allEntries = true)
	@ApiOperation(value = "新增字典项", notes = "新增字典项")
	public R<Boolean> save(@RequestBody SysDictItem sysDictItem) {
		return R.ok(sysDictItemService.save(sysDictItem));
	}

	/**
	 * 修改字典项
	 *
	 * @param sysDictItem 字典项
	 * @return R
	 */
	@SysLog("修改字典项")
	@PutMapping("/item")
	@ApiOperation(value = "修改字典项", notes = "修改字典项")
	@PreAuthorize("@pms.hasPermission('sys_dict_item_edit')")
	@CacheEvict(value = CacheConstants.DICT_DETAILS, allEntries = true)
	public R<Boolean> updateById(@RequestBody SysDictItem sysDictItem) {
		return sysDictItemService.updateDictItem(sysDictItem);
	}

	/**
	 * 通过id删除字典项
	 *
	 * @param id id
	 * @return R
	 */
	@SysLog("删除字典项")
	@DeleteMapping("/item/{id}")
	@PreAuthorize("@pms.hasPermission('sys_dict_item_delete')")
	@ApiOperation(value = "删除字典项", notes = "删除字典项")
	@CacheEvict(value = CacheConstants.DICT_DETAILS, allEntries = true)
	public R<Boolean> removeDictItemById(@PathVariable Integer id) {
		return sysDictItemService.removeDictItem(id);
	}

	private List<SysDictItem> getDicts(List<String> type, Boolean excludeDisabledItem) {
		LambdaQueryChainWrapper<SysDict> sysDictLambdaQueryChainWrapper = sysDictService.lambdaQuery();
		if (excludeDisabledItem) {
			sysDictLambdaQueryChainWrapper.eq(SysDict::getEnabled, true);
		}
		List<Integer> dictIdList = Optional.ofNullable(sysDictLambdaQueryChainWrapper
						.select(SysDict::getId, SysDict::getType)
						.in(SysDict::getType, type)
						.list())
				.orElse(Collections.emptyList())
				.stream()
				.map(SysDict::getId)
				.collect(Collectors.toList());
		if (CollectionUtils.isEmpty(dictIdList)) {
			return Collections.emptyList();
		}
		LambdaQueryChainWrapper<SysDictItem> sysDictItemLambdaQueryChainWrapper = sysDictItemService.lambdaQuery();
		if (excludeDisabledItem) {
			sysDictItemLambdaQueryChainWrapper.eq(SysDictItem::getEnabled, true);
		}
		return Optional.ofNullable(sysDictItemLambdaQueryChainWrapper
						.in(SysDictItem::getDictId, dictIdList).list())
				.orElse(Collections.emptyList())
				.stream()
				.sorted(Comparator.comparing(SysDictItem::getSort))
				.collect(Collectors.toList());
	}

	private String genEnumFileContent(SysDict dictionaryGroup, String packageName, String enumFileName,
									  String itemDataType) {
		Integer groupId = dictionaryGroup.getId();
		String groupName = dictionaryGroup.getDescription();
		String code = dictionaryGroup.getType();
		List<SysDictItem> dictionaries = sysDictItemService.lambdaQuery().eq(SysDictItem::getDictId, groupId).list();
		if (StringUtils.isNotBlank(groupName) && StringUtils.isNotBlank(code)
				&& CollectionUtils.isNotEmpty(dictionaries)) {
			GenDictEnumDto domainInfo = new GenDictEnumDto();
			domainInfo.setDict(dictionaryGroup);
			domainInfo.setPackageName(packageName);
			domainInfo.setEnumFileName(enumFileName);
			domainInfo.setDictItems(dictionaries);
			domainInfo.setItemDataType(itemDataType);
			domainInfo.setEncoding(StandardCharsets.UTF_8.name());
			return replaceForTemplate(domainInfo);
		}
		return "生成失败!";
	}

	private String replaceForTemplate(GenDictEnumDto domainInfo) {
		try {
			File templateDir = ResourceUtils.getFile(CLASSPATH_URL_PREFIX + "template");
			Configuration cfg = new Configuration(freemarker.template.Configuration.VERSION_2_3_22);
			cfg.setDirectoryForTemplateLoading(templateDir);
			String defaultTemplateName = "dict_2_enum.ftl.java";
			cfg.setDefaultEncoding(domainInfo.getEncoding());
			cfg.setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER);
			Template templateName = cfg.getTemplate(defaultTemplateName);
			Writer writer = new StringWriter();
			Map<String, Object> map = new HashMap<>();
			map.put("domain", domainInfo);
			templateName.process(map, writer);
			return writer.toString();
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}


//    public static void main(String[] args) {
//
//        List<SysDictItem> dictItems = new ArrayList<>();
//        dictItems.add(new SysDictItem() {{
//            this.setId(1);
//            this.setEnumCode("YES");
//            this.setLabel("是");
//            this.setValue("1");
//            this.setRemark("是的");
//        }});
//        dictItems.add(new SysDictItem() {{
//            this.setId(2);
//            this.setEnumCode("NO");
//            this.setLabel("否");
//            this.setValue("0");
//            this.setRemark("否定");
//        }});
//
//        GenDictEnumDto genDictEnumDto = new GenDictEnumDto();
//        genDictEnumDto.setDict(new SysDict() {{
//            this.setId(1);
//            this.setDescription("测试分组");
//            this.setRemark("测试备注");
//            this.setType("yes_no");
//        }});
//        genDictEnumDto.setEncoding(StandardCharsets.UTF_8.name());
//        genDictEnumDto.setPackageName("com.xcloud.charge.api.enums");
//        genDictEnumDto.setEnumFileName("YesOrNoEnum");
//        genDictEnumDto.setDictItems(dictItems);
//        genDictEnumDto.setItemDataType("Integer");
//        String replace = replace(genDictEnumDto);
//        System.out.println(replace);
//    }

}

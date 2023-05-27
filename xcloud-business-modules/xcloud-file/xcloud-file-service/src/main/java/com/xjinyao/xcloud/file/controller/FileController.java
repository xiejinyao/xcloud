package com.xjinyao.xcloud.file.controller;

import cn.hutool.core.lang.UUID;
import cn.hutool.core.util.NumberUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xjinyao.xcloud.common.core.util.JarUtil;
import com.xjinyao.xcloud.common.core.util.R;
import com.xjinyao.xcloud.common.core.util.SpringContextHolder;
import com.xjinyao.xcloud.common.log.annotation.SysLog;
import com.xjinyao.xcloud.common.mybatis.wrappers.HightQueryWrapper;
import com.xjinyao.xcloud.common.security.annotation.Inner;
import com.xjinyao.xcloud.file.api.entity.SysFile;
import com.xjinyao.xcloud.file.api.vo.SysFileVO;
import com.xjinyao.xcloud.file.properties.FileUploadProperties;
import com.xjinyao.xcloud.file.service.IFileService;
import com.xjinyao.xcloud.file.store.FileStore;
import com.xjinyao.xcloud.file.store.FileStoreType;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;

/**
 * 文件
 *
 * @author 谢进伟
 * @date 2020-05-15 14:49:59
 */
@Slf4j
@RestController
@RequestMapping("/")
@Api(value = "/", tags = "文件管理")
public class FileController {

	private final IFileService fileService;
	private final FileUploadProperties fileUploadConfig;

	public FileController(IFileService fileService,
						  FileUploadProperties fileUploadConfig) {
		this.fileService = fileService;
		this.fileUploadConfig = fileUploadConfig;
	}

	/**
	 * 分页查询
	 *
	 * @param page 分页对象
	 * @param file 文件
	 * @return
	 */
	@ApiOperation(value = "分页查询", notes = "分页查询")
	@GetMapping("/page")
	public R<Page<SysFile>> getFilePage(Page page, SysFile file, HttpServletRequest request) {
		return R.ok(fileService.page(page, HightQueryWrapper.wrapper(file, request.getParameterMap())));
	}

	/**
	 * 通过id集合查询文件
	 *
	 * @param fileIdList
	 * @return R
	 */
	@GetMapping("/getFiles")
	@ApiOperation(value = "通过id集合查询文件", notes = "通过id集合查询文件，同时查询多个文件信息")
	public R<List<SysFileVO>> getFiles(@RequestParam(value = "fileIdList") List<Integer> fileIdList) {
		List<SysFileVO> list = new ArrayList<>();
		List<SysFile> entitys = fileService.listByIds(fileIdList);
		if (entitys != null && !entitys.isEmpty()) {
			entitys.forEach(entity -> {
				if (entity != null) {
					list.add(entity.convertToSysFileVO());
				}
			});
		}
		return R.ok(list);
	}

	/**
	 * 通过id集合查询文件
	 *
	 * @param fileIdList
	 * @return R
	 */
	@Inner
	@ApiOperation(value = "通过id集合查询文件", notes = "通过id集合查询文件", hidden = true)
	@GetMapping("/inner/getFiles")
	public R<List<SysFileVO>> getFilesForInner(@RequestParam(value = "fileIdList") List<Integer> fileIdList) {
		return getFiles(fileIdList);
	}

	/**
	 * 通过id查询文件
	 *
	 * @param id id
	 * @return R
	 */
	@ApiOperation(value = "通过id查询", notes = "通过id查询")
	@GetMapping("/{id}")
	public R<SysFileVO> getById(@PathVariable("id") String id) {
		if (NumberUtil.isNumber(id)) {
			SysFile entity = fileService.getById(id);
			if (entity != null) {
				return R.ok(entity.convertToSysFileVO());
			} else {
				return R.failed(null, "文件不存在");
			}
		} else {
			return R.ok();
		}
	}

	/**
	 * 通过id查询文件
	 *
	 * @param id id
	 * @return R
	 */
	@Inner
	@ApiOperation(value = "通过id查询文件", notes = "通过id查询文件", hidden = true)
	@GetMapping("/inner/{id}")
	public R<SysFileVO> getByIdForInner(@PathVariable("id") String id) {
		return getById(id);
	}

	/**
	 * 修改文件
	 *
	 * @param file 文件
	 * @return R
	 */
	@ApiOperation(value = "修改文件", notes = "修改文件")
	@SysLog("修改文件")
	@PutMapping
	@PreAuthorize("@pms.hasPermission('admin_file_edit')")
	public R<Boolean> updateById(@RequestBody SysFile file) {
		return R.ok(fileService.updateById(file));
	}

	/**
	 * 通过id删除文件
	 *
	 * @param id id
	 * @return R
	 */
	@ApiOperation(value = "通过id删除文件（慎用）")
	@SysLog("通过id删除文件")
	@DeleteMapping("/delete/{id}")
	public R<Boolean> deleteFile(@PathVariable Integer id) {
		SysFile sysFile = fileService.getById(id);
		if (sysFile == null) {
			return R.failed("文件数据不存在，无法删除！");
		}
		boolean data = fileService.removeById(id);
		String storeType = sysFile.getStoreType();
		if (data && StringUtils.isNoneBlank(storeType) && Arrays.stream(FileStoreType.values())
				.map(FileStoreType::toString)
				.anyMatch(d -> d.equals(storeType))) {
			Optional.of(SpringContextHolder.getBeans(FileStore.class))
					.orElse(Collections.emptyList())
					.stream()
					.filter(d -> d.getFileStoreType().equals(FileStoreType.valueOf(storeType)))
					.forEach(d -> d.delete(sysFile.getRelativePath()));
		}
		return R.ok(data);
	}

	/**
	 * 通过id删除文件
	 *
	 * @param id id
	 * @return R
	 */
	@Inner
	@ApiOperation(value = "通过id删除文件", notes = "通过id删除文件", hidden = true)
	@DeleteMapping("/inner/delete/{id}")
	public R<Boolean> deleteFileForInner(@PathVariable Integer id) {
		return deleteFile(id);
	}

	/**
	 * 通过id集合删除文件
	 *
	 * @param fileIdList
	 * @return R
	 */

	@ApiOperation(value = "通过id集合删除文件（慎用）")
	@SysLog("通过id集合删除文件")
	@DeleteMapping("/deleteFiles")
	public R<Boolean> deleteFiles(@RequestParam(value = "fileIdList") List<Integer> fileIdList) {
		fileIdList.forEach(id -> deleteFile(id));
		return R.ok();
	}

	/**
	 * 通过id集合删除文件
	 *
	 * @param fileIdList
	 * @return R
	 */
	@Inner
	@ApiOperation(value = "通过id集合删除文件", notes = "通过id集合删除文件", hidden = true)
	@DeleteMapping("/inner/delete")
	public R<Boolean> deleteFilesForInner(@RequestParam(value = "fileIdList") List<Integer> fileIdList) {
		return deleteFiles(fileIdList);
	}

	/**
	 * 读取jar包中 META-INF/MANIFEST.MF 的内容
	 *
	 * @param id 文件Id
	 * @return R
	 */
	@Inner
	@ApiOperation(value = "读取jar包中 META-INF/MANIFEST.MF 的内容", notes = "读取jar包中 META-INF/MANIFEST.MF 的内容", hidden = true)
	@GetMapping("/inner/getJarFileManifest/{id}")
	public R<Map<String, String>> getJarFileManifest(@PathVariable("id") Integer id) {
		SysFile sysFile = fileService.getById(id);
		if (sysFile == null) {
			return R.failed(Collections.emptyMap(), "没有查询到文件上传记录!");
		}
		String filetype = sysFile.getFileType();
		if (!"jar".equals(filetype)) {
			return R.failed(Collections.emptyMap(), "文件不是jar包文件!");
		}
		AtomicReference<Map<String, String>> resultMap = new AtomicReference<>(Collections.emptyMap());
		fileService.handler(sysFile, fileStore -> {
			if (fileStore.exists(sysFile.getRelativePath())) {
				File tempFile = null;
				String tempDirectoryPath = FileUtils.getTempDirectoryPath();
				String prefix = "temp_" + UUID.randomUUID();
				String suffix = ".jar";
				try (FileOutputStream outputStream = new FileOutputStream(tempDirectoryPath + File.separator
						+ prefix + suffix)) {
					tempFile = File.createTempFile(prefix, suffix);
					fileStore.download(sysFile.getRelativePath(), outputStream);
					resultMap.set(JarUtil.readJarManifestFile(tempFile.getPath()));
				} catch (IOException e) {
					e.printStackTrace();
				} finally {
					FileUtils.deleteQuietly(tempFile);
				}
			}
		});
		return R.ok(resultMap.get());
	}

	/**
	 * 检查jar包中是否存在指定的文件
	 *
	 * @param id 文件Id
	 * @return R
	 */
	@Inner
	@ApiOperation(value = "检查jar包中是否存在指定的文件", notes = "检查jar包中是否存在指定的文件", hidden = true)
	@GetMapping("/inner/checkJarFileContainsFile/{id}")
	public R<Boolean> checkJarFileContainsFile(@PathVariable("id") Integer id,
											   @RequestParam("filePath") String filePath) {
		SysFile sysFile = fileService.getById(id);
		if (sysFile == null) {
			return R.failed(Boolean.FALSE, "没有查询到文件上传记录!");
		}
		String filetype = sysFile.getFileType();
		if (!"jar".equals(filetype)) {
			return R.failed(Boolean.FALSE, "文件不是jar包文件!");
		}
		AtomicReference<Boolean> result = new AtomicReference<>(false);
		fileService.handler(sysFile, fileStore -> {
			if (fileStore.exists(sysFile.getRelativePath())) {
				File tempFile = null;
				String tempDirectoryPath = FileUtils.getTempDirectoryPath();
				String prefix = "temp_" + UUID.randomUUID();
				String suffix = ".jar";
				try (FileOutputStream outputStream = new FileOutputStream(tempDirectoryPath + File.separator
						+ prefix + suffix)) {
					tempFile = File.createTempFile(prefix, suffix);
					fileStore.download(sysFile.getRelativePath(), outputStream);
					result.set(JarUtil.containsName(tempFile.getPath(), filePath));
				} catch (IOException e) {
					e.printStackTrace();
				} finally {
					FileUtils.deleteQuietly(tempFile);
				}
			}
		});
		return R.ok(result.get());
	}
}

package com.xjinyao.xcloud.file.controller;

import cn.hutool.core.collection.CollectionUtil;
import com.alibaba.fastjson.JSONArray;
import com.xjinyao.xcloud.common.core.util.R;
import com.xjinyao.xcloud.common.core.util.SpringContextHolder;
import com.xjinyao.xcloud.common.log.annotation.SysLog;
import com.xjinyao.xcloud.common.security.annotation.Inner;
import com.xjinyao.xcloud.common.security.service.CustomUser;
import com.xjinyao.xcloud.common.security.util.SecurityUtils;
import com.xjinyao.xcloud.file.api.dto.UploadExtFileInfoDTO;
import com.xjinyao.xcloud.file.api.entity.SysFile;
import com.xjinyao.xcloud.file.api.vo.FileUploadSuccessVO;
import com.xjinyao.xcloud.file.api.vo.SysFileVO;
import com.xjinyao.xcloud.file.api.vo.UploadFileBaseInfoVO;
import com.xjinyao.xcloud.file.service.IFileService;
import com.xjinyao.xcloud.file.store.FileStore;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.util.MimeTypeUtils;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import java.io.IOException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author 谢进伟
 * @description 文件上传
 * @createDate 2020/5/18 19:08
 */
@Slf4j
@Controller
@AllArgsConstructor
@RequestMapping("/")
@Api(value = "/", tags = "文件上传管理")
public class FileUploadController {

	private final IFileService fileService;

	/**
	 * 上传
	 *
	 * @param file        文件
	 * @param extFileInfo ext文件信息
	 * @return {@link R}<{@link UploadFileBaseInfoVO}>
	 */
	@SneakyThrows
	@ResponseBody
	@SysLog("上传文件")
	@ApiOperation(value = "上传文件", notes = "上传文件", httpMethod = "POST")
	@RequestMapping(
			value = "upload",
			method = RequestMethod.POST,
			consumes = "multipart/*",
			headers = {"content-type=multipart/form-data", "content-type=application/json"},
			produces = MimeTypeUtils.APPLICATION_JSON_VALUE
	)
	public R<UploadFileBaseInfoVO> upload(@ApiParam("上传文件") MultipartFile file, UploadExtFileInfoDTO extFileInfo) {
		List<SysFileVO> fileVoList = new ArrayList<>();
		upload(file, extFileInfo, fileVoList, null, true);
		if (fileVoList.size() > 0) {
			SysFileVO vo = fileVoList.get(0);
			return R.ok(new UploadFileBaseInfoVO(vo.getId(), vo.getOriginalName(), vo.getUrl()));
		}
		return R.failed("上传失败！");
	}

	/**
	 * 多部分上传
	 *
	 * @param multipartFiles 多部分文件
	 * @param extFileInfos   ext文件信息
	 * @return {@link R}<{@link FileUploadSuccessVO}>
	 */
	@SneakyThrows
	@ResponseBody
	@SysLog("多文件上传")
	@ApiOperation(value = "多文件上传", notes = "多文件上传（注意:请不要使用swagger测试该接口）", httpMethod = "POST")
	@RequestMapping(
			value = "multipart_upload",
			method = RequestMethod.POST,
			consumes = "multipart/*",
			headers = "content-type=multipart/form-data",
			produces = MimeTypeUtils.APPLICATION_JSON_VALUE
	)
	public R<FileUploadSuccessVO> multipartUpload(@ApiParam("多文件上传文件")
												  MultipartFile[] multipartFiles,
												  @ApiParam("上传文件扩展信息,与上传文件一一对应")
												  @RequestHeader String extFileInfos) {
		if (multipartFiles == null) {
			return R.failed("请选择需要上传的文件!");
		}
		List<SysFileVO> fileVoList = new ArrayList<>();
		List<UploadExtFileInfoDTO> uploadExtFileInfos = null;
		if (extFileInfos != null) {
			String decode = URLDecoder.decode(extFileInfos, StandardCharsets.UTF_8);
			uploadExtFileInfos = JSONArray.parseArray(decode, UploadExtFileInfoDTO.class);
		}
		for (int i = 0; i < multipartFiles.length; i++) {
			MultipartFile multipartFile = multipartFiles[i];
			assert uploadExtFileInfos != null;
			UploadExtFileInfoDTO extFileInfo = uploadExtFileInfos.get(i);
			upload(multipartFile, extFileInfo, fileVoList, null, true);
		}
		return R.ok(setUploadSuccessInfo(fileVoList));
	}

	/**
	 * 多部分文件上传
	 *
	 * @param request      请求
	 * @param extFileInfos ext文件信息
	 * @return {@link R}<{@link FileUploadSuccessVO}>
	 */
	@ResponseBody
	@SysLog("多文件上传文件(支持文件域名多个情况)")
	@ApiOperation(value = "多文件上传文件(支持文件域名多个情况)", notes = "多文件上传文件(支持文件域名多个情况)", httpMethod = "POST")
	@RequestMapping(
			value = "multipart_files_upload",
			method = RequestMethod.POST,
			consumes = "multipart/*",
			headers = {"content-type=multipart/form-data", "content-type=application/json"},
			produces = MimeTypeUtils.APPLICATION_JSON_VALUE
	)
	public R<FileUploadSuccessVO> multipartFileUpload(@ApiParam("多文件上传文件") MultipartHttpServletRequest request,
													  @RequestHeader(required = false) String extFileInfos) {
		List<SysFileVO> fileVoList = new ArrayList<>();


		Map<String, UploadExtFileInfoDTO> map = new HashMap<>();
		if (StringUtils.isNotBlank(extFileInfos)) {
			String decode = URLDecoder.decode(extFileInfos, StandardCharsets.UTF_8);
			List<UploadExtFileInfoDTO> uploadExtFileInfos = JSONArray.parseArray(decode, UploadExtFileInfoDTO.class);
			if (uploadExtFileInfos != null) {
				uploadExtFileInfos.forEach(u -> map.put(u.getFileInputName(), u));
			}
		}

		MultiValueMap<String, MultipartFile> multiFileMap = request.getMultiFileMap();
		multiFileMap.forEach((k, v) -> {
			UploadExtFileInfoDTO extFileInfo = map.get(k);
			v.forEach(multipartFile -> upload(multipartFile, extFileInfo, fileVoList,
					null, true));
		});
		return R.ok(setUploadSuccessInfo(fileVoList));
	}

	/**
	 * 上传文件到服务器dir
	 * 上传文件到服务器指定目录
	 *
	 * @param multipartFile 多部分文件
	 * @param relativePath  相对路径
	 * @param isSaveLog     保存日志
	 * @return {@link R}<{@link Collection}<{@link SysFileVO}>>
	 */
	@ResponseBody
	@SysLog("上传文件到服务器指定目录")
	@ApiOperation(value = "上传文件到服务器指定目录", notes = "上传文件到服务器指定目录(不会产生数据库文件记录)", httpMethod = "POST")
	@RequestMapping(
			value = "uploadFileToServerDir",
			method = RequestMethod.POST,
			consumes = "multipart/*",
			headers = {"content-type=multipart/form-data", "content-type=application/json"},
			produces = MimeTypeUtils.APPLICATION_JSON_VALUE
	)
	public R<Collection<SysFileVO>> uploadFileToServerDir(
			@RequestPart("file") MultipartFile multipartFile,
			String relativePath,
			@RequestParam(required = false, defaultValue = "false") Boolean isSaveLog) {
		List<SysFileVO> sysFileVOList = new ArrayList<>();
		List<FileStore.UploadResult> results = upload(multipartFile, null, sysFileVOList, relativePath,
				isSaveLog);
		if (isSaveLog) {
			return R.ok(sysFileVOList, "上传成功!");
		} else {
			List<SysFileVO> collect = Optional.of(results)
					.orElse(Collections.emptyList())
					.stream()
					.map(d -> {
						SysFileVO sysFileVO = new SysFileVO();
						sysFileVO.setUrl(d.getUrl());
						return sysFileVO;
					}).collect(Collectors.toList());
			return R.ok(collect, "上传成功!");
		}
	}

	/**
	 * 上传文件到服务器dir项目
	 * 上传文件到服务器指定目录
	 *
	 * @param multipartFile 多部分文件
	 * @param relativePath  相对路径
	 * @param isSaveLog     保存日志
	 * @return {@link R}<{@link Collection}<{@link SysFileVO}>>
	 */
	@Inner
	@ResponseBody
	@ApiOperation(value = "上传文件到服务器指定目录", notes = "上传文件到服务器指定目录", hidden = true)
	@SysLog("上传文件到服务器指定目录")
	@RequestMapping(
			value = "/inner/uploadFileToServerDirProject",
			method = RequestMethod.POST,
			consumes = "multipart/*",
			headers = {"content-type=multipart/form-data", "content-type=application/json"},
			produces = MimeTypeUtils.APPLICATION_JSON_VALUE
	)
	public R<Collection<SysFileVO>> uploadFileToServerDirProject(
			@RequestPart("file") MultipartFile multipartFile,
			String relativePath,
			@RequestParam(required = false, defaultValue = "false") Boolean isSaveLog) {
		return uploadFileToServerDir(multipartFile, relativePath, isSaveLog);
	}


	/**
	 * 上传文件
	 *
	 * @param multipartFile      上传文件对象
	 * @param extFileInfo        扩展信息
	 * @param fileVOList         上传成功转换成vo对象集合
	 * @param customRelativePath 自定义存储目录
	 * @param isSaveLog          是否将文件信息存储到数据库
	 */
	private List<FileStore.UploadResult> upload(MultipartFile multipartFile,
												UploadExtFileInfoDTO extFileInfo,
												List<SysFileVO> fileVOList,
												String customRelativePath, boolean isSaveLog) {
		List<FileStore.UploadResult> resultList = new ArrayList<>();
		Collection<FileStore> stores = SpringContextHolder.getBeans(FileStore.class);
		stores.forEach(fileStore -> {
			try {
				FileStore.UploadResult result = fileStore.upload(multipartFile, customRelativePath);
				if (result == null) {
					return;
				}
				if (isSaveLog) {
					String originalFilename = multipartFile.getOriginalFilename();
					String fileType = StringUtils.substringAfterLast(originalFilename, ".");
					String relativePath = result.getRelativePath();
					long fileSize = result.getFileSize();
					String url = result.getUrl();
					String prefix = result.getPrefix();

					CustomUser user = SecurityUtils.getUser();
					SysFile sysFile = new SysFile();
					sysFile.setStoreType(fileStore.getFileStoreType().name());
					sysFile.setOriginalName(originalFilename);
					sysFile.setCustomName(extFileInfo != null ? extFileInfo.getCustomName() : null);
					sysFile.setCreateTime(LocalDateTime.now());
					sysFile.setCreateUserId(user != null ? user.getId() : null);
					sysFile.setBusinessCode(extFileInfo != null ? extFileInfo.getBusinessCode() : null);
					sysFile.setFileType(fileType);
					sysFile.setRelativePath(StringUtils.replace(relativePath, "\\", "/"));
					sysFile.setFileSize((double) fileSize);
					sysFile.setPrefix(prefix);
					sysFile.setUrl(url);

					boolean save = fileService.save(sysFile);
					if (save) {
						fileVOList.add(sysFile.convertToSysFileVO());
					}
				}
				resultList.add(result);
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		});
		return resultList;
	}

	private FileUploadSuccessVO setUploadSuccessInfo(List<SysFileVO> fileVoList) {
		List<Integer> fileIdList = new ArrayList<>();
		List<UploadFileBaseInfoVO> infos = new ArrayList<>();
		if (CollectionUtil.isNotEmpty(fileVoList)) {
			fileVoList.forEach(vo -> {
				fileIdList.add(vo.getId());
				infos.add(new UploadFileBaseInfoVO(vo.getId(), vo.getOriginalName(), vo.getUrl()));
			});
		}
		FileUploadSuccessVO fileUploadSuccessVO = new FileUploadSuccessVO();
		fileUploadSuccessVO.setIds(fileIdList);
		fileUploadSuccessVO.setInfos(infos);
		return fileUploadSuccessVO;
	}
}

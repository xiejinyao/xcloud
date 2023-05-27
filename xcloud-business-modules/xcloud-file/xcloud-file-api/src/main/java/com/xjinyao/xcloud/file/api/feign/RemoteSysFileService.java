package com.xjinyao.xcloud.file.api.feign;

import com.alibaba.fastjson.JSON;
import com.xjinyao.xcloud.common.core.annotations.SysFileInfo;
import com.xjinyao.xcloud.common.core.constant.SecurityConstants;
import com.xjinyao.xcloud.common.core.constant.ServiceNameConstants;
import com.xjinyao.xcloud.common.core.util.NumberUtils;
import com.xjinyao.xcloud.common.core.util.R;
import com.xjinyao.xcloud.file.api.feign.factory.RemotSysFileServiceFallbackFactory;
import com.xjinyao.xcloud.file.api.vo.SysFileVO;
import feign.form.spring.SpringFormEncoder;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.boot.autoconfigure.http.HttpMessageConverters;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.cloud.openfeign.support.SpringEncoder;
import org.springframework.context.annotation.Bean;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.lang.reflect.Field;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author 谢进伟
 * @description 文件服务
 * @createDate 2020/5/22 18:46
 */
@FeignClient(contextId = "remoteSysFileService", value = ServiceNameConstants.FILE_SERVICE,
		fallbackFactory = RemotSysFileServiceFallbackFactory.class,
		configuration = {RemoteSysFileService.MultiPartSupportConfiguration.class})
public interface RemoteSysFileService {

	@GetMapping("/inner/getJarFileManifest/{id}")
	R<Map<String, String>> getJarFileManifest(@PathVariable("id") Integer id,
											  @RequestHeader(SecurityConstants.FROM) String from);

	@GetMapping("/inner/checkJarFileContainsFile/{id}")
	R<Boolean> checkJarFileContainsFile(@PathVariable("id") Integer id,
										@RequestParam("filePath") String filePath,
										@RequestHeader(SecurityConstants.FROM) String from);

	@GetMapping("/inner/{id}")
	R<SysFileVO> getFile(@PathVariable("id") Integer id,
						 @RequestHeader(SecurityConstants.FROM) String from);


	@GetMapping("/inner/getFiles")
	R<List<SysFileVO>> getFiles(@RequestParam(value = "fileIdList") Collection<Integer> fileIdList,
								@RequestHeader(SecurityConstants.FROM) String from);


	@DeleteMapping("/inner/delete/{id}")
	R<Boolean> deleteFile(@PathVariable("id") Integer id,
						  @RequestHeader(SecurityConstants.FROM) String from);

	@DeleteMapping("/inner/delete")
	R<Boolean> deleteFiles(@RequestParam(value = "fileIdList") Collection<Integer> fileIdList,
						   @RequestHeader(SecurityConstants.FROM) String from);

	@PostMapping(value = "/inner/uploadFileToServerDirProject", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	R<Collection<SysFileVO>> uploadFileToServerDir(@RequestPart("file") MultipartFile multipartFile,
												   @RequestParam(value = "relativePath") String relativePath,
												   @RequestParam(value = "isSaveLog") Boolean isSaveLog,
												   @RequestHeader(SecurityConstants.FROM) String from);


	/**
	 * 填充文件信息
	 *
	 * @param source
	 * @param <S>
	 * @return
	 */
	default <S> S fillFileInfos(S source) {
		if (source instanceof List && ((Collection<?>) source).size() > 0) {
			Class<?> cls = ((Collection<?>) source).iterator().next().getClass();
			List<Field> fieldList = FieldUtils.getFieldsListWithAnnotation(cls, SysFileInfo.class);
			((Collection<?>) source).forEach(obj -> getFileInfo(cls, fieldList, obj));
		} else {
			Class<?> cls = source.getClass();
			List<Field> fieldList = FieldUtils.getFieldsListWithAnnotation(cls, SysFileInfo.class);
			getFileInfo(cls, fieldList, source);
		}
		return source;
	}

	private void getFileInfo(Class<?> cls, List<Field> fieldList, Object obj) {
		String separator = ",";
		Map<Field, Set<Integer>> fieldListMap = new HashMap<>();

		Set<Integer> fileIdList = Optional.ofNullable(fieldList).orElse(Collections.emptyList())
				.stream()
				.map(field -> {
					String targetFieldName = field.getAnnotation(SysFileInfo.class).value();
					if (StringUtils.isBlank(targetFieldName)) {
						return null;
					}
					Field targetField = FieldUtils.getDeclaredField(cls, targetFieldName, true);
					if (targetField == null) {
						return null;
					}
					Object fileIdObj;
					try {
						fileIdObj = targetField.get(obj);
					} catch (IllegalAccessException e) {
						throw new RuntimeException(e);
					}
					if (fileIdObj != null) {
						String fileIdStr = fileIdObj.toString();
						if (NumberUtils.isNumber(fileIdStr)) {
							Set<Integer> value = Collections.singleton(Integer.parseInt(fileIdStr));
							fieldListMap.put(field, value);
							return value;
						} else {
							if (fileIdStr.contains(separator)) {
								String[] fileIds = StringUtils.split(fileIdStr, separator);
								Set<Integer> collect = Arrays.stream(fileIds)
										.map(Integer::parseInt)
										.collect(Collectors.toSet());
								fieldListMap.put(field, collect);
								return collect;
							}
						}
					}
					return null;
				})
				.filter(Objects::nonNull)
				.flatMap(Collection::stream)
				.collect(Collectors.toSet());
		if (CollectionUtils.isNotEmpty(fileIdList)) {
			R<List<SysFileVO>> result = getFiles(fileIdList, SecurityConstants.FROM_IN);
			if (result != null && result.getData() != null) {
				List<SysFileVO> fileVOList = result.getData();
				if (CollectionUtils.isNotEmpty(fileVOList)) {
					fieldListMap.forEach((field, fileIds) -> {
						field.setAccessible(true);
						try {
							if (field.getType().equals(SysFileVO.class)) {
								if (fileVOList.size() > 1) {
									if (field.getType().isAssignableFrom(List.class)) {
										field.set(obj, fileVOList);
									} else {
										field.set(obj, JSON.toJSONString(fileVOList));
									}
								} else {
									field.set(obj, fileVOList.get(0));
								}
							} else {
								List<String> netUrls = fileVOList.stream().filter(f -> fileIds.contains(f.getId()))
										.map(SysFileVO::getUrl)
										.collect(Collectors.toList());
								if (CollectionUtils.isNotEmpty(netUrls)) {
									if (netUrls.size() > 1) {
										if (field.getType().isAssignableFrom(List.class)) {
											field.set(obj, netUrls);
										} else {
											field.set(obj, JSON.toJSONString(netUrls));
										}
									} else {
										field.set(obj, netUrls.get(0));
									}
								}
							}
						} catch (IllegalAccessException e) {
							throw new RuntimeException(e);
						}
					});
				}
			}

		}
	}

	class MultiPartSupportConfiguration {

		@Bean
		public SpringFormEncoder feignFormEncoder(ObjectFactory<HttpMessageConverters> messageConverters) {
			return new SpringFormEncoder(new SpringEncoder(messageConverters));
		}
	}

}

package com.xjinyao.xcloud.file.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xjinyao.xcloud.common.core.util.SpringContextHolder;
import com.xjinyao.xcloud.file.api.entity.SysFile;
import com.xjinyao.xcloud.file.mapper.FileMapper;
import com.xjinyao.xcloud.file.service.IFileService;
import com.xjinyao.xcloud.file.store.FileStore;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Optional;
import java.util.function.Consumer;

/**
 * 文件库
 *
 * @author 谢进伟
 * @date 2020-05-15 14:49:59
 */
@Service
public class FileServiceImpl extends ServiceImpl<FileMapper, SysFile> implements IFileService {

	@Override
	public void handler(SysFile sysFile, Consumer<FileStore> consumer) {
		Optional.of(SpringContextHolder.getBeans(FileStore.class))
				.orElse(Collections.emptyList())
				.stream()
				.filter(fileStore -> fileStore.getFileStoreType().toString().equals(sysFile.getFileType()))
				.findFirst()
				.ifPresent(consumer);
	}
}

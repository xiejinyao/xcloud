package com.xjinyao.xcloud.file.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xjinyao.xcloud.file.api.entity.SysFile;
import com.xjinyao.xcloud.file.store.FileStore;

import java.util.function.Consumer;

/**
 * 文件库
 *
 * @author 谢进伟
 * @date 2020-05-15 14:49:59
 */
public interface IFileService extends IService<SysFile> {

	void handler(SysFile sysFile, Consumer<FileStore> consumer);
}

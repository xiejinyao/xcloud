package com.xjinyao.xcloud.file.store;

import lombok.Data;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.OutputStream;

/**
 * @author 谢进伟
 * @createDate 2023/2/27 12:32
 */
public interface FileStore {

	/**
	 * 获取文件存储类型
	 *
	 * @return {@link FileStoreType}
	 */
	FileStoreType getFileStoreType();

	/**
	 * 文件预览前缀
	 *
	 * @return
	 */
	String getFileUrlPrefix();

	/**
	 * 让网络网址
	 *
	 * @param relativePath 相对路径
	 * @return {@link String}
	 */
	default String getNetUrl(String relativePath) {
		String urlPrefix = this.getFileUrlPrefix();
		relativePath = StringUtils.replace(relativePath, "\\", "/");
		urlPrefix = StringUtils.endsWith(urlPrefix, "/") ? urlPrefix : (urlPrefix + "/");
		relativePath = StringUtils.startsWith(relativePath, "/") ? StringUtils.substring(relativePath, 1) : relativePath;
		return urlPrefix + relativePath;
	}

	/**
	 * 上传文件
	 *
	 * @param multipartFile      上传文件对象
	 * @param customRelativePath 自定义存储目录
	 */
	UploadResult upload(MultipartFile multipartFile,
						String customRelativePath) throws IOException;

	/**
	 * 存在
	 *
	 * @param relativePath 相对路径
	 * @return boolean
	 */
	boolean exists(String relativePath);

	/**
	 * 删除
	 *
	 * @param relativePath 相对路径
	 * @return boolean
	 */
	boolean delete(String relativePath);

	/**
	 * 下载
	 *
	 * @param relativePath 相对路径
	 * @param outputStream 输出流
	 */
	void download(String relativePath, OutputStream outputStream);

	@Data
	class UploadResult {

		private long fileSize;

		private String relativePath;

		private String prefix;

		private String url;

	}

}

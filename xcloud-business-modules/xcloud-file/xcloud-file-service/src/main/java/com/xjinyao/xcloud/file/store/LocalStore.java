package com.xjinyao.xcloud.file.store;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.lang.UUID;
import com.xjinyao.xcloud.file.properties.LocalStoreProperties;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Date;

/**
 * @author 谢进伟
 * @createDate 2023/2/27 12:34
 */
@Data
@Slf4j
public class LocalStore implements FileStore {

	private LocalStoreProperties properties;

	public LocalStore(LocalStoreProperties properties) {
		this.properties = properties;
	}

	/**
	 * 文件预览前缀
	 *
	 * @return
	 */
	@Override
	public String getFileUrlPrefix() {
		return properties.getFileUrlPrefix();
	}

	/**
	 * 获取文件存储类型
	 *
	 * @return {@link FileStoreType}
	 */
	@Override
	public FileStoreType getFileStoreType() {
		return FileStoreType.LOCAL;
	}

	/**
	 * 上传文件
	 *
	 * @param multipartFile      上传文件对象
	 * @param customRelativePath 自定义存储目录
	 */
	@Override
	public UploadResult upload(MultipartFile multipartFile,
							   String customRelativePath) throws IOException {
		String templateFilePath = properties.getTemplateFilePath();
		String localSavePath = properties.getLocalSavePath();
		if (StringUtils.isBlank(templateFilePath)) {
			log.error("请设置文件上传临时存储路径：file.upload.template-file-path");
			throw new RuntimeException("服务器端未配置文件上传相关参数(config 'file.upload.local-store.template-file-path' is not set)!");
		}
		if (StringUtils.isBlank(localSavePath)) {
			log.error("请设置文件上传临时存储路径：file.upload.local-save-path");
			throw new RuntimeException("服务器端未配置文件上传相关参数(config 'file.upload.local-store.local-save-path' is not set)!");
		}

		//创建临时目录
		FileUtils.forceMkdir(new File(templateFilePath));

		try (InputStream inputStream = multipartFile.getInputStream()) {
			String originalFilename = multipartFile.getOriginalFilename();
			String fileType = StringUtils.substringAfterLast(originalFilename, ".");
			String relativePath = StringUtils.isNotBlank(customRelativePath) ?
					(customRelativePath + File.separator + originalFilename) :
					(DateUtil.format(new Date(), "yyyy" + File.separator + "MM" + File.separator + "dd" +
							File.separator + "HH") + File.separator + UUID.fastUUID() + "." + fileType);
			File file = new File(localSavePath + File.separator + relativePath);
			File parentFile = file.getParentFile();
			if (!parentFile.exists()) {
				parentFile.mkdirs();
			}
			FileUtils.copyInputStreamToFile(inputStream, file);

			UploadResult uploadResult = new UploadResult();
			uploadResult.setFileSize(file.length());
			uploadResult.setRelativePath(relativePath);
			uploadResult.setPrefix(this.getFileUrlPrefix());
			uploadResult.setUrl(this.getNetUrl(relativePath));
			return uploadResult;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 存在
	 *
	 * @param relativePath 相对路径
	 * @return boolean
	 */
	@Override
	public boolean exists(String relativePath) {
		return new File(properties.getLocalSavePath() + File.separator + relativePath).exists();
	}

	/**
	 * 删除
	 *
	 * @param relativePath 相对路径
	 * @return boolean
	 */
	@Override
	public boolean delete(String relativePath) {
		File file = new File(properties.getLocalSavePath() + File.separator + relativePath);
		if (file.exists()) {
			return FileUtils.deleteQuietly(file);
		}
		return false;
	}

	/**
	 * 下载
	 *
	 * @param relativePath 相对路径
	 * @param outputStream 输出流
	 */
	@Override
	public void download(String relativePath, OutputStream outputStream) {
		if (outputStream == null) {
			return;
		}
		File file = new File(properties.getLocalSavePath() + File.separator + relativePath);
		if (!file.exists()) {
			return;
		}
		try {
			FileUtils.copyFile(file, outputStream);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
}

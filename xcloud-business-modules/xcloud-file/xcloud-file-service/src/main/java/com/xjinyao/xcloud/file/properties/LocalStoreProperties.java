package com.xjinyao.xcloud.file.properties;

import lombok.Data;

/**
 * @author 谢进伟
 * @createDate 2023/2/27 12:34
 */
@Data
public class LocalStoreProperties {

	/**
	 * 启用
	 */
	private Boolean enable = Boolean.FALSE;

	/**
	 * 临时文件缓存位置
	 */
	private String templateFilePath;

	/**
	 * 上传文件在服务器端保存的位置
	 */
	private String localSavePath;

	/**
	 * 文件预览前缀
	 */
	private String fileUrlPrefix;

}

package com.xjinyao.xcloud.file.controller;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.lang.UUID;
import com.xjinyao.xcloud.common.core.util.ZipUtil;
import com.xjinyao.xcloud.file.api.entity.SysFile;
import com.xjinyao.xcloud.file.service.IFileService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiModelProperty;
import io.swagger.annotations.ApiParam;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.httpclient.DefaultHttpMethodRetryHandler;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.SimpleHttpConnectionManager;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.HeadMethod;
import org.apache.commons.httpclient.params.HttpClientParams;
import org.apache.commons.httpclient.params.HttpConnectionManagerParams;
import org.apache.commons.httpclient.params.HttpMethodParams;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.activation.MimetypesFileTypeMap;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author 谢进伟
 * @description 下载代理控制器
 * @createDate 2020/7/23 13:05
 */
@Slf4j
@Controller
@AllArgsConstructor
@RequestMapping(value = "/")
@Api(value = "/", tags = "文件下载管理")
public class DownloadProxyController {

	private final IFileService fileService;

	/**
	 * 获取远程文件大小
	 *
	 * @param url 远程文件大小
	 * @return
	 * @throws IOException
	 */
	public static long getRemoteFileLength(String url) throws IOException {
		HeadMethod method = new HeadMethod(url);
		HttpClient hc = getHttpClient();
		hc.executeMethod(method);
		return method.getResponseContentLength();
	}

	/**
	 * 下载远程文件
	 *
	 * @param httpUrl 远程文件访问路径
	 * @param out     输出流
	 * @return
	 * @throws IOException
	 */
	public static void downloadRemoteFile(String httpUrl, OutputStream out) throws IOException {
		downloadRemoteFile(httpUrl, out, null, null);
	}

	/**
	 * 下载远程文件
	 *
	 * @param httpUrl 远程文件访问路径
	 * @param out     输出流
	 * @param start   其实下载位置
	 * @param end     结束位置
	 * @throws IOException
	 */
	public static void downloadRemoteFile(String httpUrl, OutputStream out, Long start, Long end) throws IOException {
		if (out == null) {
			return;
		}
		log.info("开始下载远程文件：" + httpUrl);
		HttpClient hc = getHttpClient();
		HeadMethod headMethod = new HeadMethod(httpUrl);
		long contentLength = headMethod.getResponseContentLength();
		GetMethod getMethod = new GetMethod(httpUrl);
		if (start != null) {
			if (end == null || end == 0) {
				end = contentLength - 1;
			}
			contentLength = end - start + 1;
			// 断点开始响应的格式:Range: bytes=start-end
			if (start != null) {
				StringBuffer contentRange = new StringBuffer("bytes=").append(start).append("-").append(end);
				getMethod.addRequestHeader("Range", contentRange.toString());
			} else {
				getMethod.addRequestHeader("Range", new StringBuffer("bytes=").append("0-")
						.append(contentLength - 1).toString());
			}
		}
		hc.executeMethod(getMethod);
		try (InputStream fis = getMethod.getResponseBodyAsStream()) {
			IOUtils.copy(fis, out);
		} catch (IOException e) {
		} finally {
			out.close();
		}
	}

	/**
	 * 获取一个 HttpClient 实列
	 *
	 * @return
	 */
	private static HttpClient getHttpClient() {
		HttpClientParams httpClientParams = new HttpClientParams();
		// 设置httpClient的连接超时，对连接管理器设置的连接超时是无用的
		httpClientParams.setConnectionManagerTimeout(5000); ///该值就是连接不够用的时候等待超时时间，一定要设置，而且不能太大
		//另外设置http client的重试次数，默认是3次；当前是禁用掉（如果项目量不到，这个默认即可）
		httpClientParams.setParameter(HttpMethodParams.RETRY_HANDLER,
				new DefaultHttpMethodRetryHandler(5, false));

		SimpleHttpConnectionManager httpConnectionManager = new SimpleHttpConnectionManager(true);
		HttpConnectionManagerParams connectionManagerParams = new HttpConnectionManagerParams();
		connectionManagerParams.setSoTimeout(0);//设置等待数据超时时间
		connectionManagerParams.setStaleCheckingEnabled(true);
		httpConnectionManager.setParams(connectionManagerParams);

		return new HttpClient(httpClientParams, httpConnectionManager);
	}

	@ResponseBody
	@ApiModelProperty("下载本地文件")
	@RequestMapping(value = "downloadLocalFile", method = RequestMethod.GET)
	public void downloadLocalFile(@ApiParam(value = "文件id，多个文件将以zip形式打包下载", required = true)
								  @RequestParam List<String> fileIdList, HttpServletResponse response) {
		if (CollectionUtil.isNotEmpty(fileIdList)) {
			List<SysFile> fileList = fileService.listByIds(fileIdList);
			if (CollectionUtil.isNotEmpty(fileList)) {
				if (CollectionUtil.isNotEmpty(fileList)) {
					List<File> srcFileList = new ArrayList<>();
					fileList.forEach(sf -> {
						fileService.handler(sf, fileStore -> {
							if (fileStore.exists(sf.getRelativePath())) {
								String tempDirectoryPath = FileUtils.getTempDirectoryPath();
								String prefix = sf.getOriginalName();
								String suffix = "." + sf.getFileType();
								try (FileOutputStream outputStream = new FileOutputStream(tempDirectoryPath +
										File.separator + prefix + suffix)) {
									File tempFile = File.createTempFile(prefix, suffix);
									fileStore.download(sf.getRelativePath(), outputStream);
									srcFileList.add(tempFile);
								} catch (IOException e) {
									e.printStackTrace();
								}
							}
						});
					});
					try (ServletOutputStream outputStream = response.getOutputStream()) {
						if (srcFileList.size() > 1) {
							response.setHeader("Content-Disposition", "attachment;filename=" +
									URLEncoder.encode(DateUtil.format(new Date(), "yyyyMMdd") +
											UUID.fastUUID().toString() + ".zip", StandardCharsets.UTF_8.toString()));
							ZipUtil.zip(srcFileList, outputStream);
						} else if (srcFileList.size() == 1) {
							SysFile sysFile = fileList.get(0);
							File file = srcFileList.get(0);
							response.setHeader("Content-Disposition", "attachment;filename=" +
									URLEncoder.encode(sysFile.getOriginalName(), StandardCharsets.UTF_8.toString()));
							FileUtils.copyFile(file, outputStream);
						} else {
							response.setHeader("Content-Disposition", "attachment;filename=notfound.txt");
							IOUtils.write("we are sorry that we did not find these files, these files have been " +
									"cleaned or did not exist at all!", outputStream, StandardCharsets.UTF_8);
						}
					} catch (IOException e) {
						e.printStackTrace();
					} finally {
						srcFileList.forEach(FileUtils::deleteQuietly);

					}
				}
			}
		}
	}

	@ResponseBody
	@ApiModelProperty("下载远程文件")
	@RequestMapping(value = "downloadRemoteFile", method = RequestMethod.GET)
	public void downloadRemoteFile(@ApiParam(value = "文件下载地址", required = true)
								   @RequestParam String url, String downloadFileName, HttpServletResponse response) {
		try {
			if (StringUtils.isNotBlank(url)) {
				if (StringUtils.isBlank(downloadFileName)) {
					downloadFileName = UUID.fastUUID().toString();
				}
				response.setContentType(new MimetypesFileTypeMap().getContentType(downloadFileName));
				response.setContentLengthLong(getRemoteFileLength(url));
				response.setHeader("Cache-Control", "no-store");
				response.setHeader("Accept-Ranges", "bytes");
				response.setHeader("Server", "NLDMS.Server");
				response.setHeader("Content-Disposition", "attachment;filename=" +
						URLEncoder.encode(downloadFileName, StandardCharsets.UTF_8.toString()));
				downloadRemoteFile(url, response.getOutputStream());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}

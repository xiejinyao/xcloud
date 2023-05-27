package com.xjinyao.xcloud.report.provider;

import com.baomidou.mybatisplus.extension.conditions.query.LambdaQueryChainWrapper;
import com.xjinyao.xcloud.common.core.util.DateUtil;
import com.xjinyao.xcloud.common.core.util.RequestHolder;
import com.xjinyao.xcloud.common.security.service.CustomRemoteTokenServices;
import com.xjinyao.xcloud.common.security.service.CustomUser;
import com.xjinyao.xcloud.common.security.util.SecurityUtils;
import com.xjinyao.xcloud.report.entity.ReportInfo;
import com.xjinyao.xcloud.report.service.ReportInfoService;
import com.xjinyao.report.core.provider.report.ReportFile;
import com.xjinyao.report.core.provider.report.ReportProvider;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpHeaders;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

/**
 * JDBC报表管理
 *
 * @author 谢进伟
 * @createDate 2023/2/28 10:04
 */
@Slf4j
public class JdbcReportProvider implements ReportProvider {

	private final String name;

	private final String prefix;

	private final boolean disabled;

	private final ReportInfoService reportInfoService;

	private final CustomRemoteTokenServices remoteTokenServices;

	public JdbcReportProvider(String name, String prefix, boolean disabled, ReportInfoService reportInfoService,
							  CustomRemoteTokenServices remoteTokenServices) {
		this.name = name;
		this.prefix = prefix;
		this.disabled = disabled;
		this.reportInfoService = reportInfoService;
		this.remoteTokenServices = remoteTokenServices;
	}

	/**
	 * 根据报表名加载报表文件
	 *
	 * @param file 报表名称
	 * @return 返回的InputStream
	 */
	@Override
	public InputStream loadReport(String file) {
		String projectId = getRequestProjectIdValue();
		log.info("加载报表 projectId:{},fileName:{}", projectId, file);
		if (StringUtils.isBlank(projectId)) {
			return null;
		}
		file = getRealFileName(file);
		AtomicReference<ByteArrayInputStream> inputStream = new AtomicReference<>(new ByteArrayInputStream(new byte[0]));
		Optional.ofNullable(getReportInfo(file, projectId, true))
				.map(ReportInfo::getTplContent)
				.filter(StringUtils::isNotBlank)
				.ifPresent(tplContent -> {
					byte[] bytes = tplContent.getBytes(StandardCharsets.UTF_8);
					ByteArrayInputStream newValue = new ByteArrayInputStream(bytes);
					inputStream.set(newValue);
				});

		return inputStream.get();
	}

	/**
	 * 根据报表名加载报表文件信息
	 *
	 * @param file 报表名称
	 * @return 返回的InputStream
	 */
	@Override
	public ReportFile loadReportInfo(String file) {
		String projectId = getRequestProjectIdValue();
		log.info("加载报表 projectId:{},fileName:{}", projectId, file);
		if (StringUtils.isBlank(projectId)) {
			return null;
		}
		file = getRealFileName(file);
		return Optional.ofNullable(getReportInfo(file, projectId, false))
				.stream()
				.findFirst()
				.map(reportInfo -> ReportFile.builder()
						.type(reportInfo.getType().toString())
						.name(reportInfo.getName())
						.fileName(reportInfo.getFileName())
						.description(reportInfo.getDescription())
						.isTemplate(reportInfo.getIsTemplate())
						.visible(reportInfo.getVisible())
						.previewImmediatelyLoad(reportInfo.getPreviewImmediatelyLoad())
						.previewParamsDeclarationConfig(reportInfo.getPreviewParamsDeclarationConfig())
						.updateDate(DateUtil.localDateTime2Date(reportInfo.getCreateTime()))
						.build())
				.orElse(null);
	}

	private ReportInfo getReportInfo(String file, String projectId, boolean onlySelectContent) {
		LambdaQueryChainWrapper<ReportInfo> query = this.reportInfoService.lambdaQuery();
		LambdaQueryChainWrapper<ReportInfo> query1 = this.reportInfoService.lambdaQuery();
		if (onlySelectContent) {
			query.select(ReportInfo::getTplContent);
			query1.select(ReportInfo::getTplContent);
		}
		return query.eq(ReportInfo::getFileName, file)
				.eq(ReportInfo::getProjectId, projectId)
				.oneOpt()
				.orElse(query1.eq(ReportInfo::getFileName, file)
						.eq(ReportInfo::getIsTemplate, true)
						.isNull(ReportInfo::getProjectId)
						.oneOpt()
						.orElse(null));
	}

	/**
	 * 根据报表名，删除指定的报表文件
	 *
	 * @param file 报表名称
	 */
	@Override
	public void deleteReport(String file) {
		String projectId = getRequestProjectIdValue();
		log.info("删除报表 projectId:{},fileName:{}", projectId, file);
		if (StringUtils.isBlank(projectId)) {
			return;
		}
		this.reportInfoService.lambdaUpdate()
				.eq(ReportInfo::getProjectId, projectId)
				.eq(ReportInfo::getFileName, getRealFileName(file))
				.remove();
	}

	/**
	 * 获取所有的报表文件
	 *
	 * @return 返回报表文件列表
	 */
	@Override
	public List<ReportFile> getReportFiles() {
		String projectId = getRequestProjectIdValue();
		log.info("获取所有报 projectId:{}", projectId);
		if (StringUtils.isBlank(projectId)) {
			return null;
		}
		LambdaQueryChainWrapper<ReportInfo> query = this.reportInfoService.lambdaQuery()
				.select(ReportInfo::getType,
						ReportInfo::getName,
						ReportInfo::getFileName,
						ReportInfo::getDescription,
						ReportInfo::getIsTemplate,
						ReportInfo::getVisible,
						ReportInfo::getCreateTime);
		String requestAuValue = getRequestAuValue();
		if (StringUtils.isNotBlank(requestAuValue)) {
			CustomUser user = SecurityUtils.getUser(this.remoteTokenServices.loadAuthentication(
					requestAuValue, true));
			log.info("user is {}", user);
			if (!SecurityUtils.isSuperAdmin(user)) {
				query.eq(ReportInfo::getProjectId, projectId);
			}
		} else {
			throw new RuntimeException("授权信息不存在，无法加载！");
		}
		return Optional.ofNullable(query.list())
				.orElse(Collections.emptyList())
				.stream()
				.map(d -> new ReportFile(d.getType().toString(),
						d.getName(),
						d.getFileName(),
						d.getDescription(),
						d.getIsTemplate(),
						d.getVisible(),
						d.getPreviewImmediatelyLoad(),
						DateUtil.localDateTime2Date(d.getCreateTime())))
				.collect(Collectors.toList());
	}

	/**
	 * 保存报表文件
	 *
	 * @param file    报表名称
	 * @param content 报表的XML内容
	 */
	@Override
	public void saveReport(String file, String content) {
		String projectId = getRequestProjectIdValue();
		log.info("保存报表 projectId:{}", projectId);
		if (StringUtils.isBlank(projectId)) {
			return;
		}
		Optional.ofNullable(this.reportInfoService.lambdaQuery()
						.eq(ReportInfo::getFileName, getRealFileName(file))
						.list())
				.orElse(Collections.emptyList())
				.stream()
				.filter(d -> {
					String recordProjectId = d.getProjectId();
					Boolean isTemplate = d.getIsTemplate();
					if (StringUtils.isNotBlank(recordProjectId)) {
						return StringUtils.equals(projectId, recordProjectId);
					} else {
						return BooleanUtils.toBoolean(isTemplate);
					}
				})
				.findFirst()
				.ifPresentOrElse(reportInfo -> {
					String onlySaveContent = RequestHolder.getParamValue("onlySaveContent");
					Integer reportType = RequestHolder.getParamIntValue("reportType");
					String reportName = RequestHolder.getParamValue("reportName");
					String description = RequestHolder.getParamValue("description");
					String visible = RequestHolder.getParamValue("visible");
					String previewParamsDeclarationConfig = RequestHolder.getParamValue("previewParamsDeclarationConfig");

					if (StringUtils.isNotBlank(onlySaveContent) && BooleanUtils.toBoolean(onlySaveContent)) {
						reportInfo.setTplContent(content);
					} else {
						reportInfo.setType(reportType);
						reportInfo.setVisible(BooleanUtils.toBoolean(visible));
						reportInfo.setPreviewParamsDeclarationConfig(previewParamsDeclarationConfig);
						reportInfo.setName(reportName);
						reportInfo.setDescription(description);
					}

					this.reportInfoService.updateById(reportInfo);
				}, () -> {
					Integer reportType = RequestHolder.getParamIntValue("reportType");
					if (reportType == null) {
						return;
					}
					String reportName = RequestHolder.getParamValue("reportName");
					String description = RequestHolder.getParamValue("description");
					String visible = RequestHolder.getParamValue("visible");
					String isTemplate = RequestHolder.getParamValue("isTemplate");
					String previewParamsDeclarationConfig = RequestHolder.getParamValue("previewParamsDeclarationConfig");
					if (StringUtils.isAnyBlank(reportName, description)) {
						return;
					}
					ReportInfo reportInfo = new ReportInfo();
					reportInfo.setProjectId(projectId);
					reportInfo.setIsTemplate(BooleanUtils.toBoolean(isTemplate));
					reportInfo.setVisible(BooleanUtils.toBoolean(visible));
					reportInfo.setPreviewParamsDeclarationConfig(previewParamsDeclarationConfig);
					reportInfo.setType(reportType);
					reportInfo.setName(reportName);
					reportInfo.setFileName(getRealFileName(file));
					reportInfo.setTplContent(content);
					reportInfo.setDescription(description);
					if (reportInfo.getIsTemplate()) {
						reportInfo.setProjectId(null);
					}
					this.reportInfoService.save(reportInfo);
				});


	}

	/**
	 * @return 返回存储器名称
	 */
	@Override
	public String getName() {
		return name;
	}

	/**
	 * @return 返回是否禁用
	 */
	@Override
	public boolean disabled() {
		return this.disabled;
	}

	/**
	 * @return 返回报表文件名前缀
	 */
	@Override
	public String getPrefix() {
		return this.prefix;
	}

	private String getRealFileName(String file) {
		if (file.startsWith(prefix)) {
			file = file.substring(prefix.length());
		}
		return file;
	}

	private String getRequestProjectIdValue() {
		String headerValue = RequestHolder.getHeaderValue("projectId");
		return StringUtils.defaultString(headerValue, RequestHolder.getParamValue("projectId"));
	}

	public String getRequestAuValue() {
		String headerValue = RequestHolder.getHeaderValue(HttpHeaders.AUTHORIZATION);
		String au = StringUtils.defaultString(headerValue, RequestHolder.getParamValue(HttpHeaders.AUTHORIZATION));
		String prefix = "Bearer ";
		return StringUtils.isNotBlank(au) ? (au.startsWith(prefix) ? StringUtils.substringAfter(au, prefix) : au) : null;
	}
}

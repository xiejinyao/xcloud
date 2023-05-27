package com.xjinyao.xcloud.common.core.excel;


import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.lang.UUID;
import cn.hutool.http.HttpUtil;
import com.xjinyao.xcloud.common.core.excel.annotation.ExcelHeardField;
import com.xjinyao.xcloud.common.core.excel.annotation.ExcelVO;
import com.xjinyao.xcloud.common.core.excel.pojo.ExcelDictionary;
import com.xjinyao.xcloud.common.core.excel.pojo.ExcelHeard;
import com.xjinyao.xcloud.common.core.excel.pojo.ExcelImportErrorData;
import com.xjinyao.xcloud.common.core.excel.pojo.PersistenceErrorVO;
import com.xjinyao.xcloud.common.core.excel.progress.ExcelDisposeProgress;
import com.xjinyao.xcloud.common.core.excel.progress.ExcelPersistenceProgress;
import com.xjinyao.xcloud.common.core.excel.util.ExcelHelper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author 谢进伟
 * @description Excel导入过程抽象, 泛型T代表数据映射实体，M代表excel头信息映射实体
 * @createDate 2020/9/10 21:02
 */
@Slf4j
public abstract class AbstractDataImport<T, M extends PersistenceErrorVO> {

	/**
	 * 数据持久化线程池
	 */
	protected static ExecutorService executorService = Executors.newFixedThreadPool(5);
	/**
	 * 数据入库时，针对入库数据的切片大小
	 */
	protected int batchSaveDataListSize = 100;
	/**
	 * 数据持久化总进度
	 */
	protected volatile int batchCount = 0;

	/**
	 * excel 解析之后所发现的错误数据问题
	 */
	protected List<ExcelImportErrorData> excelImportErrorData = new ArrayList<>();
	/**
	 * 持久化错误数据
	 */
	protected List<M> persistenceErrorDataList = new ArrayList<>();
	/**
	 * excel 解析之后得到的数据
	 */
	protected List<T> excelDataList = new ArrayList<>();
	/**
	 * excel 解析之后得到数据总条数
	 */
	protected int excelDataSize;
	/**
	 * 动态数据字典映射表
	 */
	protected Map<String, List<ExcelDictionary>> fieldDynamicExcelDictionary = new HashMap<>();

	/**
	 * excel 每一行所对应的POJO类型
	 */
	protected Class<T> cls;
	/**
	 * excel 文件的路径，若该路径为网络路径，则在开始导入时将会自动尝试下载网络上的excel文件
	 */
	protected String excelFilePath;
	/**
	 * excel 文件大小
	 */
	protected long excelFileSize;
	/**
	 * 映射实体
	 */
	protected Class<?> voCls;
	/**
	 * excel 文件，此文件参与excel解析过程
	 */
	protected File excelFile;
	/**
	 * excel 数据解析进度
	 */
	protected ExcelDisposeProgress disposeProgress;
	/**
	 * excel 数据持久化进度
	 */
	protected ExcelPersistenceProgress<T> persistenceProgress;


	protected AbstractDataImport() {
	}

	public AbstractDataImport(Class<T> cls,
							  String excelFilePath,
							  Class<?> voCls) {
		this.cls = cls;
		this.excelFilePath = excelFilePath;
		this.voCls = voCls;
	}

	public AbstractDataImport(Class<T> cls,
							  String excelFilePath,
							  Class<?> voCls,
							  ExcelDisposeProgress disposeProgress,
							  ExcelPersistenceProgress<T> persistenceProgress) {
		this(cls, excelFilePath, voCls);
		this.disposeProgress = disposeProgress;
		this.persistenceProgress = persistenceProgress;
	}

	/**
	 * 加载excel文件
	 *
	 * @throws IOException
	 */
	private void loadExcelFile() throws IOException {
		if (StringUtils.startsWith(excelFilePath, "http://") || StringUtils.startsWith(excelFilePath, "https://")) {
			log.info("正在下载远程 Excel 文件,URL：{}", excelFilePath);
			String suffix = StringUtils.substringAfterLast(this.excelFilePath, ".");
			this.excelFile = File.createTempFile(UUID.fastUUID().toString(), "." + suffix);
			excelFileSize = HttpUtil.downloadFile(this.excelFilePath, this.excelFile);
		} else {
			this.excelFile = new File(excelFilePath);
		}
		log.info("Excel 文件路径：{}", excelFile.getPath());
	}

	/**
	 * 加载excel数据
	 */
	private void loadData() {
		try {
			this.loadDataBefore();
			excelDataList = ExcelHelper.loadExcel(
					this.cls,
					this.voCls,
					this.excelFile.getPath(),
					this.excelImportErrorData,
					this.fieldDynamicExcelDictionary,
					this.disposeProgress
			);
			this.excelDataSize = excelDataList.size();
			this.batchCount = (int) Math.ceil(excelDataSize / Double.valueOf(batchSaveDataListSize));
		} catch (Exception e) {
			this.loadDataException(e);
		} finally {
			FileUtils.deleteQuietly(excelFile);
			this.loadDataAfter();
		}
	}

	protected List<ExcelHeard> getErrorIdentifyHeard() {
		if (!voCls.isAnnotationPresent(ExcelVO.class)) {
			return null;
		}
		return new ArrayList<ExcelHeard>() {{
			for (Class acls = voCls; acls != null; acls = acls.getSuperclass()) {
				Field[] declaredFields = acls.getDeclaredFields();
				for (Field field : declaredFields) {
					field.setAccessible(true);
					if (!field.isAnnotationPresent(ExcelHeardField.class)) {
						continue;
					}
					ExcelHeardField importField = field.getAnnotation(ExcelHeardField.class);
					if (!importField.errorIdentify()) {
						continue;
					}
					this.add(new ExcelHeard() {{
						this.setColumnName(importField.columnName());
						this.setRequired(importField.importRequired());
						this.setPattern(importField.importPattern());
						this.setMatchErrorMessage(importField.importMatchErrorMessage());
						this.setJavaField(field.getName());
						this.setColumnType(field.getType().getTypeName());
						ExcelHelper.setHeaderDictionary(this, importField.dictionary());
					}});
				}
			}
		}};
	}

	/**
	 * 开始导入
	 *
	 * @return
	 * @throws IOException
	 */
	protected void execute() throws Exception {
		this.begin();

		this.loadExcelFile();

		if (this.excelFileSize > 0) {
			this.loadData();
			if (CollectionUtil.isEmpty(excelImportErrorData)) {
				int count = this.batchCount;
				for (int pageNo = 1; pageNo <= count; pageNo++) {
					int finalPageNo = pageNo;
					executorService.execute(() -> {
						List<T> subList = null;
						try {
							int fromIndex = (finalPageNo - 1) * batchSaveDataListSize;
							int toIndex = fromIndex + batchSaveDataListSize;
							toIndex = toIndex > excelDataSize ? excelDataSize : toIndex;
							subList = excelDataList.subList(fromIndex, toIndex);
							persistence(finalPageNo, subList);
						} catch (Exception e) {
							e.printStackTrace();
						} finally {
							if (--batchCount == 0) {
								done();
							}
						}
					});
				}
			} else {
				this.parseError(excelImportErrorData);
			}
		} else {
			excelImportErrorData.add(new ExcelImportErrorData(0, 0, true,
					"未发现任何数据,无法导入!", ""));
			this.parseError(excelImportErrorData);
		}
	}

	protected boolean addErrorData(M e) {
		return persistenceErrorDataList.add(e);
	}

	/**
	 * 开始处理之前，在下载文件之前触发
	 */
	protected abstract void begin();

	/**
	 * 当解析excel文件出现错误时触发
	 */
	protected abstract void parseError(List<ExcelImportErrorData> excelImportErrorData);

	/**
	 * 加载excel数据之前
	 */
	protected void loadDataBefore() {
		log.info("开始解析Excel数据...");
		log.info("数据映射类：{}", voCls);
	}

	/**
	 * 加载excel数据之后，不管会不会异常都会触发
	 */
	protected void loadDataAfter() {
		log.info("excel解析完成，共{}条数据", excelDataSize);
	}

	/**
	 * 加载excel出现异常的方法
	 *
	 * @param e
	 */
	protected void loadDataException(Exception e) {
		log.error("excel 解析失败!", e);
	}

	/**
	 * 持久化
	 *
	 * @param batchNumber 批次号
	 * @param list        数据集
	 */
	protected abstract void persistence(int batchNumber, List<T> list);

	/**
	 * 导入完成
	 */
	protected abstract void done();

	/**
	 * 持久化进度
	 *
	 * @param obj         解析得到的对象
	 * @param batchNumber 批次号
	 * @param current     当前分片元素位置
	 */
	protected void persistenceProgress(T obj, int batchNumber, int total, int current) {
		if (persistenceProgress != null) {
			persistenceProgress.progress(obj, batchNumber, total, current);//getCurrent(batchNumber, current));
		}
	}


    /**
     * 获取当前的入库解析的行号
     *
     * @param batchNumber 批次号
     * @param current     当前分片元素位置
     * @return
     */
    protected int getCurrent(int batchNumber, int current) {
        return (batchNumber - 1) * batchSaveDataListSize + current;
    }

}

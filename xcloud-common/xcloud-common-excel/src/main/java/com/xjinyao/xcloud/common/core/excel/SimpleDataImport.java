package com.xjinyao.xcloud.common.core.excel;

import cn.hutool.core.lang.UUID;
import com.xjinyao.xcloud.common.core.excel.constants.ExcelDataDisposeProgressConstants;
import com.xjinyao.xcloud.common.core.excel.pojo.ExcelHeard;
import com.xjinyao.xcloud.common.core.excel.pojo.ExcelImportErrorData;
import com.xjinyao.xcloud.common.core.excel.pojo.PersistenceErrorVO;
import com.xjinyao.xcloud.common.core.excel.progress.dto.ExcelDisposeProgressInfo;
import com.xjinyao.xcloud.common.core.excel.progress.dto.ExcelPersistenceProgressInfo;
import com.xjinyao.xcloud.common.core.excel.util.ExcelHelper;
import com.xjinyao.xcloud.common.core.util.StringUtils;
import com.xjinyao.xcloud.common.core.util.ZipUtil;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.ListUtils;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.http.MediaType;
import org.springframework.web.multipart.commons.CommonsMultipartFile;

import java.io.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author 谢进伟
 * @description 简单数据导入实现
 * @createDate 2021/3/5 9:47
 */
@Slf4j
public abstract class SimpleDataImport<T, M extends PersistenceErrorVO> extends AbstractDataImport<T, M> {

    /**
     * 单个excel文件存储的错误数据行数
     */
    protected static final int EXCEL_FILE_MAX_ROW_COUNT = 1000;

    /**
     * 导入任务Id
     */
    protected String taskId;
    /**
     * 任务名称
     */
    protected String taskName;
    /**
     * excel 文件id
     */
    protected String fileId;
    /**
     * excel文件名
     */
    protected String fileName;

    /**
     * 错误数据记录文件
     */
    protected File errorTemplateZipFile;

    /**
     * 错误文件下载路径
     */
    protected String errorFileDownloadUrl;

    protected SimpleDataImport() {

    }

    protected SimpleDataImport(Class<T> cls,
                               String excelFilePath,
                               Class<M> voCls,
                               String taskId,
                               String taskName,
                               String fileId,
                               String fileName) {
        super(cls, excelFilePath, voCls);
        this.taskId = taskId;
        this.fileId = fileId;
        this.taskName = taskName;
        this.fileName = fileName;

        initProgressImpl(taskId, fileId, fileName);
    }

    protected void initProgressImpl(String taskId, String fileId, String fileName) {
        //excel 数据解析进度
        this.disposeProgress = (total, current) -> {
            log.info("当前读取进度：" + current + StringUtils.SLASH_SEPARATOR + total);
            this.broadcast(new ExcelDisposeProgressInfo(ExcelDataDisposeProgressConstants.ANALYTICAL,
                    current,
                    total,
                    "excel解析中",
                    taskId,
                    taskName,
                    fileId,
                    fileName));

        };

        //持久化处理进度
        this.persistenceProgress = (obj, batchNumber, total, current) -> {
            log.info("数据处理进度：{}/{},当前数据：{}", current, total, obj);
            this.broadcast(new ExcelPersistenceProgressInfo(ExcelDataDisposeProgressConstants.STORAGE,
                    current,
                    total,
                    "处理入库中",
                    taskId,
                    fileId,
                    taskName,
                    fileName,
                    String.valueOf(batchNumber)
            ));
        };
    }

    @Override
    protected void done() {
        log.info("文件id: {} 导入完成!", fileId);
        List<File> errorFileList = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(excelImportErrorData)) {
            AtomicInteger no = new AtomicInteger(1);
            final String sheetName = "文件解析错误信息";
            final String temFilePrefix = "excel_parse_error_";
            ListUtils.partition(this.excelImportErrorData, EXCEL_FILE_MAX_ROW_COUNT).forEach(subList -> {
                try (XSSFWorkbook errorDataExcelWorkbook = ExcelHelper.export(subList,
                        sheetName,
                        ExcelImportErrorData.class)) {
                    File errorFile = workbookToFile(no, temFilePrefix, errorDataExcelWorkbook);
                    errorFileList.add(errorFile);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
        }
        if (CollectionUtils.isNotEmpty(persistenceErrorDataList)) {
            final List<ExcelHeard> errorIdentifyHead = this.getErrorIdentifyHeard();
            if (CollectionUtils.isNotEmpty(errorIdentifyHead)) {
                final String sheetName = "数据持久化错误信息";
                final String temFilePrefix = "data_persistence_error_";
                AtomicInteger no = new AtomicInteger(1);
                ListUtils.partition(this.persistenceErrorDataList, EXCEL_FILE_MAX_ROW_COUNT).forEach(subList -> {
                    try (XSSFWorkbook errorDataExcelWorkbook = ExcelHelper.export(subList,
                            sheetName, errorIdentifyHead)) {
                        File errorFile = workbookToFile(no, temFilePrefix, errorDataExcelWorkbook);
                        errorFileList.add(errorFile);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });
            }
        }
        if (CollectionUtils.isEmpty(errorFileList)) {
            broadcast(new ExcelPersistenceProgressInfo(
                    ExcelDataDisposeProgressConstants.DONE,
                    100,
                    100,
                    "导入完成,等待数据入库,请稍后!",
                    taskId,
                    fileId,
                    taskName,
                    fileName,
                    "-1"
            ));
        } else {
            try {
                this.errorTemplateZipFile = File.createTempFile("导入错误_" + UUID.fastUUID(),
                        ".zip");
                //压缩
                ZipUtil.zip(errorFileList, this.errorTemplateZipFile);
                //删除临时文件
                errorFileList.parallelStream().forEach(file -> FileUtils.deleteQuietly(file));
                //上传文件
                this.errorFileDownloadUrl = uploadErrorFile(getMultipartFile());

                broadcast(new ExcelPersistenceProgressInfo(
                        ExcelDataDisposeProgressConstants.DONE_ERROR,
                        100,
                        100,
                        "导入完成,但发现了一些错误!",
                        taskId,
                        fileId,
                        taskName,
                        fileName,
                        "-1"
                ) {
                    @Getter
                    private String errorFileDownloadUrl = SimpleDataImport.this.errorFileDownloadUrl;
                });
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    @Override
    protected void persistence(int batchNumber, List<T> list) {
        this.doPersistence(batchNumber, list);
    }

    @Override
    protected void execute() throws Exception {
        super.execute();
    }


    @Override
    protected void parseError(List<ExcelImportErrorData> excelImportErrorData) {
        this.done();
    }

    /**
     * excel workbook转换成文件
     *
     * @param no            文件编号
     * @param temFilePrefix 文件前缀
     * @param workbook      内存中的workbook
     * @return
     * @throws IOException
     */
    private File workbookToFile(AtomicInteger no, String temFilePrefix, XSSFWorkbook workbook) throws IOException {
        File errorFile = File.createTempFile(temFilePrefix + no.getAndIncrement() + "_" + UUID.fastUUID(),
                ".xls");
        try (FileOutputStream stream = new FileOutputStream(errorFile)) {
            workbook.write(stream);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return errorFile;
    }

    /**
     * 组装上传文件
     *
     * @return
     */
    private CommonsMultipartFile getMultipartFile() {
        DiskFileItemFactory diskFileItemFactory = new DiskFileItemFactory();
        FileItem fileItem = diskFileItemFactory.createItem("file", MediaType.MULTIPART_FORM_DATA_VALUE,
                true, this.errorTemplateZipFile.getName());
        try (FileInputStream input = new FileInputStream(this.errorTemplateZipFile);
             OutputStream output = fileItem.getOutputStream()) {
            IOUtils.copy(input, output);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new CommonsMultipartFile(fileItem);
    }

    /**
     * 上传错误文件文件
     *
     * @param templateFile 错误信息文件
     * @return 文件下载地址
     */
    protected abstract String uploadErrorFile(CommonsMultipartFile templateFile);

    /**
     * 处理进度广播
     *
     * @param content 消息内容
     */
    protected abstract void broadcast(Object content);

    /**
     * 执行持久化数据
     *
     * @param datas
     */
    protected abstract void doPersistence(int batchNumber, Collection<T> datas);
}

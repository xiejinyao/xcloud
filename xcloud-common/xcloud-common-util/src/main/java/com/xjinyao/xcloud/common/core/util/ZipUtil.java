package com.xjinyao.xcloud.common.core.util;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;

import java.io.*;
import java.util.Enumeration;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

/**
 * @author 谢进伟
 * @description
 * @createDate 2020/7/23 15:35
 */
@Slf4j
public class ZipUtil extends cn.hutool.core.util.ZipUtil {


    /**
     * 压缩文件
     *
     * @param srcfile 需要压缩的文件列表
     * @param zipfile 压缩后的文件
     */
    public static void zip(List<File> srcfile, File zipfile) {
        byte[] buf = new byte[1024];
        try {
            if (srcfile != null && !srcfile.isEmpty()) {
                ZipOutputStream out = new ZipOutputStream(new FileOutputStream(zipfile));
                for (File file : srcfile) {
                    FileInputStream in = new FileInputStream(file);
                    out.putNextEntry(new ZipEntry(file.getName()));
                    int len;
                    while ((len = in.read(buf)) > 0) {
                        out.write(buf, 0, len);
                    }
                    closeQuietly(in);
                }
                closeQuietly(out);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 将指定目录压缩成ZIP
     *
     * @param srcDir           压缩文件夹路径
     * @param out              压缩文件输出流
     * @param KeepDirStructure 是否保留原来的目录结构,true:保留目录结构;
     *                         false:所有文件跑到压缩包根目录下(注意：不保留目录结构可能会出现同名文件,会压缩失败)
     * @throws RuntimeException 压缩失败会抛出运行时异常
     */
    public static void zip(String srcDir, OutputStream out, boolean KeepDirStructure)
            throws RuntimeException {
        long start = System.currentTimeMillis();
        ZipOutputStream zos = null;
        try {
            zos = new ZipOutputStream(out);
            File sourceFile = new File(srcDir);
            compress(sourceFile, zos, sourceFile.getName(), KeepDirStructure);
            long end = System.currentTimeMillis();
            log.info("压缩完成，耗时：" + (end - start) + " ms");
        } catch (Exception e) {
            throw new RuntimeException("zip error from ZipUtils", e);
        } finally {
            closeQuietly(zos);
        }
    }

    /**
     * 将文件列表压缩成ZIP
     *
     * @param srcFiles 需要压缩的文件列表
     * @param out      压缩文件输出流
     * @throws RuntimeException 压缩失败会抛出运行时异常
     */
    public static void zip(List<File> srcFiles, OutputStream out) throws RuntimeException {
        long start = System.currentTimeMillis();
        ZipOutputStream zos = null;
        try {
            zos = new ZipOutputStream(out);
            for (File srcFile : srcFiles) {
                int buffSize = 2 * 1024;
                byte[] buf = new byte[buffSize];
                zos.putNextEntry(new ZipEntry(srcFile.getName()));
                int len;
                FileInputStream in = new FileInputStream(srcFile);
                while ((len = in.read(buf)) != -1) {
                    zos.write(buf, 0, len);
                }
                zos.closeEntry();
                in.close();
            }
            long end = System.currentTimeMillis();
            log.info("压缩完成，耗时：" + (end - start) + " ms");
        } catch (Exception e) {
            throw new RuntimeException("zip error from ZipUtils", e);
        } finally {
            closeQuietly(zos);
        }
    }


    /**
     * 递归压缩方法
     *
     * @param sourceFile       源文件
     * @param zos              zip输出流
     * @param name             压缩后的名称
     * @param KeepDirStructure 是否保留原来的目录结构,true:保留目录结构;
     *                         false:所有文件跑到压缩包根目录下(注意：不保留目录结构可能会出现同名文件,会压缩失败)
     * @throws Exception
     */
    private static void compress(File sourceFile, ZipOutputStream zos, String name,
                                 boolean KeepDirStructure) throws Exception {
        byte[] buf = new byte[2 * 1024];
        if (sourceFile.isFile()) {
            // 向zip输出流中添加一个zip实体，构造器中name为zip实体的文件的名字
            zos.putNextEntry(new ZipEntry(name));
            // copy文件到zip输出流中
            int len;
            FileInputStream in = new FileInputStream(sourceFile);
            while ((len = in.read(buf)) != -1) {
                zos.write(buf, 0, len);
            }
            // Complete the entry
            zos.closeEntry();
            in.close();
        } else {
            File[] listFiles = sourceFile.listFiles();
            if (listFiles == null || listFiles.length == 0) {
                // 需要保留原来的文件结构时,需要对空文件夹进行处理
                if (KeepDirStructure) {
                    // 空文件夹的处理
                    zos.putNextEntry(new ZipEntry(name + "/"));
                    // 没有文件，不需要文件的copy
                    zos.closeEntry();
                }

            } else {
                for (File file : listFiles) {
                    // 判断是否需要保留原来的文件结构
                    if (KeepDirStructure) {
                        // 注意：file.getName()前面需要带上父文件夹的名字加一斜杠,
                        // 不然最后压缩包中就不能保留原来的文件结构,即：所有文件都跑到压缩包根目录下了
                        compress(file, zos, name + "/" + file.getName(), KeepDirStructure);
                    } else {
                        compress(file, zos, file.getName(), KeepDirStructure);
                    }

                }
            }
        }
    }

    /**
     * 解压文件到压缩文件当前目录
     *
     * @param zipfile 压缩文件
     */
    public static void unZip(File zipfile) {
        unZip(zipfile, null);
    }

    /**
     * 解压缩
     *
     * @param zipfile        需要解压缩的文件
     * @param unzipTargetDir 解压后的目标目录
     */
    public static void unZip(File zipfile, String unzipTargetDir) {
        try {
            String zipfileParentFilePath = zipfile.getParentFile().getPath();
            if (StringUtils.isBlank(unzipTargetDir)) {
                unzipTargetDir = zipfileParentFilePath;
            }
            File descDirFile = new File(unzipTargetDir);
            if (!descDirFile.exists()) {
                //创建解压目录
                descDirFile.mkdirs();
            }
            ZipFile zf = new ZipFile(zipfile);
            for (Enumeration entries = zf.entries(); entries.hasMoreElements(); ) {
                ZipEntry entry = ((ZipEntry) entries.nextElement());
                if (entry.isDirectory()) {//文件夹
                    File childrenDir = new File(unzipTargetDir + File.separator + entry.getName());
                    if (!childrenDir.exists()) {
                        childrenDir.mkdirs();
                    }
                } else {//文件
                    String zipEntryName = entry.getName();
                    InputStream in = null;
                    try {
                        in = zf.getInputStream(entry);
                        File f = new File(unzipTargetDir + File.separator + zipEntryName);
                        FileUtils.copyInputStreamToFile(in, f);
                    } catch (IOException e) {
                        e.printStackTrace();
                    } finally {
                        closeQuietly(in);
                    }
                }
            }
            zf.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void closeQuietly(Closeable closeable) {
        try {
            if (closeable != null) {
                closeable.close();
            }
        } catch (IOException ioe) {
            // ignore
        }
    }
}

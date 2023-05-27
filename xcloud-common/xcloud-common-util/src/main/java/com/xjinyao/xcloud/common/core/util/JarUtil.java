package com.xjinyao.xcloud.common.core.util;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.jar.Attributes;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.Manifest;

/**
 * @author 谢进伟
 * @description Jar包工具
 * @createDate 2021/3/29 11:29
 */
public class JarUtil {

    /**
     * 读取jar包中指定文件名的内容
     *
     * @param jarFilePath Jar包路径
     * @param name        需要读取的文件名称
     * @return
     */
    public static String readJarFile(String jarFilePath, String name) {
        String result = null;
        if (!new File(jarFilePath).exists()) {
            return null;
        }
        try (JarFile jarFile = new JarFile(jarFilePath)) {
            Enumeration entries = jarFile.entries();
            while (entries.hasMoreElements()) {
                JarEntry jarEntry = (JarEntry) entries.nextElement();
                if (jarEntry.getName().equals(name)) {
                    try (InputStream is = jarFile.getInputStream(jarEntry)) {
                        result = IOUtils.toString(is, StandardCharsets.UTF_8);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 读取jar包中 META-INF/MANIFEST.MF 的内容
     *
     * @param jarFilePath Jar包路径
     * @return
     */
    public static Map<String, String> readJarManifestFile(String jarFilePath) {
        if (!new File(jarFilePath).exists()) {
            return null;
        }
        Map<String, String> result = new HashMap<>();
        try (JarFile jarFile = new JarFile(jarFilePath)) {
            Manifest manifest = jarFile.getManifest();
            Attributes mainAttributes = manifest.getMainAttributes();
            Set<Object> objects = mainAttributes.keySet();
            objects.forEach(k -> result.put(StringUtils.trim(k + ""), mainAttributes.getOrDefault(k, "")
                    .toString()));
            return result;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 判断jar包中是否包含指定的文件名
     *
     * @param jarFilePath Jar包路径
     * @param name        需要读取的文件名称
     * @return
     */
    public static boolean containsName(String jarFilePath, String name) {
        try (JarFile jarFile = new JarFile(jarFilePath)) {
            Enumeration entries = jarFile.entries();
            while (entries.hasMoreElements()) {
                JarEntry jarEntry = (JarEntry) entries.nextElement();
                if (jarEntry.getName().equals(name)) {
                    return true;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
}

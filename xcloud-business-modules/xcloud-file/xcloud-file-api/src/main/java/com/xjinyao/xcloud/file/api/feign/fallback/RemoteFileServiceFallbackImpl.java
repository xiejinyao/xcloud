package com.xjinyao.xcloud.file.api.feign.fallback;

import com.xjinyao.xcloud.common.core.util.R;
import com.xjinyao.xcloud.file.api.feign.RemoteSysFileService;
import com.xjinyao.xcloud.file.api.vo.SysFileVO;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * @author 谢进伟
 * @description 文件服务调用失败回调
 * @createDate 2020/6/9 9:28
 */
@Slf4j
@Component
public class RemoteFileServiceFallbackImpl implements RemoteSysFileService {

    @Setter
    private Throwable cause;


    @Override
    public R<Map<String, String>> getJarFileManifest(Integer id, String from) {
        log.error("读取jar包中 META-INF/MANIFEST.MF 的内容失败,文件Id:{}", id, cause);
        return R.failed(Collections.emptyMap());
    }

    @Override
    public R<Boolean> checkJarFileContainsFile(Integer id, String filePath, String from) {
        log.error("检查jar包中是否存在指定的文件失败,文件Id:{}", id, cause);
        return R.failed(Boolean.FALSE);
    }

    @Override
    public R<SysFileVO> getFile(Integer id, String from) {
        log.error("获取文件失败,文件Id:{}", id, cause);
        return R.failed();
    }

    @Override
    public R<List<SysFileVO>> getFiles(Collection<Integer> fileIdList, String from) {
        log.error("获取文件失败,文件fileIdList:{}", fileIdList, cause);
        return R.failed(Collections.emptyList());
    }


    @Override
    public R<Boolean> deleteFile(Integer id, String from) {
        log.error("删除文件失败,文件Id:{}", id, cause);
        return R.failed(Boolean.FALSE);
    }

    @Override
    public R<Boolean> deleteFiles(Collection<Integer> fileIdList, String from) {
        log.error("删除文件失败,文件fileIdList:{}", fileIdList, cause);
        return R.failed(Boolean.FALSE);
    }

    @Override
    public R<Collection<SysFileVO>> uploadFileToServerDir(MultipartFile multipartFile, String relativePath, Boolean isSaveLog, String from) {
        log.error("上传文件到文件服务器指定目录失败，相对存储目录{}", relativePath, cause);
        return R.failed(Collections.emptyList());
    }
}

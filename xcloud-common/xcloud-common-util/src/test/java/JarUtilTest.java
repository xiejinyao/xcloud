import com.xjinyao.xcloud.common.core.util.JarUtil;
import org.apache.commons.lang3.StringUtils;
import org.junit.Test;

import java.io.File;
import java.util.Map;

/**
 * @author 谢进伟
 * @description JarUtil 测试
 * @createDate 2021/3/29 11:44
 */
public class JarUtilTest {

    @Test
    public void test01() {
        String jarFilePath = "F:\\Workspaces\\idea\\dhy_xcloud\\platform-business-modules\\platform-device\\platform-device-data-parse\\plugins\\plugin-shenou\\target\\plugin-shenou.jar";
        if (!new File(jarFilePath).exists()) {
            return;
        }
        Map<String, String> map = JarUtil.readJarManifestFile(jarFilePath);
        String pluginClass = map.get("Plugin-Class");
        String pluginClassName = StringUtils.replace(pluginClass, ".", "/") + ".class";
        System.out.println(pluginClassName);
        boolean b = JarUtil.containsName(jarFilePath, pluginClassName);
        System.out.println(b);
    }
}

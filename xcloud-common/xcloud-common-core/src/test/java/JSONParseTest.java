import com.xjinyao.xcloud.common.core.util.JSONParse;
import org.junit.Test;

/**
 * @author 谢进伟
 * @description JSON解析测试
 * @createDate 2021/3/2 19:07
 */
public class JSONParseTest {

    @Test
    public void test01() {
        //构造json字符串
        String jsonContent = "{\"projectName\":\"asdfasdf\",\"projectInfo\":{\"author\":\"test\",\"aa\":{\"bb\":3434},\"version\":1.0}}";
        String val = JSONParse.getNodeValue(jsonContent, "JSON.projectInfo.aa.bb");
        System.out.println(val);//执行结果：test
    }

    @Test
    public void test02() {
        //构造json字符串
        String jsonContent = "{\"projectName\":\"JSON\",\"projectInfo\":{\"author\":\"test\",\"aa\":{\"bb\":[{\"cc\":334}]},\"version\":1.0}}";
        System.out.println(jsonContent);
        String val = JSONParse.getNodeValue(jsonContent, "JSON.projectInfo.aa.bb[0].cc");
        System.out.println(val);//执行结果：test
    }
}

import com.ulisesbocchio.jasyptspringboot.encryptor.DefaultLazyEncryptor;
import org.jasypt.encryption.StringEncryptor;
import org.springframework.core.env.StandardEnvironment;

public class EncryptorTest {

    public static void main(String[] args) {
        //对应配置文件中对应的根密码
        String root = "tcs";
        System.setProperty("jasypt.encryptor.password", root);
        StringEncryptor stringEncryptor = new DefaultLazyEncryptor(new StandardEnvironment());
        System.out.println(stringEncryptor.encrypt(root));
        String str = "tcs";
        System.out.println(stringEncryptor.encrypt(str));
        System.out.println(stringEncryptor.encrypt(str));


        System.out.println(stringEncryptor.decrypt("BpEjTS0Zsk+ZvADykvlChg=="));
        System.out.println(stringEncryptor.decrypt("ML6cka0s5loPsAWvep4xgA=="));

        System.out.println("+++++++++++++");

        System.out.println(stringEncryptor.decrypt("xYeNUiEFnsibKt5fp4sSiw=="));
        System.out.println(stringEncryptor.decrypt("cfJNJm40/4DVo/YoNj4Sew=="));

        /**
         *
         *
         client-id: ENC(BpEjTS0Zsk+ZvADykvlChg==)
         client-secret: ENC(ML6cka0s5loPsAWvep4xgA==)
         */

    }
}

package com.xjinyao.xcloud.admin;

import com.ulisesbocchio.jasyptspringboot.encryptor.DefaultLazyEncryptor;
import org.jasypt.encryption.StringEncryptor;
import org.springframework.core.env.StandardEnvironment;

import java.util.Scanner;

public class EncryptorGen {

    public static void main(String[] args) {
        //对应配置文件中对应的根密码
        System.out.printf("请输入根密码:");
        Scanner scanner = new Scanner(System.in);
        String root = scanner.nextLine();
        System.setProperty("jasypt.encryptor.password", root);
        StringEncryptor stringEncryptor = new DefaultLazyEncryptor(new StandardEnvironment());
        System.out.printf("请输入需要加密的字符串:");
        String str = scanner.nextLine();
        System.out.printf("请输入加密个数:");
        int count = scanner.nextInt();
        for (int i = 0; i < count; i++) {
            System.out.println(stringEncryptor.encrypt(str));
        }
    }
}

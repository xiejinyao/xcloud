import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Objects;

/**
 * @author 谢进伟
 * @createDate 2023/5/27 09:35
 */
public class T1 {

    public static void main(String[] args) {
        String root = "/Users/yang/IdeaProjects/xcloud";

        File rootDir = new File(root);
        createSourceFile(rootDir);
    }

    private static void createSourceFile(File dir) {
        System.out.println("check dir" + dir.getPath());
        if (dir.exists() && dir.getPath().endsWith("src/main/java")) {
            File sourceFile = new File(dir.getPath() + File.separator + "source.txt");
            if (!sourceFile.exists()) {
                try {
                    sourceFile.createNewFile();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                System.out.println(sourceFile.getPath() + "\tcreated");
            }
        }
        Arrays.stream(Objects.requireNonNull(dir.listFiles())).filter(File::isDirectory).forEach(T1::createSourceFile);
    }
}

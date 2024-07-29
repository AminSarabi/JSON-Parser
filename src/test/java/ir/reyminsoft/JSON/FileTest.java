package ir.reyminsoft.json;

import java.io.FileInputStream;
import java.io.IOException;

public class FileTest implements TestClass {

    public static void main(String[] args) {
        TestClassRunner.run(FileTest.class);
    }

    public static void test_large_file() throws IOException {
        FileInputStream fileInputStream = new FileInputStream("src/test/java/ir/reyminsoft/json/large-file.json");
        byte[] bytes = new byte[fileInputStream.available()];
        int read = fileInputStream.read(bytes);
        fileInputStream.close();
        System.out.println("read " + read + " bytes from the file.");
        final String text = new String(bytes).replaceAll("\\n", "");
        JSONArray jsonArray = new JSONArray(text);
        TestClassRunner.assertEquals(text, jsonArray.toString());
    }
}

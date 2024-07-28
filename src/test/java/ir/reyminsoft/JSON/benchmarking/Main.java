package ir.reyminsoft.json.benchmarking;

import ir.reyminsoft.json.JSONArray;

import java.io.FileInputStream;
import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException {

        FileInputStream fileInputStream = new FileInputStream("src/test/java/ir/reyminsoft/json/benchmarking/large-file.json");
        byte[] bytes = new byte[fileInputStream.available()];
        int read = fileInputStream.read(bytes);
        System.out.println("read " + read + " bytes from the file.");
        final String text = new String(bytes);

        fileInputStream.close();
        BenchmarkTool.benchmark(() -> {
            JSONArray jsonArray = new JSONArray(text);
        }, 500); //result: 7.3

        BenchmarkTool.benchmark(() -> {
            JSONArray jsonArray = new JSONArray(text);
            jsonArray.toString();
        }, 500); //result: 23

    }
}

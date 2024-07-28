package ir.reyminsoft.JSON.benchmarking;

import ir.reyminsoft.JSON.JSONArray;

import java.io.FileInputStream;
import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException {

        FileInputStream fileInputStream = new FileInputStream("src/test/java/ir/reyminsoft/JSON/benchmarking/large-file.json");
        byte[] bytes = new byte[fileInputStream.available()];
        int read = fileInputStream.read(bytes);
        System.out.println("read " + read + " bytes from the file.");
        final String text = new String(bytes);

        fileInputStream.close();
        BenchmarkTool.benchmark(() -> {
            JSONArray jsonArray = new JSONArray(text);
        }, 500); //result: 7.6

        BenchmarkTool.benchmark(() -> {
            JSONArray jsonArray = new JSONArray(text);
            jsonArray.toString();
        }, 500); //result: 23

    }
}

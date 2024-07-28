package ir.reyminsoft.JSON.benchmarking;

import ir.reyminsoft.JSON.JSONArray;

import java.io.FileInputStream;
import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException {

        final String text = new String(new FileInputStream("src/test/java/ir/reyminsoft/JSON/benchmarking/large-file.json").readAllBytes());

        BenchmarkTool.benchmark(() -> {
            JSONArray jsonArray = new JSONArray(text);
        }, 500); //result: 11

        /*BenchmarkTool.benchmark(() -> {
            JSONArray jsonArray = new JSONArray(text);
            jsonArray.toString();
        }, 1000); //result: 24*/

    }
}

package ir.reyminsoft.JSON.benchmarking;

import ir.reyminsoft.JSON.JSONArray;

import java.io.FileInputStream;
import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException {

        final String text = new String(new FileInputStream("src/test/java/ir/reyminsoft/JSON/benchmarking/large-file.json").readAllBytes());
        long time;
        long sum = 0;
        int runCount = 50;
        for (int x = 0; x != runCount; x++) {
            time = System.currentTimeMillis();
            JSONArray jsonArray = new JSONArray(text);
            time = System.currentTimeMillis() - time;
            sum += time;
            System.out.println("took:" + time);
        }

        System.out.println("average time " + sum / runCount);
    }
}

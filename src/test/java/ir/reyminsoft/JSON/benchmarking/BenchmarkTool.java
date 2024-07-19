package ir.reyminsoft.JSON.benchmarking;


import java.util.Random;

public class BenchmarkTool {


    public static void benchmark(Runnable runnable, int runCount) {
        System.out.println("warming up...");
        Object[][] objects = new Object[runCount][];
        for (int x = 0; x != 500; x++) {
            objects[x % runCount] = perform_standard(); //warm up
        }
        long runnableSum = 0;
        long controlSum = 0;

        System.out.println("performing...");
        for (int i = 0; i != runCount; i++) {
            long before = System.nanoTime();
            runnable.run();
            runnableSum += System.nanoTime() - before;
            before = System.nanoTime();
            objects[i] = perform_standard();
            controlSum += System.nanoTime() - before;
        }
        long average = runnableSum / runCount;
        long averageControl = controlSum / runCount;

        long comparative = runnableSum / controlSum;

        System.out.println("comparative score: " + comparative);
        System.out.println("sum run: " + get(runnableSum));
        System.out.println("sum control: " + get(controlSum));
        System.out.println("average run: " + get(average));
        System.out.println("average control run: " + get(averageControl));
        System.out.println(objects.length); //so the compiler does not optimize this.

        System.out.println("-----------------------------------------------");
    }

    private static String get(long l) {
        long ms = l / (1000 * 1000);
        l = l % 1000 * 1000;
        long ns = l / 1000;
        l = l % 1000;
        return ms + "." + ns;
    }

    private static Object[] perform_standard() {
        StringBuilder stringBuilder = new StringBuilder();
        Random random = new Random();
        long something = 0;
        for (int x = 0; x != 100000; x++) {
            Object o = new Object();
            stringBuilder.append(random.nextInt());
            something += random.nextLong(10000);
        }
        return new Object[]{something, stringBuilder};
    }

}

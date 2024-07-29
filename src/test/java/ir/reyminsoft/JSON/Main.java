package ir.reyminsoft.json;

public class Main {
    public static void main(String[] args) {
        TestClassRunner.run(ObjectTests.class);
        TestClassRunner.run(ArrayTests.class);
        TestClassRunner.run(EscaperTests.class);
        TestClassRunner.run(FileTest.class);
    }
}

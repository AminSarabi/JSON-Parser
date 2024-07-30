package ir.reyminsoft.json;

import static ir.reyminsoft.json.TestClassRunner.assertEquals;

public class SerializerTests implements TestClass {

    public static void main(String[] args) {
        TestClassRunner.run(SerializerTests.class);
    }


    public static void test_basic() throws Exception {
        class Some {
            public Some() {

            }

            String name;
        }
        Some first = new Some();
        first.name = "amin";
        long before = System.nanoTime();
        long took = System.nanoTime() - before;
        JSONObject jsonObject = Serializer.serialize(first);
        Some second = Serializer.deserialize(jsonObject, Some.class);
        assertEquals(first.name, second.name);
        Utils.print((double)took / 1000000d);


    }
}

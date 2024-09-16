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
        Utils.print((double) took / 1000000d);
    }


    public static void test_internal_object_null() {
        ObjectTypeOne one_1 = new ObjectTypeOne();
        JSONObject jsonObject = Serializer.serialize(one_1);
        ObjectTypeOne one_2 = Serializer.deserialize(jsonObject, ObjectTypeOne.class);
        assertEquals(one_2.objectTypeTwo, null);
        assertEquals(one_1, one_2);
    }

    public static void test_internal_object() {
        ObjectTypeOne one_1 = new ObjectTypeOne();
        ObjectTypeTwo two_1 = new ObjectTypeTwo();
        two_1.value = "testing here.";
        one_1.objectTypeTwo = two_1;
        JSONObject jsonObject = Serializer.serialize(one_1);
        ObjectTypeOne one_2 = Serializer.deserialize(jsonObject, ObjectTypeOne.class);
        assertEquals(one_2.objectTypeTwo, two_1);
        assertEquals(one_2.objectTypeTwo.value, "testing here.");
        assertEquals(one_1, one_2);
    }


    public static void test_internal_object_recursion() {
        ObjectTypeOne one_1 = new ObjectTypeOne();
        ObjectTypeTwo two_1 = new ObjectTypeTwo();
        two_1.value = "testing here.";
        one_1.objectTypeTwo = two_1;
        one_1.objectTypeTwo.objectTypeOne = one_1;
        JSONObject jsonObject = Serializer.serialize(one_1);
        ObjectTypeOne one_2 = Serializer.deserialize(jsonObject, ObjectTypeOne.class);
        assertEquals(one_2.objectTypeTwo, two_1);
        assertEquals(one_2.objectTypeTwo.value, "testing here.");
        assertEquals(one_2.objectTypeTwo.objectTypeOne, one_1);
        assertEquals(one_1, one_2);
    }


    public static void test_skip_static_fields(){
        ObjectTypeThree one_1 = new ObjectTypeThree();
        JSONObject jsonObject = Serializer.serialize(one_1);
        ObjectTypeThree one_2 = Serializer.deserialize(jsonObject, ObjectTypeThree.class);

    }


}

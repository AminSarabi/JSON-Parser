package ir.reyminsoft.json;


import static ir.reyminsoft.json.TestClassRunner.assertEquals;

public class ArrayTests implements TestClass {

    public static void main(String[] args) {
        TestClassRunner.run(ArrayTests.class);
    }

    public static void creates_empty_array() {
        final String string = "[]";
        final JSONArray jsonArray = new JSONArray(string);
        assertEquals(jsonArray.toString(), string);
    }

    public static void parses_internal_array() {
        final String string = "[[[],[]]]";
        final JSONArray jsonArray = new JSONArray(string);
        assertEquals(jsonArray.toString(), string);
        final JSONArray a = jsonArray.getJSONArray(0);
        assertEquals(a.toString(), "[[],[]]");
        final JSONArray b = a.getJSONArray(0);
        assertEquals(b.toString(), "[]");
        final JSONArray c = a.getJSONArray(1);
        assertEquals(c.toString(), "[]");
    }

    public static void parses_string() {
        final String value = " this is amin.";
        final String string = "[\"" + value + "\"]";
        final JSONArray jsonArray = new JSONArray(string);
        assertEquals(jsonArray.toString(), string);
        final String got = jsonArray.getString(0);
        assertEquals(got, value);

    }

    public static void parses_integer() {
        final int value = 7091;
        final String string = "[" + value + "]";
        final JSONArray jsonArray = new JSONArray(string);
        assertEquals(jsonArray.toString(), string);
        final int got = jsonArray.getInteger(0);
        assertEquals(got, value);

    }

    public static void parses_double() {
        final double value = 7.091;
        final String string = "[" + value + "]";
        final JSONArray jsonArray = new JSONArray(string);
        assertEquals(jsonArray.toString(), string);
        final double got = jsonArray.getDouble(0);
        assertEquals(got, value);

    }

    public static void parses_internal_json() {
        final String string = "[{\"amin\":7091},7091]";
        final JSONArray jsonArray = new JSONArray(string);
        assertEquals(jsonArray.toString(), string);
    }

    public static void handles_boolean() {
        final String string = "[true,false,false]";
        final JSONArray jsonArray = new JSONArray(string);
        assertEquals(jsonArray.toString(), string);
    }

    public static void handles_null() {
        final String string = "[true,null,null]";
        final JSONArray jsonArray = new JSONArray(string);
        assertEquals(jsonArray.toString(), string);
    }


}
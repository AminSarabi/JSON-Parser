package ir.reyminsoft.json;

import java.nio.charset.StandardCharsets;
import java.util.Hashtable;
import java.util.Random;

import static ir.reyminsoft.json.TestClassRunner.assertEquals;

public class ObjectTests implements TestClass {

    public static final Random random = new Random();

    public static void main(String[] args) {

        TestClassRunner.run(ObjectTests.class);

    }


    public static void test_create_from_string() {
        String jsonString = "{}";
        JSONObject jsonObject = new JSONObject(jsonString);
        assertEquals(jsonString, jsonObject.toString());
    }

    public static void removes_white_space() {
        String jsonString = "{ }";
        JSONObject jsonObject = new JSONObject(jsonString);
        assertEquals(jsonObject.toString(), jsonString.replaceAll("\\s", ""));
    }

    public static void throws_on_alone_braces() {
        for (String jsonString : new String[]{"{", "}", "{{}", " { } }", "{{{", " {}{{} {}"}) {
            try {
                new JSONObject(jsonString);
                throw new RuntimeException("did not throw for" + jsonString);
            } catch (JSONException ignored) {

            }
        }
    }

    public static void throwsOnIndependentBraces() {
        for (String jsonString : new String[]{"{} {}", "{{}} {}", "{   {} } {} {{}}"}) {
            try {
                new JSONObject(jsonString);
                throw new RuntimeException("did not throw for" + jsonString);
            } catch (JSONException ignored) {

            }
        }
    }

    public static void does_not_throw_on_recursive_braces() {
        for (String jsonString : new String[]{"{\"a\":{}}",
                "{\"a\":{" +
                        "\"b\":{" +
                        "\"c\":{" +
                        "\"d\":{" +
                        "}}}}}"}) {
            new JSONObject(jsonString);
        }
    }

    public static void parses_internal_object() {
        String str = "{\"a\":{}}";
        JSONObject object = new JSONObject(str);
        JSONObject object2 = object.getJSONObject("a");
        assertEquals(object2.toString(), "{}");
        assertEquals(object.toString(), str);

    }

    public static void parses_string() {
        String str = "{\"a\":\"hi there\",\"the other key\":\"  value \"}";
        JSONObject object = new JSONObject(str);
        assertEquals(object.getString("a"), "hi there");
        assertEquals(object.getString("the other key"), "  value ");
        assertEquals(object.toString(), str);
    }

    public static void parses_int() {
        String str = "{\"a\":7091}";
        JSONObject object = new JSONObject(str);
        int object2 = object.getInt("a");
        assertEquals(object2, 7091);
        assertEquals(object.toString(), str);
    }




    public static void parses_long() {
        for (int x = 0; x != 1000; x++) {
            long l = random.nextLong();
            String s = "{\"a\":" + l + "}";
            JSONObject o = new JSONObject(s);
            if (!o.toString().equals(s)) {
                o = new JSONObject(s);
            }
            assertEquals(o.toString(), s);
            if (!Utils.equals(o.getLong("a"), l)) {
                o = new JSONObject(s);
            }
            assertEquals(o.getLong("a"), l);
        }
    }

    /*
    * This is a failing test, because the number converter algorithm is not binary wise and small doubles are a problem.
    * but these tiny errors are negligible.
    * */
    public static void parses_double() {
        String str = "{\"a\":7.091}";
        JSONObject object = new JSONObject(str);
        double object2 = object.getDouble("a");
        assertEquals(object2, 7.091d);
        assertEquals(object.toString(), str);

        for (int x = 0; x != 1000; x++) {
            double d = random.nextDouble();
            String s = "{\"a\":" + d + "}";
            JSONObject o = new JSONObject(s);
            assertEquals(o.toString(), s);
            assertEquals(o.getDouble("a"), d);
        }
    }

    public static void parses_boolean() {
        String str = "{\"a\":true,\"b\":false}";
        JSONObject object = new JSONObject(str);
        boolean a = object.getBoolean("a");
        boolean b = object.getBoolean("b");
        assertEquals(a, true);
        assertEquals(b, false);
    }


    public static void parses_array() {
        String str = "{\"a\":[7091,\"amin\",{\"code\":7091}]}";
        JSONObject object = new JSONObject(str);
        assertEquals(object.toString(), str);
    }


    public static void special_characters_escaping() {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("key", "[][[]{} \" \" \"");
        String str = jsonObject.toString();
        JSONObject object = new JSONObject(str);
        assertEquals(object.toString(), jsonObject.toString());
    }

    public static void handles_null() {
        String str = "{\"a\":NULL,\"b\":null}";
        JSONObject object = new JSONObject(str);
        assertEquals(object.getString("a"), null);
    }


    public static String randomString() {
        StringBuilder str = new StringBuilder();
        String array = "{}\"\\(),.:#@!/-=[]abcdefghijklmnopqrstuvwxyz1234567890\n\r\t\b\f";
        String[] array2 = new String[]{"null", "false", "true", "\r\n"};
        for (int x = 0; x != 32; x++) {
            char ch = array.charAt(random.nextInt(array.length()));
            str.append(random.nextBoolean() ? Character.toLowerCase(ch) : Character.toUpperCase(ch));
            if (random.nextBoolean()) str.append(" ").append(array2[random.nextInt(array2.length)]).append(" ");
            if (random.nextBoolean()) str.append("  ");
            if (random.nextBoolean()) {
                byte[] bytes = new byte[8];
                random.nextBytes(bytes);
                str.append(new String(bytes, StandardCharsets.UTF_8));
            }
        }
        return str.toString();
    }


    public static void extreme_testing() {
        JSONObject jsonObject = new JSONObject();
        Hashtable<String, String> hashtable = new Hashtable<>();
        for (int x = 0; x != 10; x++) {
            String key = randomString();
            String value = randomString();
            jsonObject.put(key, value);
            hashtable.put(key, value);
        }
        Utils.print(jsonObject.toString());
        jsonObject = new JSONObject(jsonObject.toString());
        for (String key : hashtable.keySet()) {
            String value = hashtable.get(key);
            assertEquals(jsonObject.getString(key), value);
        }
    }

}

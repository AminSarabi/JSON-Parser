package ir.reyminsoft.JSON;

import java.nio.charset.StandardCharsets;
import java.util.Hashtable;
import java.util.Random;

import static ir.reyminsoft.JSON.TestClassRunner.assertEquals;

public class Test implements TestClass {

    public static Random random = new Random();

    public static void main(String[] args) {

        TestClassRunner.run(Test.class);

    }

    public static void parse_parse_empty_json() {

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

    public static void parses_double() {
        String str = "{\"a\":7.091}";
        JSONObject object = new JSONObject(str);
        double object2 = object.getDouble("a");
        assertEquals(object2, 7.091d);
        assertEquals(object.toString(), str);
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



    public static void escaper_tests() {
        for (int x = 0; x != 10000; x++) {
            String random = randomString();
            String escaped = JSONObject.stringifyEscaping(random);
            String deEscaped = JSONObject.stringifyDeEscaping(escaped);
            if (!Utils.equals(deEscaped, random)) {
                Utils.print("\n----------------------------------------");
                Utils.print(random);
                Utils.print("\n----------------------------------------");
                Utils.print(escaped);
                Utils.print("\n----------------------------------------");
                Utils.print(deEscaped);
                Utils.print("\n----------------------------------------");
            }
            assertEquals(deEscaped, random);
        }
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

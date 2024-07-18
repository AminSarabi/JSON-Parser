package ir.reyminsoft.JSON;

import java.util.Hashtable;
import java.util.Stack;

public class JSONObject {

    protected static final JSONEscaper escaper = new JSONEscaper();

    public static final Object NULL = new Object();
    private final Hashtable<String, Object> hashtable;


    public JSONObject(String jsonString) {
        this.hashtable = readObject(new Cursor(jsonString));
    }

    public JSONObject() {
        this.hashtable = new Hashtable<>();
    }

    public JSONObject(Hashtable<String, Object> hashtable) {
        this.hashtable = hashtable;
    }

    public static Hashtable<String, Object> readObject(Cursor cursor) {
        int beginCursor = cursor.currentIndex();
        Hashtable<String, Object> table = new Hashtable<>();
        boolean readingValue = false;
        Stack<Integer> braces = new Stack<>();
        String currentKey = null;
        while (cursor.hasNextChar()) {
            char ch = cursor.currentCharacter();
            switch (ch) {
                case '"':
                    if (!readingValue) {
                        currentKey = readStringValueUnescaped(cursor.increment());
                    } else {
                        StringType value = readStringValue(cursor.increment());
                        table.put(currentKey, value);
                        readingValue = false;
                        currentKey = null;
                    }
                    break;
                case '0':
                case '1':
                case '2':
                case '3':
                case '4':
                case '5':
                case '6':
                case '7':
                case '8':
                case '9':
                    if (readingValue) {
                        Object value = readNumeric(cursor);
                        table.put(currentKey, value);
                        readingValue = false;
                        currentKey = null;
                    } else {
                        throw new JSONException("unexpected character '" + ch + "' at " + cursor.currentIndex());
                    }
                    break;
                case 't':
                case 'f':
                    if (readingValue) {
                        Object value = readBoolean(cursor);
                        table.put(currentKey, value);
                        readingValue = false;
                        currentKey = null;
                    } else {
                        throw new JSONException("unexpected character '" + ch + "' at " + cursor.currentIndex());
                    }
                    break;
                case 'n':
                case 'N':
                    if (readingValue) {
                        cursor.assertNextChars(4);
                        String str = cursor.getRangeAsString(4);
                        if (str.equalsIgnoreCase("null")) {
                            table.put(currentKey, NULL);
                            readingValue = false;
                            currentKey = null;
                        } else {
                            throw new JSONException("unrecognized value : " + str);
                        }
                    } else {
                        throw new JSONException("unexpected character '" + ch + "' at " + cursor.currentIndex());
                    }
                case ',':
                    break;
                case ':':
                    readingValue = true;
                    break;
                case '{':
                    if (cursor.currentIndex() != beginCursor && braces.isEmpty())
                        throw new JSONException("independent brace opening at " + cursor);
                    if (currentKey != null) {
                        table.put(currentKey, new JSONObject(readObject(cursor)));
                        currentKey = null;
                        readingValue = false;
                        break;
                    }
                    braces.push(cursor.currentIndex());
                    break;
                case '}':
                    if (braces.empty())
                        throw new JSONException("} at position " + cursor + " closes nothing.");
                    braces.pop();
                    if (braces.isEmpty()) {
                        if (beginCursor != 0) return table;
                    }
                    break;

                case '[':
                    if (braces.isEmpty()) {
                        throw new JSONException("this is a json array.");
                    }
                    if (readingValue) {
                        table.put(currentKey, new JSONArray(JSONArray.readArray(cursor)));
                        readingValue = false;
                        currentKey = null;
                    } else {
                        throw new JSONException("unexpected character '" + ch + "' at " + cursor.currentIndex());
                    }
                    break;

            }/**/
            cursor.increment();
        }
        if (!braces.empty()) {
            throw new JSONException("the brace(s) opened at position(s) " + braces + " were never closed.");
        }
        return table;
    }

    public static boolean readBoolean(Cursor cursor) {
        char first = cursor.currentCharacter();
        int count = 0;
        if (first == 't') {
            count = 4;
        } else if (first == 'f') {
            count = 5;
        } else cursor.throwUnrecognizedCharacter();
        cursor.assertNextChars(count);
        return Boolean.parseBoolean(cursor.getRangeAsString(count));
    }

    public static Object readNumeric(Cursor cursor) {
        int beginIndex = cursor.currentIndex(); //we can safely assume that this index is a number.
        int endIndex = -1;
        while (cursor.hasNextChar()) {
            char ch = cursor.currentCharacter();
            if (Character.isWhitespace(ch)) {
                endIndex = cursor.currentIndex();
                break;
            } else if (ch == ',' || ch == '}') {
                endIndex = cursor.currentIndex();
                cursor.decrement();
                break;
            }
            cursor.increment();
        }
        String str = cursor.getRangeAsString(beginIndex, endIndex);
        if (str.contains(".")) {
            return Double.parseDouble(str);
        } else {
            return Integer.parseInt(str);
        }
    }

    public static StringType readStringValue(Cursor cursor) {
        int beginIndex = cursor.currentIndex();
        followString(cursor);
        return new StringType(cursor.getRangeAsString(beginIndex, cursor.currentIndex()));
    }

    public static String readStringValueUnescaped(Cursor cursor) {
        return escaper.unescapeHunting(cursor, '"');
    }

    private static void followString(Cursor cursor) {
        while (cursor.hasNextChar()) {
            char ch = cursor.currentCharacter();
            if (ch == '\\') {
                if (cursor.hasNextChars(1)) {
                    char nextChar = cursor.nextCharacter(1);
                    if (
                            nextChar == '"' || nextChar == '\\'
                    ) {
                        cursor.increment();
                        cursor.increment();
                        continue;
                    }
                }

            }
            if (ch == '"') {
                break;
            }
            cursor.increment();
        }
    }

    public static String stringifyEscaping(String s) {
        return escaper.escape(s);
    }


    public void put(String key, Object s) {
        this.hashtable.put(key, s);
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        toString(builder);
        return builder.toString();
    }

    public void toString(StringBuilder stringBuilder) {
        stringBuilder.append("{");
        boolean first = true;
        for (String key : hashtable.keySet()) {
            if (!first) {
                stringBuilder.append(',');
            } else {
                first = false;
            }
            Object value = hashtable.get(key);
            stringBuilder.append('"').append(stringifyEscaping(key)).append('"').append(':');
            if (value instanceof StringType) {
                stringBuilder.append('"').append(((StringType) value).getContentEscaped(escaper)).append('"');
            } else if (value instanceof String) {
                stringBuilder.append('"').append(escaper.escape((String) value)).append('"');
            } else if (value == NULL) {
                stringBuilder.append("null");
            } else if (value instanceof JSONObject) {
                ((JSONObject) value).toString(stringBuilder);
            } else if (value instanceof JSONArray) {
                ((JSONArray) value).toString(stringBuilder);
            } else {
                stringBuilder.append(value.toString());
            }
        }
        stringBuilder.append("}");
    }

    public JSONObject getJSONObject(String key) {
        Object object = hashtable.get(key);
        return (JSONObject) object;
    }

    public String getString(String key) {
        Object object = hashtable.get(key);
        if (object == NULL) return null;
        return ((StringType) object).getContentUnescaped(escaper);
    }

    public int getInt(String key) {
        Object object = hashtable.get(key);
        return (Integer) object;
    }

    public double getDouble(String key) {
        Object object = hashtable.get(key);
        return (Double) object;
    }

    public boolean getBoolean(String key) {
        Object object = hashtable.get(key);
        return (Boolean) object;
    }

    public <T> T get(String key) {
        Object object = hashtable.get(key);
        return (T) object;
    }


}

package ir.reyminsoft.JSON;

import java.util.Hashtable;
import java.util.Stack;

class JSONObject {

    protected static final ObjectEscaper escaper = new ObjectEscaper();

    static final Object NULL = new Object();
    private final Hashtable<String, Object> hashtable;


    public JSONObject(String jsonString) {
        this.hashtable = readObject(new Cursor(jsonString));
    }

    JSONObject() {
        this.hashtable = new Hashtable<>();
    }

    JSONObject(Hashtable<String, Object> hashtable) {
        this.hashtable = hashtable;
    }

    static Hashtable<String, Object> readObject(Cursor cursor) {
        int beginCursor = cursor.currentIndex();
        Hashtable<String, Object> table = new Hashtable<>();
        boolean readingValue = false;
        int openBracesCount = 0;
        String currentKey = null;
        while (cursor.hasNextChar()) {
            char ch = cursor.currentCharacter();
            switch (ch) {
                case '"':
                    if (!readingValue) {
                        currentKey = readStringValueUnescaped(cursor.increment());
                    } else {
                        Escapable value = readStringValue(cursor.increment());
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
                    if (cursor.currentIndex() != beginCursor && openBracesCount==0)
                        throw new JSONException("independent brace opening at " + cursor);
                    if (currentKey != null) {
                        table.put(currentKey, new JSONObject(readObject(cursor)));
                        currentKey = null;
                        readingValue = false;
                        break;
                    }
                    openBracesCount++;
                    break;
                case '}':
                    if (openBracesCount==0)
                        throw new JSONException("} at position " + cursor + " closes nothing.");
                    if (--openBracesCount==0) {
                        if (beginCursor != 0) return table;
                    }
                    break;

                case '[':
                    if (openBracesCount==0) {
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
        if (openBracesCount!=0) {
            throw new JSONException(openBracesCount + " open braces were never closed in this json string");
        }
        return table;
    }

    static boolean readBoolean(Cursor cursor) {
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

    static Object readNumeric(Cursor cursor) {
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

    static Escapable readStringValue(Cursor cursor) {
        int beginIndex = cursor.currentIndex();
        followString(cursor);
        return new Escapable(cursor.getRangeAsString(beginIndex, cursor.currentIndex()));
    }

    static String readStringValueUnescaped(Cursor cursor) {
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

    static String stringifyEscaping(String s) {
        return escaper.escape(s);
    }


    void put(String key, Object o) {
        if (o == null) throw new JSONException("putting null in json-object. if intended, use JSONObject.NULL instead");
        if (!(o instanceof String || o instanceof Integer || o instanceof Double ||
                o instanceof Boolean || o instanceof JSONArray
                || o instanceof JSONObject || o == JSONObject.NULL)) {
            throw new JSONException("unknown type to put in json-object: " + o.getClass());
        }
        this.hashtable.put(key, o);
    }

    @Override
    public String toString() { //todo if the content is not modified, use a cached string (weak reference)
        StringBuilder builder = new StringBuilder();
        toString(builder);
        return builder.toString();
    }

    void toString(StringBuilder stringBuilder) {
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
            if (value instanceof Escapable) {
                stringBuilder.append('"').append(((Escapable) value).getContentEscaped(escaper)).append('"');
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

    public JSONArray getJSONArray(String key) {
        return get(key);
    }

    public String getString(String key) {
        return get(key);
    }

    public int getInt(String key) {
        return get(key);
    }

    public double getDouble(String key) {
        return get(key);
    }

    public boolean getBoolean(String key) {
        return get(key);
    }

    public <T> T get(String key) {
        Object object = hashtable.get(key);
        if (object == null || object == NULL) return null;
        if (object instanceof Escapable) {
            return (T) ((Escapable) object).getContentUnescaped(escaper);
        }
        return (T) object;
    }


}

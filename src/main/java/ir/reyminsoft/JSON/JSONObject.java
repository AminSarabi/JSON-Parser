package ir.reyminsoft.JSON;

import java.util.Hashtable;

class JSONObject {

    protected static final ObjectEscaper escaper = new ObjectEscaper();

    static final Object NULL = new Object();
    private final Hashtable<String, Object> hashtable;


    int cachedStringLength = 128;
    public JSONObject(String jsonString) {
        this.hashtable = readObject(new Cursor(jsonString));
    }

    JSONObject() {
        this.hashtable = new Hashtable<>();
    }

    JSONObject(Hashtable<String, Object> hashtable) {
        this.hashtable = hashtable;
    }

    static Hashtable<String, Object> readObject(Cursor cursor, int openBracesCount) {
        int beginCursor = cursor.currentIndex();
        Hashtable<String, Object> table = new Hashtable<>();
        boolean readingValue = false;
        String currentKey = null;
        while (cursor.hasNextChar()) {
            char ch = cursor.currentCharacter();
            int characterIndex = cursor.currentIndex();
            cursor.increment();
            switch (ch) {
                case '"':
                    if (!readingValue) {
                        currentKey = readStringValueUnescaped(cursor);
                    } else {
                        table.put(currentKey, readStringValue(cursor));
                        readingValue = false;
                        currentKey = null;
                    }
                    break;
                case '+':
                case '-':
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
                        Object value;
                        boolean end = false;
                        int beginIndex = cursor.currentIndex(); //we can safely assume that this index is a number.
                        int endIndex = -1;
                        while (cursor.hasNextChar()) {
                            ch = cursor.currentCharacter();
                            if (Character.isWhitespace(ch)) {
                                endIndex = cursor.currentIndex();
                                break;
                            } else if (ch == ',') {
                                endIndex = cursor.currentIndex();
                                break;
                            } else if (ch == '}') {
                                end = true;
                                endIndex = cursor.currentIndex();
                                break;
                            }
                            cursor.increment();
                        }
                        String str = cursor.getRangeAsString(beginIndex - 1, endIndex);
                        if (str.contains(".")) {
                            value = Double.parseDouble(str);
                        } else {
                            value = Integer.parseInt(str);
                        }
                        table.put(currentKey, value);
                        if (end) return table;
                        readingValue = false;
                        currentKey = null;
                    } else {
                        throw new JSONException("unexpected character '" + ch + "' at " + characterIndex);
                    }
                    break;
                case 't':
                case 'T':
                    if (readingValue) {
                        table.put(currentKey, true);
                        cursor.increment(3);  //todo here we assume that the value is true. throw exception if not.
                        currentKey = null;
                        readingValue = false;
                        break;
                    } else {
                        throw new JSONException("unexpected character '" + ch + "' at " + characterIndex);
                    }
                case 'f':
                case 'F':
                    if (readingValue) {
                        table.put(currentKey, false);
                        cursor.increment(4); //todo here we assume that the value is true. throw exception if not.
                        currentKey = null;
                        readingValue = false;
                        break;
                    } else {
                        throw new JSONException("unexpected character '" + ch + "' at " + characterIndex);
                    }
                case 'n':
                case 'N':
                    if (readingValue) {
                        table.put(currentKey, NULL);
                        cursor.increment(3);
                        readingValue = false;
                        currentKey = null;
                    } else {
                        throw new JSONException("unexpected character '" + ch + "' at " + characterIndex);
                    }
                case ',':
                    break;
                case ':':
                    readingValue = true;
                    break;
                case '{':
                    if (characterIndex != beginCursor && openBracesCount == 0)
                        throw new JSONException("independent brace opening at " + characterIndex);
                    if (currentKey != null) {
                        table.put(currentKey, new JSONObject(readObject(cursor, 1)));
                        currentKey = null;
                        readingValue = false;
                        break;
                    }
                    openBracesCount++;
                    break;
                case '}':
                    if (openBracesCount == 0)
                        throw new JSONException("} at position " + cursor.currentIndex() + " closes nothing.");
                    if (--openBracesCount == 0) {
                        if (beginCursor != 0) return table;
                    }
                    break;

                case '[':
                    if (openBracesCount == 0) {
                        throw new JSONException("this is a json array.");
                    }
                    if (readingValue) {
                        table.put(currentKey, new JSONArray(JSONArray.readArray(cursor)));
                        readingValue = false;
                        currentKey = null;
                    } else {
                        throw new JSONException("unexpected character '" + ch + "' at " + characterIndex);
                    }
                    break;

            }
        }
        if (openBracesCount != 0) {
            throw new JSONException(openBracesCount + " open braces were never closed in this json string");
        }
        return table;
    }

    static Hashtable<String, Object> readObject(Cursor cursor) {
        return readObject(cursor, 0);
    }

    static Escapable readStringValue(Cursor cursor) {
        int beginIndex = cursor.currentIndex();
        followString(cursor);
        return new Escapable(new StringSubString(cursor.string, beginIndex, cursor.currentIndex() - 1));
    }

    static String readStringValueUnescaped(Cursor cursor) {
        return escaper.unescapeHunting(cursor, '"');
    }

    private static void followString(Cursor cursor) {
        while (cursor.hasNextChar()) {
            char ch = cursor.currentCharacter();
            cursor.increment();
            if (ch == '\\') {
                char nextChar = cursor.currentCharacter();
                if (
                        nextChar == '"' || nextChar == '\\'
                ) {
                    cursor.increment();
                    continue;
                }
            }
            if (ch == '"') {
                break;
            }
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
        StringBuilder builder = new StringBuilder(cachedStringLength);
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
                stringBuilder.append('"').append(((Escapable) value).getContentEscaped()).append('"');
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

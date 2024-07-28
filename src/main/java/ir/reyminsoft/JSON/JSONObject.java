package ir.reyminsoft.json;

import java.util.Hashtable;

public class JSONObject {

    static final ObjectEscaper escaper = new ObjectEscaper();

    static final Object NULL = new Object();
    private final Hashtable<String, Object> hashtable;


    final int cachedStringLength = 128;

    public JSONObject(final String jsonString) {
        this.hashtable = readObject(new Cursor(jsonString));
    }

    public JSONObject() {
        this.hashtable = new Hashtable<>();
    }

    JSONObject(final Hashtable<String, Object> hashtable) {
        this.hashtable = hashtable;
    }

    static Hashtable<String, Object> readObject(final Cursor cursor, int openBracesCount) {
        final int beginCursor = cursor.currentIndex();
        final Hashtable<String, Object> table = new Hashtable<>();
        boolean readingValue = false;
        String currentKey = null;
        while (cursor.hasNextChar()) {
            char ch = cursor.currentCharacter();
            final int characterIndex = cursor.currentIndex();
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
                        boolean end = false;
                        final int beginIndex = cursor.currentIndex(); //we can safely assume that this index is a number.
                        int endIndex = -1;
                        boolean dotSeen = false;
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
                            } else if (ch == '.') {
                                if (dotSeen) throw new JSONException("numeric value has more than one points");
                                dotSeen = true;
                            }
                            cursor.increment();
                        }
                        String str = cursor.getRangeAsString(beginIndex - 1, endIndex);
                        //ATTENTION: do not replace the following with ternary conditional. that converts both to double.
                        if (dotSeen) {
                            table.put(currentKey, Double.parseDouble(str));
                        } else {
                            table.put(currentKey, Integer.parseInt(str));
                        }
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

    static Hashtable<String, Object> readObject(final Cursor cursor) {
        return readObject(cursor, 0);
    }

    static Escapable readStringValue(final Cursor cursor) {
        final int beginIndex = cursor.currentIndex();
        followString(cursor);
        return new Escapable(new StringSubString(cursor.string, beginIndex, cursor.currentIndex() - 1));
    }

    static String readStringValueUnescaped(final Cursor cursor) {
        return escaper.unescapeHunting(cursor, '"');
    }

    private static void followString(final Cursor cursor) {
        while (cursor.hasNextChar()) {
            final char ch = cursor.currentCharacter();
            cursor.increment();
            if (ch == '\\') {
                final char nextChar = cursor.currentCharacter();
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

    static String stringifyEscaping(final String s) {
        return escaper.escape(s);
    }


    void put(final String key, final Object o) {
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
        final StringBuilder builder = new StringBuilder(cachedStringLength);
        toString(builder);
        return builder.toString();
    }

    void toString(final StringBuilder stringBuilder) {
        stringBuilder.append("{");
        boolean first = true;
        for (final String key : hashtable.keySet()) {
            if (!first) {
                stringBuilder.append(',');
            } else {
                first = false;
            }
            final Object value = hashtable.get(key);
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


    public JSONObject getJSONObject(final String key) {
        final Object object = hashtable.get(key);
        return (JSONObject) object;
    }

    public JSONArray getJSONArray(final String key) {
        return get(key);
    }

    public String getString(final String key) {
        return get(key);
    }

    public int getInt(final String key) {
        if (!hashtable.containsKey(key)) return 0;
        return (int) hashtable.get(key);
    }

    public double getDouble(final String key) {
        if (!hashtable.containsKey(key)) return 0;
        return (double) hashtable.get(key);
    }

    public boolean getBoolean(final String key) {
        return get(key);
    }

    public <T> T get(final String key) {
        Object o = hashtable.get(key);
        if (o == null || o == NULL) return null;
        if (o instanceof Escapable) {
            o = ((Escapable) o).getContentUnescaped(escaper);
        }
        @SuppressWarnings("unchecked") final T casted = (T) o;
        return casted;
    }


}

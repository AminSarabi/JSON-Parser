package ir.reyminsoft.json;

import java.util.LinkedHashMap;
import java.util.Map;

public class JSONObject {

    static final ObjectEscaper escaper = new ObjectEscaper();

    static final Object NULL = new Object();
    private final Map<String, Object> hashtable;


    final int cachedStringLength = 128;

    public JSONObject(final String jsonString) {
        this.hashtable = readObject(new Cursor(jsonString));
    }

    public JSONObject() {
        this.hashtable = new LinkedHashMap<>();
    }

    JSONObject(final Map<String, Object> hashtable) {
        this.hashtable = hashtable;
    }

    static Map<String, Object> readObject(final Cursor cursor, int openBracesCount) {
        final int beginCursor = cursor.currentIndex();
        final Map<String, Object> table = new LinkedHashMap<>();
        boolean readingValue = false;
        String currentKey = null;
        while (cursor.hasNextChar()) {
            final char endChar = '}';
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
                        table.put(currentKey, readNumeric(cursor, ch, endChar));
                        if (cursor.isMarked()) {
                            cursor.clearMark();
                            return table;
                        }
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

    static Number readNumeric(Cursor cursor, char ch, char endChar) {
        Number n;
        boolean isNegative = ch == '-';
        boolean exponentIsNegative = false;
        long literal = isNegative ? 0 : ch - '0';
        long fractional = 0;
        long fractionalExponent = 0;
        double exponent = 0;
        int mode = 0;
        loop:
        while (cursor.hasNextChar()) {
            ch = cursor.currentCharacter();
            if (ch == endChar) {
                cursor.increment();
                cursor.mark();
                break;
            }
            switch (ch) {
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
                    if (mode == 0) literal = literal * 10L + (ch - '0');
                    if (mode == 1) {
                        fractionalExponent++;
                        fractional = fractional * 10L + (ch - '0');
                    }
                    if (mode == 2) throw new JSONException("invalid numeric value.");
                    if (mode == 3) exponent = exponent * 10L + (ch - '0');
                    break;
                case '-':
                    exponentIsNegative = false;
                    if (mode != 2) throw new JSONException("invalid numeric value.");
                    mode = 4;
                    break;
                case '+':
                    exponentIsNegative = true;
                    if (mode != 2) throw new JSONException("invalid numeric value.");
                    mode = 4;
                    break;
                case '.':
                    if (mode == 1) throw new JSONException("numeric value with more than 1 point.");
                    mode++;
                    break;
                case 'E':
                case 'e':
                    if (mode == 2) throw new JSONException("invalid numeric value.");
                    mode = 2;
                    break;
                case ',':
                    cursor.increment();
                    break loop;
                default:
                    break loop;
            }
            cursor.increment();
        }
        double pow = Math.pow(10, exponent * (exponentIsNegative ? -1 : 1));
        int factor = isNegative ? -1 : 1;
        if (fractionalExponent != 0) { //has fractional, needs to be parsed as double.
            if (fractional == 0) {
                double number = literal * pow;
                n = number * (double) factor;
            } else {
                double number = (literal + fractional * Math.pow(10, -fractionalExponent)) * pow;
                n = number * (double) factor;
            }
        } else {
            long number = literal * (long) pow;
            if (number > Integer.MAX_VALUE) n = number * factor;
            else n = (int) number * factor;
        }
        return n;
    }

    static Map<String, Object> readObject(final Cursor cursor) {
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

    public long getLong(final String key) {
        if (!hashtable.containsKey(key)) return 0;
        return (long) hashtable.get(key);
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

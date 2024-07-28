package ir.reyminsoft.json;

import java.util.ArrayList;
import java.util.List;

import static ir.reyminsoft.json.JSONObject.NULL;

public class JSONArray {
    private static final Escaper escaper;


    static {
        escaper = new ArrayEscaper();
    }

    private final List<Object> objectList;

    public JSONArray() {
        this.objectList = new ArrayList<>();
    }

    JSONArray(final List<Object> objectList) {
        this.objectList = objectList;
    }


    int cachedStringLength = 128;

    public JSONArray(final String string) {
        cachedStringLength = string.length();
        final Cursor cursor = new Cursor(string);
        cursor.assertChar('[');
        this.objectList = readArray(cursor.increment());
    }

    static List<Object> readArray(final Cursor cursor) {
        final List<Object> list = new ArrayList<>();
        while (cursor.hasNextChar()) {
            char ch = cursor.currentCharacter();
            cursor.increment();
            switch (ch) {
                case ',':
                    break;
                case '[':
                    list.add(new JSONArray(readArray(cursor)));
                    break;
                case ']':
                    return list;
                case '{':
                    list.add(new JSONObject(JSONObject.readObject(cursor, 1)));
                    break;
                case '"':
                    list.add(JSONObject.readStringValue(cursor));
                    break;

                case 't':
                case 'T':
                    list.add(true);
                    cursor.increment(3);
                    break;
                case 'f':
                case 'F':
                    list.add(false);
                    cursor.increment(4);
                    break;
                case 'n':
                case 'N':
                    list.add(NULL);
                    cursor.increment(3);
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
                        } else if (ch == ']') {
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
                        list.add(Double.parseDouble(str));
                    } else {
                        list.add(Integer.parseInt(str));
                    }
                    if (end) return list;
                    break;
            }
        }


        return list;
    }


    @Override
    public String toString() { //todo if the content is not modified, use a cached string (weak reference)
        final StringBuilder stringBuilder = new StringBuilder(cachedStringLength);
        toString(stringBuilder);
        return stringBuilder.toString();
    }

    void toString(final StringBuilder stringBuilder) {
        stringBuilder.append("[");
        boolean first = true;
        for (final Object o : objectList) {
            if (!first) {
                stringBuilder.append(',');
            } else {
                first = false;
            }
            if (o instanceof Escapable) {
                final Escapable escapable = (Escapable) o;
                stringBuilder.append('"').append(escapable.getContentEscaped()).append('"');
            } else if (o instanceof String) {
                stringBuilder.append('"').append(escaper.escape((String) o)).append('"');
            } else if (o == NULL) {
                stringBuilder.append("null");
            } else if (o instanceof JSONObject) {
                ((JSONObject) o).toString(stringBuilder);
            } else if (o instanceof JSONArray) {
                ((JSONArray) o).toString(stringBuilder);
            } else {
                stringBuilder.append(o.toString());
            }
        }
        stringBuilder.append("]");
    }

    public void put(final Object o) {
        if (o == null) throw new JSONException("putting null in json-array. if intended, use JSONObject.NULL instead");
        if (!(o instanceof String || o instanceof Integer || o instanceof Double ||
                o instanceof Boolean || o instanceof JSONArray
                || o instanceof JSONObject || o == NULL)) {
            throw new JSONException("unknown type to put in json-array: " + o.getClass());
        }
        this.objectList.add(o);
    }

    public JSONArray getJSONArray(final int i) {
        return get(i);
    }

    public JSONObject getJSONObject(final int i) {
        return get(i);
    }

    public String getString(final int i) {
        final Object o = objectList.get(i);
        if (o instanceof String) return (String) o;
        final Escapable escapable = (Escapable) o;
        return escapable.getContentUnescaped(escaper);
    }

    public boolean getBoolean(final int i) {
        return get(i);
    }

    public int getInteger(final int i) {
        if (i >= objectList.size() || i < 0) return 0;
        return (int) objectList.get(i);
    }

    public double getDouble(final int i) {
        if (i >= objectList.size() || i < 0) return 0;
        return (double) objectList.get(i);
    }

    public <T> T get(final int i) {
        if (i >= objectList.size() || i < 0) return null;
        Object o = objectList.get(i);
        if (o == null || o == NULL) return null;
        if (o instanceof Escapable) {
            o = ((Escapable) o).getContentUnescaped(escaper);
        }
        @SuppressWarnings("unchecked") final T casted = (T) o;
        return casted;
    }
}

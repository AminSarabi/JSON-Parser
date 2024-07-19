package ir.reyminsoft.JSON;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

public class JSONArray {
    private static final Escaper escaper;



    static {
        escaper = new ArrayEscaper();
    }

    private final List<Object> objectList;

    public JSONArray() {
        this.objectList = new ArrayList<>();
    }

    JSONArray(List<Object> objectList) {
        this.objectList = objectList;
    }

    public JSONArray(String string) {
        this.objectList = readArray(new Cursor(string));
    }

    static List<Object> readArray(Cursor cursor) {
        int beginIndex = cursor.currentIndex();
        List<Object> list = new ArrayList<>();
        while (cursor.hasNextChar()) {
            char ch = cursor.currentCharacter();
            switch (ch) {
                case ',':
                    break;
                case '[':
                    if (cursor.currentIndex() != beginIndex)
                        list.add(new JSONArray(readArray(cursor)));
                    break;
                case ']':
                    if (beginIndex != 0) return list;
                    break;
                case '{':
                    list.add(new JSONObject(JSONObject.readObject(cursor)));
                    break;
                case '"':
                    list.add(JSONObject.readStringValue(cursor.increment()));
                    break;

                case 't':
                case 'T':
                case 'f':
                case 'F':
                    list.add(JSONObject.readBoolean(cursor));
                    break;
                case 'n':
                case 'N':
                    if (!cursor.hasNextChars(4)) {
                        throw new JSONException("unexpected end of stream at " + cursor);
                    }
                    String str = cursor.getRangeAsString(4);
                    if (str.equalsIgnoreCase("null")) {
                        list.add(JSONObject.NULL);
                    } else {
                        throw new JSONException("unrecognized value " + str);
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
                    list.add(readNumeric(cursor));
                    break;
            }
            cursor.increment();
        }


        return list;
    }

    private static Object readNumeric(Cursor cursor) {
        int beginIndex = cursor.currentIndex();
        int endIndex = -1;
        while (cursor.hasNextChar()) {
            char ch = cursor.currentCharacter();
            if (Character.isWhitespace(ch)) {
                endIndex = cursor.currentIndex();
                break;
            } else if (ch == ',' || ch == ']') {
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

    @Override
    public String toString() { //todo if the content is not modified, use a cached string (weak reference)
        StringBuilder stringBuilder = new StringBuilder();
        toString(stringBuilder);
        String str = stringBuilder.toString();
        return str;
    }

    void toString(StringBuilder stringBuilder) {
        stringBuilder.append("[");
        boolean first = true;
        for (Object o : objectList) {
            if (!first) {
                stringBuilder.append(',');
            } else {
                first = false;
            }
            if (o instanceof Escapable escapable) {
                stringBuilder.append('"').append(escapable.getContentEscaped(escaper)).append('"');
            } else if (o instanceof String) {
                stringBuilder.append('"').append(escaper.escape((String) o)).append('"');
            } else if (o == JSONObject.NULL) {
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

    public void put(Object o) {
        if (o == null) throw new JSONException("putting null in json-array. if intended, use JSONObject.NULL instead");
        if (!(o instanceof String || o instanceof Integer || o instanceof Double ||
                o instanceof Boolean || o instanceof JSONArray
                || o instanceof JSONObject || o == JSONObject.NULL)) {
            throw new JSONException("unknown type to put in json-array: " + o.getClass());
        }
        this.objectList.add(o);
    }

    public JSONArray getJSONArray(int i) {
        return get(i);
    }

    public JSONObject getJSONObject(int i) {
        return get(i);
    }

    public String getString(int i) {
        Object o = objectList.get(i);
        if (o instanceof String) return (String) o;
        Escapable escapable = (Escapable) o;
        return escapable.getContentUnescaped(escaper);
    }

    public boolean getBoolean(int i) {
        return get(i);
    }

    public int getInteger(int i) {
        if (i >= objectList.size() || i < 0) return 0;
        return get(i);
    }

    public double getDouble(int i) {
        Object o = objectList.get(i);
        return (double) o;
    }

    public <T> T get(int i) {
        if (i >= objectList.size() || i < 0) return null;
        Object o = objectList.get(i);
        if (o == null || o == JSONObject.NULL) return null;
        if (o instanceof Escapable) {
            return (T) ((Escapable) o).getContentUnescaped(escaper);
        }
        return (T) o;
    }
}

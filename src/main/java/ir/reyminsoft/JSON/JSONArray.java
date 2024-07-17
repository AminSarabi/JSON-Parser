package ir.reyminsoft.JSON;

import java.util.ArrayList;
import java.util.List;

public class JSONArray {
    private final List<Object> objectList;

    private static final Escaper escaper;

    static {
        escaper = new Escaper('\\');
        escaper.addCharToEscape('[', ']', '\"', ',', ':', '\t', '\b', '\t', '\f', '\n', '\r');
    }

    public JSONArray(List<Object> objectList) {
        this.objectList = objectList;
    }

    public JSONArray(String string) {
        char[] ch = string.toCharArray();
        this.objectList = readArray(new Cursor(ch));
    }

    public static List<Object> readArray(Cursor cursor) {
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

    public static Object readNumeric(Cursor cursor) {
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
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder("[");
        boolean first = true;
        for (Object o : objectList) {
            if (!first) {
                stringBuilder.append(',');
            } else {
                first = false;
            }
            if (o instanceof String) {
                stringBuilder.append('"').append(escaper.escape((String) o)).append('"');
            } else if (o == JSONObject.NULL) {
                stringBuilder.append("null");
            } else {
                stringBuilder.append(o.toString());
            }
        }
        return stringBuilder.append("]").toString();
    }

    public JSONArray getJSONArray(int i) {
        Object o = objectList.get(i);
        return (JSONArray) o;
    }

    public String getString(int i) {
        Object o = objectList.get(i);
        return (String) o;
    }

    public int getInteger(int i) {
        Object o = objectList.get(i);
        return (int) o;
    }

    public double getDouble(int i) {
        Object o = objectList.get(i);
        return (double) o;
    }
}
